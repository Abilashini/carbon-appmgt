/*
*  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.appmgt.sample.deployer.appm;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.wso2.carbon.appmgt.sample.deployer.appm.LoginAdminServiceClient;
import org.wso2.carbon.appmgt.sample.deployer.configuration.Configuration;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.user.mgt.stub.UserAdminStub;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.carbon.user.mgt.stub.types.carbon.ClaimValue;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.rmi.RemoteException;

/**
 * This class is use as a client of UserAdminService
 *
 * */
public class UserAdminServiceClient {

    private static final String appmHome = CarbonUtils.getCarbonHome();

    private static final String axis2Repo = appmHome + File.separator + "repository" +
            File.separator + "deployment" + File.separator + "client";
    private static final String axis2Conf =
            ServerConfiguration.getInstance().getFirstProperty("Axis2Config.clientAxis2XmlLocation");
    private UserAdminStub userAdminStub;

    /**
     * Creates a new UserAdminServiceClient object and initialising
     * the UserAdminStub
     *
     * @throws RemoteException
     *             Throws this when UserAdminStub failed initialise
     *
     * @throws LoginAuthenticationExceptionException
     *             Throws this when authentication failed
     */
    public UserAdminServiceClient() throws RemoteException, LoginAuthenticationExceptionException {
        String backEndUrl = Configuration.getHttpsUrl();
        ConfigurationContext configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                axis2Repo, axis2Conf);
        userAdminStub = new UserAdminStub(configContext, backEndUrl + "/services/UserAdmin");
        LoginAdminServiceClient loginAdminServiceClient = new LoginAdminServiceClient(backEndUrl);
        String session = loginAdminServiceClient.authenticate(Configuration.getUserName()
                , Configuration.getPassword());
        Options option;
        ServiceClient serviceClient;
        serviceClient = userAdminStub._getServiceClient();
        option = serviceClient.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, session);
    }

    /**
     * This method is use to add a user
     *
     * @param userName
     *             User name
     *
     * @throws java.rmi.RemoteException
     *             Throws this when user admin service failed to connect
     *
     * @throws UserAdminUserAdminException
     *             Throws this when user failed to register
     *
     * */
    public void addUser(String userName) throws RemoteException, UserAdminUserAdminException {
        userAdminStub.addUser(userName, "subscriber",
                new String[]{"Internal/subscriber"}, new ClaimValue[]{}, "default");
    }
}
