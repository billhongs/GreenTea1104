package org.ofbiz.search.solr;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;

public class ProductIndexServices {

	public static final String module = ProductIndexServices.class.getName();
	
	/**
     * Adds product to solr, with product denoted by productId field in instance attribute
     * - intended for use with ECAs/SECAs.
     */
    public static Map<String, Object> addToSolr(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        Map<String, Object> result;
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue productInstance = (GenericValue) context.get("instance");
        String productId = (String) productInstance.get("productId");
        
        if (SolrUtil.isSolrEcaEnabled()) {
            if (SolrUtil.isSolrEcaWebappInitCheckPassed()) {
                Debug.logVerbose("Solr: addToSolr: Running indexing for productId '" + productId + "'", module);
                
                try {
                    GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                    Map<String, Object> dispatchContext = ProductUtil.getProductContent(product, dctx, context);
                    dispatchContext.put("treatConnectErrorNonFatal", SolrUtil.isEcaTreatConnectErrorNonFatal());
                    Map<String, Object> runResult = dispatcher.runSync("addToSolrIndex", dispatchContext);
                    String runMsg = ServiceUtil.getErrorMessage(runResult);
                    if (UtilValidate.isEmpty(runMsg)) {
                        runMsg = null;
                    }
                    if (ServiceUtil.isError(runResult)) {
                        result = ServiceUtil.returnError(runMsg);
                    }
                    else if (ServiceUtil.isFailure(runResult)) {
                        result = ServiceUtil.returnFailure(runMsg);
                    }
                    else {
                        result = ServiceUtil.returnSuccess();
                    }
                } catch (Exception e) {
                    Debug.logError(e, e.getMessage(), module);
                    result = ServiceUtil.returnError(e.toString());
                }
            }
            else {
                final String statusMsg = "Solr webapp not available; skipping indexing for productId '" + productId + "'";
                Debug.logVerbose("Solr: addToSolr: " + statusMsg, module);
                result = ServiceUtil.returnSuccess();
            }
        }
        else {
            final String statusMsg = "Solr ECA indexing disabled; skipping indexing for productId '" + productId + "'";
            Debug.logVerbose("Solr: addToSolr: " + statusMsg, module);
            result = ServiceUtil.returnSuccess();
        }
        return result;
    }

    /**
     * Adds product to solr index.
     */
    public static Map<String, Object> addToSolrIndex(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        HttpSolrServer server = null;
        Map<String, Object> result;
        String productId = (String) context.get("productId");
        // connectErrorNonFatal is a necessary option because in some cases it may be considered normal that solr server is unavailable;
        // don't want to return error and abort transactions in these cases.
        Boolean treatConnectErrorNonFatal = (Boolean) context.get("treatConnectErrorNonFatal");
        try {
            Debug.logInfo("Solr: Generating and indexing document for productId '" + productId + "'", module);
            
            server = new HttpSolrServer(SolrUtil.solrUrl);
            //Debug.log(server.ping().toString());

            // Construct Documents
            SolrInputDocument doc1 = ProductSearchSolrHelper.generateSolrDocument(context);
            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            
            if (Debug.verboseOn()) {
                Debug.logVerbose("Solr: Indexing document: " + doc1.toString(), module);
            }
            
            docs.add(doc1);

            // push Documents to server
            server.add(docs);
            server.commit();
            
            final String statusStr = "Document for productId " + productId + " added to solr index";
            Debug.logInfo("Solr: " + statusStr, module);
            result = ServiceUtil.returnSuccess(statusStr);
        } catch (MalformedURLException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
            result.put("errorType", "urlError");
        } catch (SolrServerException e) {
            if (e.getCause() != null && e.getCause() instanceof ConnectException) {
                final String statusStr = "Failure connecting to solr server to commit productId " + 
                        context.get("productId") + "; product not updated";
                if (Boolean.TRUE.equals(treatConnectErrorNonFatal)) {
                    Debug.logWarning(e, "Solr: " + statusStr, module);
                    result = ServiceUtil.returnFailure(statusStr);
                }
                else {
                    Debug.logError(e, "Solr: " + statusStr, module);
                    result = ServiceUtil.returnError(statusStr);
                }
                result.put("errorType", "connectError");
            }
            else {
                Debug.logError(e, e.getMessage(), module);
                result = ServiceUtil.returnError(e.toString());
                result.put("errorType", "solrServerError");
            }
        } catch (IOException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
            result.put("errorType", "ioError");
        }
        return result;
    }
    
