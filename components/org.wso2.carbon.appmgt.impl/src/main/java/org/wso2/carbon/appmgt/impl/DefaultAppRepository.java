package org.wso2.carbon.appmgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
<<<<<<< HEAD
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.appmgt.api.AppManagementException;
import org.wso2.carbon.appmgt.api.model.*;
import org.wso2.carbon.appmgt.impl.dao.AppMDAO;
import org.wso2.carbon.appmgt.impl.service.ServiceReferenceHolder;
import org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
=======
import org.wso2.carbon.appmgt.api.model.App;
import org.wso2.carbon.appmgt.api.model.EntitlementPolicyGroup;
import org.wso2.carbon.appmgt.api.model.WebApp;
import org.wso2.carbon.appmgt.impl.service.ServiceReferenceHolder;
import org.wso2.carbon.appmgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
>>>>>>> 7bf2aafdbe09fab421bd8a3f42f6152bddc63ff5
import org.wso2.carbon.registry.api.RegistryService;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.xml.namespace.QName;
import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The default implementation of DefaultAppRepository which uses RDBMS and Carbon registry for persistence.
 */
public class DefaultAppRepository implements AppRepository {

    private static final Log log = LogFactory.getLog(DefaultAppRepository.class);

    private static final String POLICY_GROUP_TABLE_NAME = "APM_POLICY_GROUP";

    private RegistryService registryService;
    private Registry registry;
    private int tenantId;
    private String tenantDomain;
    private String username;
    private Log log = LogFactory.getLog(getClass());

    public DefaultAppRepository() throws AppManagementException {
        try {
            String loggedInUsername = CarbonContext.getThreadLocalCarbonContext().getUsername();

            this.tenantDomain = MultitenantUtils.getTenantDomain(username);
            this.username = MultitenantUtils.getTenantAwareUsername(loggedInUsername);
            ;
            this.tenantId = ServiceReferenceHolder.getInstance().getRealmService().getTenantManager().getTenantId(username);
            AppManagerUtil.loadTenantRegistry(tenantId);
            this.registry = ServiceReferenceHolder.getInstance().
                    getRegistryService().getGovernanceUserRegistry(username, tenantId);

        } catch (RegistryException e) {
            handleException("Error while instantiating Registry for user : " + username, e);
        } catch (UserStoreException e) {
            handleException("Error while obtaining user registry for user:" + username, e);
        }
    }

    @Override
    public String saveApp(App app) {

        if(AppMConstants.WEBAPP_ASSET_TYPE.equals(app.getType())){

            WebApp webApp = (WebApp) app;

            Connection connection = null;
            try {
                connection = getRDBMSConnectionWithoutAutoCommit();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                savePolicyGroups(webApp, connection);
                saveRegistryArtifact(app);
                saveAppToRDMS(webApp, connection);
                saveServiceProvider(webApp, connection);
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error(String.format("Couldn't rollback save operation for the app %s:%s", app.getType(), app.getDisplayName()));
                }
            }

        }


