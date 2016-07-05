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

package org.wso2.carbon.appmgt.gateway.handlers.security.saml2;

import org.apache.axiom.om.OMElement;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.StatusResponseType;
import org.opensaml.saml2.core.impl.ResponseImpl;
import org.wso2.carbon.appmgt.api.model.AuthenticatedIDP;

import java.util.List;

/**
 * Represents the call back request from the IDP.
 */
public class IDPMessage {

    private RequestAbstractType samlRequest;
    private StatusResponseType samlResponse;
    private List<AuthenticatedIDP> authenticatedIDPs;
    private String relayState;
    private String rawSAMLResponse;
    private String rawSAMLRequest;

    public RequestAbstractType getSAMLRequest() {
        return samlRequest;
    }

    public void setSAMLRequest(RequestAbstractType samlRequest) {
        this.samlRequest = samlRequest;
    }

    public StatusResponseType getSAMLResponse() {
        return samlResponse;
    }

    public void setSAMLResponse(StatusResponseType samlResponse) {
        this.samlResponse = samlResponse;
    }

    public List<AuthenticatedIDP> getAuthenticatedIDPs() {
        return authenticatedIDPs;
    }

    public void setAuthenticatedIDPs(List<AuthenticatedIDP> authenticatedIDPs) {
        this.authenticatedIDPs = authenticatedIDPs;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRawSAMLResponse(String rawSAMLResponse) {
        this.rawSAMLResponse = rawSAMLResponse;
    }

    public String getRawSAMLResponse() {
        return rawSAMLResponse;
    }

    public String getRawSAMLRequest() {
        return rawSAMLRequest;
    }

    public void setRawSAMLRequest(String rawSAMLRequest) {
        this.rawSAMLRequest = rawSAMLRequest;
    }

    public boolean isSLOResponse() {
        return samlResponse != null && samlResponse instanceof LogoutResponse;
    }

    public boolean isSLORequest() {
        return samlRequest != null && samlRequest instanceof LogoutRequest;
    }
}
