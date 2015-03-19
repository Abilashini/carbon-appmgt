/*
 *
 *   Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.carbon.appmgt.services.v1.apps.mobile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.services.v1.apps.App;
import org.wso2.carbon.appmgt.services.v1.apps.AppListQuery;
import org.wso2.carbon.appmgt.services.v1.apps.AppListResponse;
import org.wso2.carbon.appmgt.services.v1.apps.AppService;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.core.session.UserRegistry;

import javax.ws.rs.*;
import java.util.ArrayList;

@Produces({ "application/json"})
@Consumes({ "application/json"})
public class MobileAppService implements AppService {

        private static final Log log = LogFactory.getLog(MobileAppService.class);

        @GET
        @Path("list/tenant/{tenantDomain}")
        public AppListResponse getApplicationList(@PathParam("tenantDomain") String tenantDomain, @QueryParam("limit") int limit, @QueryParam("offset") int offset){

            boolean noLimit = false;

            int pageIndex = 0;
            int index = 0;
            int found = 0;

            if(tenantDomain == null ) tenantDomain = "carbon.super";
            if(limit == 0) noLimit = true;

            log.debug("getApplicationList: Tenant domain is " + tenantDomain);

            AppListResponse response= new AppListResponse();

            try {
                PrivilegedCarbonContext.startTenantFlow();
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain);
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm().getRealmConfiguration().getAdminUserName());

                CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext();
                Registry registry = cCtx.getRegistry(RegistryType.USER_GOVERNANCE);

                GovernanceUtils.loadGovernanceArtifacts((UserRegistry) registry);
                GenericArtifactManager artifactManager = new GenericArtifactManager((UserRegistry)registry, "mobileapp");
                GenericArtifact[] artifacts = artifactManager.getAllGenericArtifactsByLifecycleStatus("MobileAppLifeCycle", "Published");

                response.setApps(new ArrayList<App>());

                for(GenericArtifact artifact : artifacts){

                    //  Pagination Logic
                    if(offset > index++){
                        continue;
                    }
                    if(!noLimit) {
                        if(pageIndex == limit){
                            break;
                        }
                    }
                    found = ++pageIndex;

                    response.getApps().add(MobileAppDataLoader.load(new MobileApp(), artifact));
                }

                AppListQuery appListQuery = new AppListQuery();
                appListQuery.setStatus("OK");
                appListQuery.setLimit(limit);
                appListQuery.setFound(found);
                appListQuery.setOffset(offset);
                appListQuery.setTotal(artifacts.length);
                response.setQuery(appListQuery);



            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                PrivilegedCarbonContext.endTenantFlow();
                return response;
            }

        }


}
