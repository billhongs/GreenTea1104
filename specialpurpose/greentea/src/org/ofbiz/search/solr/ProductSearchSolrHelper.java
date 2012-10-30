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
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;

public class ProductSearchSolrHelper {

	public static final String module = ProductSearchSolrHelper.class.getName();
	
	private static String[] solrProdAttribute = { "productId", "internalName", "manu", "size", "smallImage", "mediumImage", "largeImage", "listPrice", "defaultPrice", "inStock", "isVirtual" };
	
	public static SolrInputDocument generateSolrDocument(Map<String, Object> context) throws GenericEntityException {
        SolrInputDocument doc1 = new SolrInputDocument();

        // add defined attributes
        for (int i = 0; i < solrProdAttribute.length; i++) {
            if (context.get(solrProdAttribute[i]) != null) {
                doc1.addField(solrProdAttribute[i], context.get(solrProdAttribute[i]).toString());
            }
        }

        // add catalog
        if (context.get("catalog") != null) {
            List<String> catalog = UtilGenerics.<String>checkList(context.get("catalog"));
            for (String c : catalog) {
                doc1.addField("catalog", c);
            }
        }

        // add categories
        if (context.get("category") != null) {
            List<String> category = UtilGenerics.<String>checkList(context.get("category"));
            Iterator<String> catIter = category.iterator();
            while (catIter.hasNext()) {
                /*
                 * GenericValue cat = (GenericValue) catIter.next(); GenericValue prodCategory = cat.getRelatedOneCache("ProductCategory"); if (prodCategory.get("description") != null) {
                 * doc1.addField("category", prodCategory.get("description")); } doc1.addField("cat", prodCategory.get("productCategoryId"));
                 */
                String cat = (String) catIter.next();
                doc1.addField("cat", cat);
            }
        }

        // add features
        if (context.get("features") != null) {
            Set<String> features = UtilGenerics.<String>checkSet(context.get("features"));
            Iterator<String> featIter = features.iterator();
            while (featIter.hasNext()) {
                String feat = featIter.next();
                doc1.addField("features", feat);
            }
        }

        // add attributes
        if (context.get("attributes") != null) {
            List<String> attributes = UtilGenerics.<String>checkList(context.get("attributes"));
            Iterator<String> attrIter = attributes.iterator();
            while (attrIter.hasNext()) {
                String attr = attrIter.next();
                doc1.addField("attributes", attr);
            }
        }

        // add title
        if (context.get("title") != null) {
            Map<String, String> title = UtilGenerics.<String, String>checkMap(context.get("title"));
            for (Map.Entry<String, String> entry : title.entrySet()) {
                doc1.addField("title_i18n_" + entry.getKey(), entry.getValue());
            }
        }

        // add short_description
        if (context.get("description") != null) {
            Map<String, String> description = UtilGenerics.<String, String>checkMap(context.get("description"));
            for (Map.Entry<String, String> entry : description.entrySet()) {
                doc1.addField("description_i18n_" + entry.getKey(), entry.getValue());
            }
        }

        // add short_description
        if (context.get("longDescription") != null) {
            Map<String, String> longDescription = UtilGenerics.<String, String>checkMap(context.get("longDescription"));
            for (Map.Entry<String, String> entry : longDescription.entrySet()) {
                doc1.addField("longdescription_i18n_" + entry.getKey(), entry.getValue());
            }
        }

        return doc1;
    }
    
    public static Map<String, Object> categoriesAvailable(String catalogId, String categoryId, String productId, boolean displayproducts, int viewIndex, int viewSize) {
    	return categoriesAvailable(catalogId,categoryId,productId,null,displayproducts,viewIndex,viewSize);
    }

    public static Map<String, Object> categoriesAvailable(String catalogId, String categoryId, String productId, String facetPrefix, boolean displayproducts, int viewIndex, int viewSize) {
        // create the data model
        Map<String, Object> result = FastMap.newInstance();
        HttpSolrServer server = null;
        QueryResponse returnMap = new QueryResponse();
        try {
            // do the basic query
            server = new HttpSolrServer(SolrUtil.solrUrl);
            // create Query Object
            String query = "inStock[1 TO *]";
            if (categoryId != null)
                query += " +cat:"+ categoryId;
            else if (productId != null)
                query += " +productId:" + productId;
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(query);

            if (catalogId != null)
                solrQuery.setFilterQueries("catalog:" + catalogId);
            if (displayproducts) {
                if (viewSize > -1) {
                    solrQuery.setRows(viewSize);
                } else
                    solrQuery.setRows(50000);
                if (viewIndex > -1) {
                    solrQuery.setStart(viewIndex);
                }
            } else {
                solrQuery.setFields("cat");
                solrQuery.setRows(0);
            }
            
            if(UtilValidate.isNotEmpty(facetPrefix)){
                solrQuery.setFacetPrefix(facetPrefix);
            }
            
            solrQuery.setFacetMinCount(0);
            solrQuery.setFacet(true);
            solrQuery.addFacetField("cat");
            solrQuery.setFacetLimit(-1);
            Debug.logVerbose("solr: solrQuery: " + solrQuery, module);
            returnMap = server.query(solrQuery,METHOD.POST);
            result.put("rows", returnMap);
            result.put("numFound", returnMap.getResults().getNumFound());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
        return result;
    }
}
