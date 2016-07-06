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

package org.wso2.carbon.appmgt.gateway.utils;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.Constants;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2Sender;
import org.apache.synapse.rest.RESTConstants;
import org.apache.synapse.transport.nhttp.NhttpConstants;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.RequestAbstractType;
import org.wso2.carbon.appmgt.api.model.URITemplate;
import org.wso2.carbon.appmgt.api.model.WebApp;
import org.wso2.carbon.appmgt.gateway.handlers.security.Session;
import org.wso2.carbon.appmgt.gateway.handlers.security.SessionStore;
import org.wso2.carbon.appmgt.gateway.handlers.security.saml2.SAMLException;
import org.wso2.carbon.appmgt.gateway.handlers.security.saml2.SAMLUtils;
import org.wso2.carbon.appmgt.gateway.internal.ServiceReferenceHolder;
import org.wso2.carbon.appmgt.impl.AppMConstants;
import org.wso2.carbon.appmgt.impl.utils.UrlPatternMatcher;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class GatewayUtils {

    private static Log log = LogFactory.getLog(GatewayUtils.class);

    public static String getIDPUrl() {
        return ServiceReferenceHolder.getInstance().getAPIManagerConfiguration().
                getFirstProperty(AppMConstants.SSO_CONFIGURATION_IDENTITY_PROVIDER_URL);
    }

    public static String getAppRootURL(MessageContext messageContext) throws MalformedURLException {

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        String servicePrefix = axis2MessageContext.getProperty("SERVICE_PREFIX").toString();

        String webAppContext = (String) messageContext.getProperty(RESTConstants.REST_API_CONTEXT);
        String webAppVersion = (String) messageContext.getProperty(RESTConstants.SYNAPSE_REST_API_VERSION);

        URL serverRootURL = new URL(servicePrefix);
        URL appRootURL = new URL(serverRootURL, String.format("%s/%s/", webAppContext, webAppVersion));
        return appRootURL.toString();
    }

    public static boolean isAnonymousAccessAllowed(WebApp webApp, URITemplate uriTemplate) {
        return webApp.getAllowAnonymous() || uriTemplate.getPolicyGroup().isAllowAnonymous();
    }

    public static void logAndThrowException(Log log, String errorMessage, Exception e) {

        if(e == null){
            log.error(errorMessage);
            throw new SynapseException(errorMessage);
        }else {
            log.error(errorMessage, e);
            throw new SynapseException(errorMessage, e);
        }
    }

    public static void send401(MessageContext messageContext, String reason) {

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("http://wso2.org/appm", "appm");
        OMElement payload = fac.createOMElement("fault", ns);

        OMElement errorMessage = fac.createOMElement("message", ns);
        errorMessage.setText(reason);

        payload.addChild(errorMessage);

        OMElement firstChild = messageContext.getEnvelope().getBody().getFirstElement();
        if (firstChild != null) {
            firstChild.insertSiblingAfter(payload);
            firstChild.detach();
        } else {
            messageContext.getEnvelope().getBody().addChild(payload);
        }

        axis2MessageContext.setProperty(NhttpConstants.HTTP_SC, HttpStatus.SC_UNAUTHORIZED);
        messageContext.setResponse(true);
        messageContext.setProperty("RESPONSE", "true");
        messageContext.setTo(null);
        axis2MessageContext.removeProperty("NO_ENTITY_BODY");

        axis2MessageContext.setProperty(Constants.Configuration.MESSAGE_TYPE, "application/xml");
        axis2MessageContext.removeProperty(Constants.Configuration.CONTENT_TYPE);

        Axis2Sender.sendBack(messageContext);
    }

    public static void send200(MessageContext messageContext, OMElement payload) {

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        OMElement firstChild = messageContext.getEnvelope().getBody().getFirstElement();
        if (firstChild != null) {
            firstChild.insertSiblingAfter(payload);
            firstChild.detach();
        } else {
            messageContext.getEnvelope().getBody().addChild(payload);
        }

        axis2MessageContext.setProperty(NhttpConstants.HTTP_SC, HttpStatus.SC_OK);
        messageContext.setResponse(true);
        messageContext.setProperty("RESPONSE", "true");
        messageContext.setTo(null);
        axis2MessageContext.removeProperty("NO_ENTITY_BODY");

        axis2MessageContext.setProperty(Constants.Configuration.MESSAGE_TYPE, "application/xml");
        axis2MessageContext.removeProperty(Constants.Configuration.CONTENT_TYPE);

        Axis2Sender.sendBack(messageContext);
    }


    public static boolean shouldSkipSecurity(MessageContext messageContext) {

        Boolean shouldSkipSecurity = (Boolean) messageContext.getProperty(AppMConstants.MESSAGE_CONTEXT_PROPERTY_GATEWAY_SKIP_SECURITY);

        if(shouldSkipSecurity != null){
            return shouldSkipSecurity.booleanValue();
        }else{
            return false;
        }
    }

    public static Session getSession(MessageContext messageContext) {
        return getSession(messageContext, false);
    }

    public static Session getSession(MessageContext messageContext, boolean createIfNotExists) {

        String sessionID = (String) messageContext.getProperty(AppMConstants.APPM_SAML2_COOKIE);
        Session session = SessionStore.getInstance().getSession(sessionID, createIfNotExists);

        if(createIfNotExists){
            messageContext.setProperty(AppMConstants.APPM_SAML2_COOKIE, session.getUuid());
        }

        return session;
    }

    public static URITemplate findMatchedURITemplate(WebApp webApp, String httpVerb, String relativeResourceURL) {

        URITemplate mostSpecificTemplate = null;

        for(URITemplate  uriTemplate : webApp.getUriTemplates()){

            if(UrlPatternMatcher.match(String.format("%s:%s", uriTemplate.getHTTPVerb(), uriTemplate.getUriTemplate()),
                    String.format("%s:/%s", httpVerb, relativeResourceURL))){

                if(mostSpecificTemplate == null){
                    mostSpecificTemplate = uriTemplate;
                }else if(mostSpecificTemplate.getUriTemplate().split("/").length < uriTemplate.getUriTemplate().split("/").length){
                    mostSpecificTemplate = uriTemplate;
                }
            }
        }

        return mostSpecificTemplate;
    }

    public static void logRequest(Log log, MessageContext messageContext) {


        if(log.isDebugEnabled()){
            org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

            String fullResourceURL = (String) messageContext.getProperty(RESTConstants.REST_FULL_REQUEST_PATH);
            String httpVerb =   (String) axis2MessageContext.getProperty(Constants.Configuration.HTTP_METHOD);

            logWithRequestInfo(log, messageContext, String.format("Processing request : '%s':'%s'", httpVerb, fullResourceURL));
        }

    }

    public static void logWithRequestInfo(Log log, MessageContext messageContext, String message) {

        Session session = getSession(messageContext);

        String hashedSessionID = null;
        if(session != null){
            hashedSessionID = getMD5Hash(session.getUuid());
        }


        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        String fullResourceURL = (String) messageContext.getProperty(RESTConstants.REST_FULL_REQUEST_PATH);
        String httpVerb =   (String) axis2MessageContext.getProperty(Constants.Configuration.HTTP_METHOD);

        String requestInfo = String.format("{%s;%s;%s;%s}", hashedSessionID, messageContext.getMessageID(), httpVerb, fullResourceURL);

        log.debug(String.format("%s - %s", requestInfo, message));

    }

    public static String getMD5Hash(String input){
        return DigestUtils.md5Hex(input);
    }

    public static void redirectToIDPWithSAMLRequest(MessageContext messageContext, RequestAbstractType samlRequest) {


        String encodedAuthenticationRequest = null;
        try {
            encodedAuthenticationRequest = SAMLUtils.marshallAndEncodeSAMLRequest(samlRequest);
        } catch (SAMLException e) {
            e.printStackTrace();
        }

        String samlRequestURL = GatewayUtils.getIDPUrl() + "?SAMLRequest=" + encodedAuthenticationRequest;
        redirectToURL(messageContext, samlRequestURL);

    }

    public static void redirectToURL(MessageContext messageContext, String url){

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext). getAxis2MessageContext();
        axis2MessageContext.setProperty(NhttpConstants.HTTP_SC, "302");

        messageContext.setResponse(true);
        messageContext.setProperty("RESPONSE", "true");
        messageContext.setTo(null);
        axis2MessageContext.removeProperty("NO_ENTITY_BODY");

        /* Always remove the ContentType - Let the formatter do its thing */
        axis2MessageContext.removeProperty(Constants.Configuration.CONTENT_TYPE);

        Map headerMap = (Map) axis2MessageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        headerMap.put("Location", url);

        if(log.isDebugEnabled()){
            GatewayUtils.logWithRequestInfo(log, messageContext, String.format("Sending HTTP redirect to '%s'", url));
        }

        removeIrrelevantHeadersBeforeResponding(headerMap);
        Axis2Sender.sendBack(messageContext);
    }

    public static boolean isLogoutURL(WebApp webApp, String relativeResourceURL) {

        String logoutUrl = webApp.getLogoutURL();

        if (logoutUrl != null && !logoutUrl.trim().isEmpty() && !relativeResourceURL.trim().isEmpty() && logoutUrl.endsWith(relativeResourceURL)) {
            return true;
        } else {
            return false;
        }
    }

    private static void removeIrrelevantHeadersBeforeResponding(Map headerMap) {
        headerMap.remove(HttpHeaders.HOST);
        headerMap.remove(HTTPConstants.HEADER_COOKIE);
    }
}
