/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
*/

package org.wso2.carbon.appmgt.gateway.internal;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ServerContextInformation;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.config.SynapseConfigurationBuilder;
import org.apache.synapse.config.xml.MultiXMLConfigurationBuilder;
import org.apache.synapse.config.xml.MultiXMLConfigurationSerializer;
import org.apache.synapse.config.xml.SequenceMediatorFactory;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.registry.Registry;
import org.wso2.carbon.appmgt.api.AppManagementException;
import org.wso2.carbon.appmgt.impl.AppMConstants;
import org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.mediation.initializer.configurations.ConfigurationInitilizerException;
import org.wso2.carbon.mediation.initializer.configurations.ConfigurationManager;
import org.wso2.carbon.mediation.registry.WSO2Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.utils.AbstractAxis2ConfigurationContextObserver;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This creates the {@link org.apache.synapse.config.SynapseConfiguration}
 * for the respective tenants. This class specifically add to deploy WebApp Manager
 * related synapse sequences. This class used to deploy resource mismatch handler, auth failure handler,
 * sandbox error handler, throttle out handler, build sequence, main sequence and fault sequence into tenant
 * synapse artifact space.
 */
public class TenantLoadGatewayObserver extends AbstractAxis2ConfigurationContextObserver {
    private static final Log log = LogFactory.getLog(TenantLoadGatewayObserver.class);
    private String resourceMisMatchSequenceName = "_resource_mismatch_handler_";
    private String authFailureHandlerSequenceName = "_auth_failure_handler_";
    private String sandboxKeyErrorSequenceName = "_sandbox_key_error_";
    private String productionKeyErrorSequenceName = "_production_key_error_";
    private String throttleOutSequenceName = "_throttle_out_handler_";
    private String buildSequenceName = "_build_";
    private String faultSequenceName = "fault";
    private String mainSequenceName = "main";
    private String saml2SequenceName = "saml2_sequence";
    private String synapseConfigRootPath = CarbonBaseUtils.getCarbonHome() +
            AppMConstants.SYNAPSE_CONFIG_RESOURCES_PATH;
    private SequenceMediator authFailureHandlerSequence = null;
    private SequenceMediator resourceMisMatchSequence = null;
    private SequenceMediator throttleOutSequence = null;
    private SequenceMediator sandboxKeyErrorSequence = null;
    private SequenceMediator productionKeyErrorSequence = null;


    public void createdConfigurationContext(ConfigurationContext configurationContext) {
        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
    	int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            // first check which configuration should be active
            org.wso2.carbon.registry.core.Registry registry =
                    (org.wso2.carbon.registry.core.Registry) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                            .getRegistry(RegistryType.SYSTEM_CONFIGURATION);

            AxisConfiguration axisConfig = configurationContext.getAxisConfiguration();

            // initialize the lock
            Lock lock = new ReentrantLock();
            axisConfig.addParameter("synapse.config.lock", lock);

            // creates the synapse configuration directory hierarchy if not exists
            // useful at the initial tenant creation
            File tenantAxis2Repo = new File(configurationContext.getAxisConfiguration().getRepository().getFile());
            File synapseConfigsDir = new File(tenantAxis2Repo, "synapse-configs");
            if (!synapseConfigsDir.exists()) {
                if (!synapseConfigsDir.mkdir()) {
                    log.fatal("Couldn't create the synapse-config root on the file system for the tenant domain : "
                                      + tenantDomain);
                    return;
                }
            }

            String synapseConfigsDirLocation = synapseConfigsDir.getAbsolutePath();
            // set the required configuration parameters to initialize the ESB
            axisConfig.addParameter(SynapseConstants.Axis2Param.SYNAPSE_CONFIG_LOCATION, synapseConfigsDirLocation);

            // init the multiple configuration tracker
            ConfigurationManager manger = new ConfigurationManager((UserRegistry) registry, configurationContext);
            manger.init();

            File synapseConfigDir = new File(synapseConfigsDir, manger.getTracker().getCurrentConfigurationName());
            File buildSequenceFile = new File(synapseConfigsDir + "/" + manger.getTracker().getCurrentConfigurationName() +
                    "/" + MultiXMLConfigurationBuilder.SEQUENCES_DIR + "/" + buildSequenceName + ".xml");
            //Here we will check build sequence exist in synapse artifact. If it is not available we will create
            //sequence synapse configurations by using resource artifacts
            if (!buildSequenceFile.exists()) {
                createTenantSynapseConfigHierarchy(synapseConfigDir, tenantDomain);
            }
        } catch (AxisFault e) {
             log.error("Failed to create Tenant's synapse sequences for tenant " + tenantDomain);
        } catch (ConfigurationInitilizerException e) {
            log.error("Failed to create Tenant's synapse sequences for tenant. ");
        }

