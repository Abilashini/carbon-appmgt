/*
 * Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmgt.impl.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.carbon.appmgt.api.AppManagementException;
import org.wso2.carbon.appmgt.impl.AppMConstants;
import org.wso2.carbon.appmgt.impl.dto.UserRegistrationConfigDTO;
import org.wso2.carbon.appmgt.api.AppManagementException;
import org.wso2.carbon.appmgt.impl.AppMConstants;
import org.wso2.carbon.appmgt.impl.dto.UserRegistrationConfigDTO;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserRealm;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains the utility methods used for self signup
 */
public final class SelfSignUpUtil {

	private static final Log log = LogFactory.getLog(SelfSignUpUtil.class);

	/**
	 * retrieve self signup configuration from the cache. if cache mises, load
	 * to the cache from
	 * the registry and return configuration
	 * 
	 * @param tenantDomain
	 *            Domain name of the tenant
	 * @return UserRegistrationConfigDTO self signup configuration for the
	 *         tenant
	 * @throws org.wso2.carbon.appmgt.api.AppManagementException
	 */
	public static UserRegistrationConfigDTO getSignupConfiguration(String tenantDomain)
			throws AppManagementException {

		UserRegistrationConfigDTO config = null;

		String currentFlowDomain =
				PrivilegedCarbonContext.getThreadLocalCarbonContext()
				.getTenantDomain();
		boolean isTenantFlowStarted = false;
		try {

			/* start the correct tenant flow to load the tenant's registry*/
			if (tenantDomain != null &&
					!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				if (!currentFlowDomain.equals(tenantDomain)) {
					/* if the current flow is not the one related to the domain */
					isTenantFlowStarted = true;
					PrivilegedCarbonContext.startTenantFlow();
					PrivilegedCarbonContext.getThreadLocalCarbonContext()
					.setTenantDomain(tenantDomain, true);
				}
			}
			String cacheName = tenantDomain + "_" + AppMConstants.SELF_SIGN_UP_CONFIG_CACHE;
			Cache signupConfigCache =
					Caching.getCacheManager(AppMConstants.API_MANAGER_CACHE_MANAGER)
					.getCache(AppMConstants.SELF_SIGN_UP_CONFIG_CACHE);
			// load self signup configuration from the cache
			config = (UserRegistrationConfigDTO) signupConfigCache.get(cacheName);

			if (config == null) {
				// get the tenant's configuration from the registry and put it
				// in the cache
				 if (log.isDebugEnabled()) {
		             log.debug("Cache miss for " + cacheName );
		         }       
				config = getSignupConfigurationFromRegistry(tenantDomain);
				signupConfigCache.put(cacheName, config);
			}
		} finally {
			if (isTenantFlowStarted) {
				PrivilegedCarbonContext.endTenantFlow();
			}
		}	

		return config;
	}

	/**
	 * load configuration from the registry
	 * 
	 * @param tenantDomain
	 * @return
	 * @throws AppManagementException
	 */
	private static UserRegistrationConfigDTO getSignupConfigurationFromRegistry(String tenantDomain)
			throws AppManagementException {


		UserRegistrationConfigDTO config = null;
	
		try {

			Registry registry =
					(Registry) PrivilegedCarbonContext.getThreadLocalCarbonContext()
					.getRegistry(RegistryType.SYSTEM_GOVERNANCE);
			if (registry.resourceExists(AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION)) {
				Resource resource = registry.get(AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION);
				// build config from registry resource
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				String configXml = new String((byte[]) resource.getContent());
				InputSource configInputSource = new InputSource();
				configInputSource.setCharacterStream(new StringReader(configXml.trim()));
				Document doc = builder.parse(configInputSource);
				NodeList nodes = doc.getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROOT);
				if (nodes.getLength() > 0) {
					config = new UserRegistrationConfigDTO();
					config.setSignUpDomain(((Element) nodes.item(0)).getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_DOMAIN_ELEM)
					                       .item(0).getTextContent());
					// tenant admin info
					config.setAdminUserName(((Element) nodes.item(0)).getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_USERNAME)
					                        .item(0).getTextContent());
					config.setAdminPassword(((Element) nodes.item(0)).getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_PASSWORD)
					                        .item(0).getTextContent());