        return null;
    }

    private void savePolicyGroups(WebApp app, Connection connection) throws SQLException {

        for(EntitlementPolicyGroup policyGroup : app.getAccessPolicyGroups()){
            savePolicyGroup(policyGroup, connection);
        }

    }

    private void savePolicyGroup(EntitlementPolicyGroup policyGroup, Connection connection) throws SQLException {

            String query = String.format("INSERT INTO %s(NAME,THROTTLING_TIER,USER_ROLES,URL_ALLOW_ANONYMOUS,DESCRIPTION) VALUES(?,?,?,?,?) ",POLICY_GROUP_TABLE_NAME);

            PreparedStatement preparedStatement = null;

            ResultSet resultSet = null;

            try {

                preparedStatement = connection.prepareStatement(query, new String[]{"POLICY_GRP_ID"});
                preparedStatement.setString(1, policyGroup.getPolicyGroupName());
                preparedStatement.setString(2, policyGroup.getThrottlingTier());
                preparedStatement.setString(3, policyGroup.getUserRoles());
                preparedStatement.setBoolean(4, policyGroup.isAllowAnonymous());
                preparedStatement.setString(5, policyGroup.getPolicyDescription());
                preparedStatement.executeUpdate();

                resultSet = preparedStatement.getGeneratedKeys();

                int generatedPolicyGroupId = 0;
                if (resultSet.next()) {
                    generatedPolicyGroupId = Integer.parseInt(resultSet.getString(1));
                    policyGroup.setPolicyGroupId(generatedPolicyGroupId);
                }

                saveEntitlementPolicyMappings(policyGroup, connection);
            } finally {
                closeResultSet(resultSet);
            }

    }

    private void closeResultSet(ResultSet resultSet) {
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException ignore) {}
        }
    }

    private void saveEntitlementPolicyMappings(EntitlementPolicyGroup policyGroup, Connection connection) {

    }

    private String saveRegistryArtifact(App app){
        String appUUID = null;
        if(AppMConstants.WEBAPP_ASSET_TYPE.equals(app.getType())){
           // appUUID = saveWebAppRegistryArtifact((WebApp) app);
        }
        return null;
    }

    private long saveAppToRDMS(WebApp app, Connection connection){
        return -1;
    }

    private void saveServiceProvider(WebApp app, Connection connection){

    }

    private Connection getRDBMSConnectionWithoutAutoCommit() throws SQLException {
        return getRDBMSConnection(false);
    }

    private Connection getRDBMSConnectionWithAutoCommit() throws SQLException {
        return getRDBMSConnection(true);
    }

    private Connection getRDBMSConnection(boolean setAutoCommit) throws SQLException {

        Connection connection = APIMgtDBUtil.getConnection();
        connection.setAutoCommit(setAutoCommit);

        return connection;
    }

    private String saveWebAppRegistryArtifact(WebApp webApp) throws AppManagementException {
        String artifactId = null;

        GenericArtifactManager artifactManager = null;
        try {
            final String webAppName = webApp.getId().getApiName();
            Map<String, List<String>> attributeListMap = new HashMap<String, List<String>>();

            artifactManager = AppManagerUtil.getArtifactManager(registry,
                    AppMConstants.WEBAPP_ASSET_TYPE);
            registry.beginTransaction();
            GenericArtifact genericArtifact =
                    artifactManager.newGovernanceArtifact(new QName(webApp.getId().getApiName()));
            GenericArtifact artifact = AppManagerUtil.createWebAppArtifactContent(genericArtifact, webApp);
            artifactManager.addGenericArtifact(artifact);
            artifactId = artifact.getId();
            String artifactPath = GovernanceUtils.getArtifactPath(registry, artifact.getId());

            Set<String> tagSet = webApp.getTags();
            if (tagSet != null) {
                for (String tag : tagSet) {
                    registry.applyTag(artifactPath, tag);
                }
            }
            if (webApp.getAppVisibility() != null) {
                AppManagerUtil.setResourcePermissions(webApp.getId().getProviderName(),
                        AppMConstants.API_RESTRICTED_VISIBILITY, webApp.getAppVisibility(), artifactPath);
            }
            String providerPath = AppManagerUtil.getAPIProviderPath(webApp.getId());
            //provider ------provides----> WebApp
            registry.addAssociation(providerPath, artifactPath, AppMConstants.PROVIDER_ASSOCIATION);
            registry.commitTransaction();
        } catch (RegistryException e) {
            try {
                registry.rollbackTransaction();
            } catch (RegistryException re) {
                handleException(
                        "Error while rolling back the transaction for web application: "
                                + webApp.getId().getApiName(), re);
            }
            handleException("Error occurred while creating the web application : " + webApp.getId().getApiName(), e);
        }
        return artifactId;

    }

    private String saveMobileAppRegistryArtifact(MobileApp mobileApp){
        return null;

    }
    protected void handleException(String msg, Exception e) throws AppManagementException {
        log.error(msg, e);
        throw new AppManagementException(msg, e);
    }

    protected void handleException(String msg) throws AppManagementException {
        log.error(msg);
        throw new AppManagementException(msg);
    }

}
