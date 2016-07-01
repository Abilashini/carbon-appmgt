/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   you may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.wso2.carbon.appmgt.gateway.handlers.security;


import org.wso2.carbon.appmgt.gateway.handlers.security.authentication.AuthenticationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a gateway user session.
 */
public class Session {


    private String uuid;
    private boolean isNew;
    private AuthenticationContext authenticationContext;
    private String requestedURL;
    private Map<String, Object> attributes;

    public Session() {
        uuid = UUID.randomUUID().toString();
        authenticationContext = new AuthenticationContext();
        attributes = new HashMap<String, Object>();
        isNew = true;
    }

    public String getUuid() {
        return uuid;
    }

    public AuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    public void setAuthenticationContext(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public String getRequestedURL() {
        return requestedURL;
    }

    public void setRequestedURL(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    public void addAttribute(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew){
        this.isNew = isNew;
    }
}