    /**
     * Adds a List of products to the solr index.
     * <p>
     * This is faster than reflushing the index each time.
     */
    public static Map<String, Object> addListToSolrIndex(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        HttpSolrServer server = null;
        Map<String, Object> result;
        Boolean treatConnectErrorNonFatal = (Boolean) context.get("treatConnectErrorNonFatal");
        try {
            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

            // Construct Documents
            List<Map<String, Object>> fieldList = UtilGenerics.<Map<String, Object>>checkList(context.get("fieldList"));
            
            Debug.logInfo("Solr: Generating and adding " + fieldList.size() + " documents to solr index", module);
            
            for (Iterator<Map<String, Object>> fieldListIterator = fieldList.iterator(); fieldListIterator.hasNext();) {
                SolrInputDocument doc1 = ProductSearchSolrHelper.generateSolrDocument(fieldListIterator.next());
                if (Debug.verboseOn()) {
                    Debug.logVerbose("Solr: Indexing document: " + doc1.toString(), module);
                }
                docs.add(doc1);
            }
            // push Documents to server
            server = new HttpSolrServer(SolrUtil.solrUrl);
            server.add(docs);
            server.commit();
            
            final String statusStr = "Added " + fieldList.size() + " documents to solr index";
            Debug.logInfo("Solr: " + statusStr, module);
            result = ServiceUtil.returnSuccess(statusStr);
        } catch (MalformedURLException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
            result.put("errorType", "urlError");
        } catch (SolrServerException e) {
            if (e.getCause() != null && e.getCause() instanceof ConnectException) {
                final String statusStr = "Failure connecting to solr server to commit product list; products not updated";
                if (Boolean.TRUE.equals(treatConnectErrorNonFatal)) {
                    Debug.logWarning(e, "Solr: " + statusStr, module);
                    result = ServiceUtil.returnFailure(statusStr);
                }
                else {
                    Debug.logError(e, "Solr: " + statusStr, module);
                    result = ServiceUtil.returnError(statusStr);
                }
                result.put("errorType", "connectError");
            }
            else {
                Debug.logError(e, e.getMessage(), module);
                result = ServiceUtil.returnError(e.toString());
                result.put("errorType", "solrServerError");
            }
        } catch (IOException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
            result.put("errorType", "ioError");
        }
        return result;
    }

    
    /**
     * Rebuilds the solr index.
     */
    public static Map<String, Object> rebuildSolrIndex(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        HttpSolrServer server = null;
        Map<String, Object> result;
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = new Locale("de_DE");
        
        Boolean treatConnectErrorNonFatal = (Boolean) context.get("treatConnectErrorNonFatal");
        
        try {
            server = new HttpSolrServer(SolrUtil.solrUrl);

            // now lets fetch all products
            List<Map<String, Object>> solrDocs = FastList.newInstance();
            List<GenericValue> products = delegator.findList("Product", null, null, null, null, true);
            int numDocs = 0;
            if (products != null) {
                numDocs = products.size();
            }
            
            Debug.logInfo("Solr: Clearing solr index and rebuilding with " + numDocs + " found products", module);
            
            Iterator<GenericValue> productIterator = products.iterator();
            while (productIterator.hasNext()) {
                GenericValue product = productIterator.next();
                Map<String, Object> dispatchContext = ProductUtil.getProductContent(product, dctx, context);
                solrDocs.add(dispatchContext);
            }

            // this removes everything from the index
            server.deleteByQuery("*:*");
            server.commit();

            // THis adds all products to the Index (instantly)
            Map<String, Object> runResult = dispatcher.runSync("addListToSolrIndex", UtilMisc.toMap("fieldList", solrDocs, "userLogin", userLogin, 
                    "locale", locale, "treatConnectErrorNonFatal", treatConnectErrorNonFatal));
            
            String runMsg = ServiceUtil.getErrorMessage(runResult);
            if (UtilValidate.isEmpty(runMsg)) {
                runMsg = null;
            }
            if (ServiceUtil.isError(runResult)) {
                result = ServiceUtil.returnError(runMsg);
            }
            else if (ServiceUtil.isFailure(runResult)) {
                result = ServiceUtil.returnFailure(runMsg);
            }
            else {
                final String statusMsg = "Cleared solr index and reindexed " + numDocs + " documents";
                result = ServiceUtil.returnSuccess(statusMsg);
            }
        } catch (MalformedURLException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
        } catch (SolrServerException e) {
            if (e.getCause() != null && e.getCause() instanceof ConnectException) {
                final String statusStr = "Failure connecting to solr server to rebuild index; index not updated";
                if (Boolean.TRUE.equals(treatConnectErrorNonFatal)) {
                    Debug.logWarning(e, "Solr: " + statusStr, module);
                    result = ServiceUtil.returnFailure(statusStr);
                }
                else {
                    Debug.logError(e, "Solr: " + statusStr, module);
                    result = ServiceUtil.returnError(statusStr);
                }
            }
            else {
                Debug.logError(e, e.getMessage(), module);
                result = ServiceUtil.returnError(e.toString());
            }
        } catch (IOException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
        } catch (ServiceAuthException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
        } catch (ServiceValidationException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
        } catch (GenericServiceException e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
        } catch (Exception e) {
            Debug.logError(e, e.getMessage(), module);
            result = ServiceUtil.returnError(e.toString());
        }
        return result;
    }
}
