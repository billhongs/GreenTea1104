package org.ofbiz.search.solr;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentConfig.WebappInfo;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;

/**
 * Solr utility class.
 */
public abstract class SolrUtil {
    
    public static final String module = SolrUtil.class.getName();
    

    public static final String solrConfigName = "solrconfig.properties";
    public static final String solrUrl = makeSolrWebappUrl();
    
    public static String makeSolrWebappUrl() {
        final String solrWebappProtocol = UtilProperties.getPropertyValue(solrConfigName, "solr.webapp.protocol");
        final String solrWebappDomainName = UtilProperties.getPropertyValue(solrConfigName, "solr.webapp.domainName");
        final String solrWebappPath = UtilProperties.getPropertyValue(solrConfigName, "solr.webapp.path");
        final String solrWebappPortOverride = UtilProperties.getPropertyValue(solrConfigName, "solr.webapp.portOverride");
        
        String solrPort;
        if (UtilValidate.isNotEmpty(solrWebappPortOverride)) {
            solrPort = solrWebappPortOverride;
        }
        else {
            solrPort = UtilProperties.getPropertyValue("url.properties", ("https".equals(solrWebappProtocol) ? "port.https" : "port.http"));
        }
        
        return solrWebappProtocol + "://" + solrWebappDomainName + ":" + solrPort + solrWebappPath;
    }
    
    public static boolean isSolrEcaEnabled() {
        Boolean ecaEnabled = null;
        String sysProp = System.getProperty("ofbiz.solr.eca.enabled");
        if (UtilValidate.isNotEmpty(sysProp)) {
            if ("true".equalsIgnoreCase(sysProp))  {
                ecaEnabled = Boolean.TRUE;
            }
            else if ("false".equalsIgnoreCase(sysProp)) {
                ecaEnabled = Boolean.FALSE;
            }
        }
        if (ecaEnabled == null) {
            ecaEnabled = UtilProperties.getPropertyAsBoolean(SolrUtil.solrConfigName, "solr.eca.enabled", false);
        }
        return Boolean.TRUE.equals(ecaEnabled);
    }
    
    public static WebappInfo getSolrWebappInfo() {
        WebappInfo solrApp = null;
        try {
            ComponentConfig cc = ComponentConfig.getComponentConfig("solr");
            for(WebappInfo currApp : cc.getWebappInfos()) {
                if ("solr".equals(currApp.getName())) {
                    solrApp = currApp;
                    break;
                }
            }
        }
        catch(ComponentException e) {
            throw new IllegalStateException(e);
        }
        return solrApp;
    }
    
    public static boolean isSolrEcaWebappInitCheckPassed() {
        Boolean webappCheckEnabled = UtilProperties.getPropertyAsBoolean(solrConfigName, "solr.eca.useSolrWebappLoadedCheck", true);
        if (Boolean.TRUE.equals(webappCheckEnabled)) {
            return isSolrWebappInitialized();
        }
        else {
            // If webapp check disabled, then we say the check passed.
            return true;
        }
    }
    
    public static boolean isSolrWebappInitialized() {
        return OfbizSolrInfoServlet.isServletInitStatusReached();
    }
    
    public static boolean isEcaTreatConnectErrorNonFatal() {
        Boolean treatConnectErrorNonFatal = UtilProperties.getPropertyAsBoolean(solrConfigName, "solr.eca.treatConnectErrorNonFatal", true);
        return Boolean.TRUE.equals(treatConnectErrorNonFatal);
    }
    
    
    

}