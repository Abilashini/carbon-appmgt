package org.wso2.carbon.appmgt.mobile.utils;

import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.NetworkUtils;

/**
 * Created by dilan on 3/18/15.
 */
public class HostResolver {

    private static final Log log = LogFactory.getLog(HostResolver.class);

    public static String getHost(String abbr){

        String host = "";

        if("%http%".equals(abbr)){

            try {
                host += "http://" + NetworkUtils.getLocalHostname() + ":" +  CarbonUtils.getTransportPort(ConfigurationContextFactory.createDefaultConfigurationContext(), "http");
            } catch (Exception e) {
               log.error("Error occurred while getting host");
               log.debug("Error: " + e);
            }
        }else if("%https%".equals(abbr)){
            try {
                host += "https://" + NetworkUtils.getLocalHostname() + ":" +  CarbonUtils.getTransportPort(ConfigurationContextFactory.createDefaultConfigurationContext(), "https");
            } catch (Exception e) {
                log.error("Error occurred while getting host");
                log.debug("Error: " + e);
            }
        }else{
            host = abbr;
        }

        return host;
    }


    public static String getHostWithHTTP(){

        String host = "";

        try {
            host += "http://" + NetworkUtils.getLocalHostname() + ":" +  CarbonUtils.getTransportPort(ConfigurationContextFactory.createDefaultConfigurationContext(), "http");
        } catch (Exception e) {
            log.error("Error occurred while getting host");
            log.debug("Error: " + e);
        }

        return host;
    }
}