					config.setSignUpEnabled(Boolean.parseBoolean(((Element) nodes.item(0)).getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ENABLED)
					                                             .item(0)
					                                             .getTextContent()));
				
					// there can be more than one <SignUpRole> elements, iterate
					// through all elements

					Element roleListParent = (Element)((Element) nodes.item(0)).getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROLES_ELEM).item(0);

					NodeList rolesEl = roleListParent.getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROLE_ELEM);
					for (int i = 0; i < rolesEl.getLength(); i++) {
						Element tmpEl = (Element) rolesEl.item(i);
						String tmpRole =
								tmpEl.getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROLE_NAME_ELEMENT)
								.item(0).getTextContent();
						boolean tmpIsExternal =
								Boolean.parseBoolean(tmpEl.getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROLE_IS_EXTERNAL)
								                     .item(0).getTextContent());
						config.getRoles().put(tmpRole, tmpIsExternal);
					}
				}
			}
		} catch (RegistryException e) {
			throw new AppManagementException("Error while reading registry" +
					AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION, e);
		} catch (ParserConfigurationException e) {
			throw new AppManagementException("Error while parsing configuration" +
					AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION, e);
		} catch (SAXException e) {
			throw new AppManagementException("Error while parsing configuration" +
					AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION, e);
		} catch (IOException e) {
			throw new AppManagementException("Error while parsing configuration" +
					AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION, e);
		}
		return config;
	}

	/**
	 * Check whether user can signup to the tenant domain
	 * 
	 * @param userName
	 * @param realm
	 * @return
	 * @throws AppManagementException
	 */
	public static boolean isUserNameWithAllowedDomainName(String userName, UserRealm realm)
			throws AppManagementException {
		int index;
		index = userName.indexOf('/');

		// Check whether we have a secondary UserStoreManager setup.
		if (index > 0) {
			// Using the short-circuit. User name comes with the domain name.
			try {
				return !realm.getRealmConfiguration()
						.isRestrictedDomainForSlefSignUp(userName.substring(0, index));
			} catch (UserStoreException e) {
				throw new AppManagementException(e.getMessage(), e);				
			}
		}

		return true;
	}

	/**
	 * get the full role name list (ex: internal/subscriber)
	 * 
	 * @param config
	 * @return
	 */
	public static List<String> getRoleNames(UserRegistrationConfigDTO config) {

		ArrayList<String> roleNamesArr = new ArrayList<String>();
		Map<String, Boolean> roles = config.getRoles();
		for (Map.Entry<String, Boolean> entry : roles.entrySet()) {
			String roleName;
			if (entry.getValue()) {
				// external role
				roleName =
						config.getSignUpDomain().toUpperCase() +
						UserCoreConstants.DOMAIN_SEPARATOR + entry.getKey();
			} else {
				// internal role
				roleName =
						UserCoreConstants.INTERNAL_DOMAIN + UserCoreConstants.DOMAIN_SEPARATOR +
						entry.getKey();
			}
			roleNamesArr.add(roleName);
		}
		return roleNamesArr;

	}

	/**
	 * modify user name with user storeage information. 
	 * @param username
	 * @param signupConfig
	 * @return
	 */
	public static String getDomainSpecificUserName(String username, UserRegistrationConfigDTO signupConfig) {
		String modifiedUsername = null;	
		// set tenant specific sign up user storage
		if (signupConfig != null && signupConfig.getSignUpDomain() != "") {
			
			int index = username.indexOf(UserCoreConstants.DOMAIN_SEPARATOR);
			/*
			 * if there is a different domain provided by the user other than one 
			 * given in the configuration, add the correct signup domain. Here signup
			 * domain refers to the user storage
			 */
		
			if (index > 0) {
				modifiedUsername =
						signupConfig.getSignUpDomain().toUpperCase() +
						UserCoreConstants.DOMAIN_SEPARATOR +
						username.substring(index + 1);
			} else {
				modifiedUsername =
						signupConfig.getSignUpDomain().toUpperCase() +
						UserCoreConstants.DOMAIN_SEPARATOR + username;
			}
		}
		
		return modifiedUsername;
	}
}