        try{
            AppManagerUtil.loadTenantAPIPolicy(tenantDomain, tenantId);
        }catch (AppManagementException e){
            log.error("Failed to load tiers.xml to tenant's registry");
        }
    }


    private ServerContextInformation initESB(String configurationName, ConfigurationContext configurationContext)
            throws AxisFault {
        return null;
    }

    /**
     * Create the file system for holding the synapse configuration for a new tenant.
     *
     * @param synapseConfigDir configuration directory where synapse configuration is created
     * @param tenantDomain     name of the tenant
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void createTenantSynapseConfigHierarchy(File synapseConfigDir, String tenantDomain) {
        synapseConfigDir.mkdir();
        File sequencesDir = new File(synapseConfigDir, MultiXMLConfigurationBuilder.SEQUENCES_DIR);

        if (!sequencesDir.mkdir()) {
            log.warn("Could not create synapse configuration for tenant" + tenantDomain);
        }

        SynapseConfiguration initialSynapseConfig = SynapseConfigurationBuilder.getDefaultConfiguration();
        try {
            if (authFailureHandlerSequence == null) {
                addSequenceMediatorName(authFailureHandlerSequence, authFailureHandlerSequenceName);
            }
            if (resourceMisMatchSequence == null) {
                addSequenceMediatorName(resourceMisMatchSequence, resourceMisMatchSequenceName);
            }
            if (throttleOutSequence == null) {
                addSequenceMediatorName(throttleOutSequence, throttleOutSequenceName);
            }
            if (sandboxKeyErrorSequence == null) {
                addSequenceMediatorName(sandboxKeyErrorSequence, sandboxKeyErrorSequenceName);
            }
            if (productionKeyErrorSequence == null) {
                addSequenceMediatorName(productionKeyErrorSequence, productionKeyErrorSequenceName);
            }
            FileUtils.copyFile(new File(synapseConfigRootPath + mainSequenceName + ".xml"),
                    new File(synapseConfigDir.getAbsolutePath() + File.separator + "sequences" + File.separator + mainSequenceName + ".xml"));

            FileUtils.copyFile(new File(synapseConfigRootPath + faultSequenceName + ".xml"),
                    new File(synapseConfigDir.getAbsolutePath() + File.separator + "sequences" + File.separator + faultSequenceName + ".xml"));
            FileUtils.copyFile(new File(synapseConfigRootPath + saml2SequenceName + ".xml"),
                    new File(synapseConfigDir.getAbsolutePath() + File.separator + "sequences" + File.separator + saml2SequenceName + ".xml"));

        } catch (IOException e) {                                                             
            log.error("Error while reading WebApp manager specific synapse sequences" + e);
        }

        Registry registry = new WSO2Registry();
        initialSynapseConfig.setRegistry(registry);
        MultiXMLConfigurationSerializer serializer = new MultiXMLConfigurationSerializer(synapseConfigDir
                                                                                                 .getAbsolutePath());
        try {
            serializer.serializeSequence(authFailureHandlerSequence, initialSynapseConfig, null);
            serializer.serializeSequence(sandboxKeyErrorSequence, initialSynapseConfig, null);
            serializer.serializeSequence(productionKeyErrorSequence, initialSynapseConfig, null);
            serializer.serializeSequence(throttleOutSequence, initialSynapseConfig, null);
            serializer.serializeSequence(resourceMisMatchSequence, initialSynapseConfig, null);
            serializer.serializeSynapseRegistry(registry, initialSynapseConfig, null);
        } catch (Exception e) {
            handleException("Couldn't serialise the initial synapse configuration for the domain : " + tenantDomain, e);
        }
    }

    private void addSequenceMediatorName(SequenceMediator sequenceMediator, String sequenceName) {
        InputStream in = null;
        try {
            in = FileUtils.openInputStream(new File(synapseConfigRootPath + sequenceName + ".xml"));
            StAXOMBuilder builder = new StAXOMBuilder(in);
            SequenceMediatorFactory factory = new SequenceMediatorFactory();
            sequenceMediator = (SequenceMediator) factory.createMediator(builder.getDocumentElement(), new Properties());
            sequenceMediator.setFileName(sequenceName + ".xml");

        } catch (IOException e) {
            log.error("Error while reading WebApp manager specific synapse sequences" + e);
        } catch (XMLStreamException e) {
            log.error("Error while parsing WebApp manager specific synapse sequences" + e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static boolean isRunningSamplesMode() {
        return true;
    }

    private void handleException(String message, Exception e) {
        log.error(message, e);
    }
}
