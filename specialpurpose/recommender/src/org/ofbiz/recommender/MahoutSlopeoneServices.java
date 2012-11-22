package org.ofbiz.recommender;

import java.util.Map;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.ofbiz.entity.Delegator;
import org.ofbiz.recommender.model.OfbizDataModel;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class MahoutSlopeoneServices {

	/**
	 * 
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> doExecute(DispatchContext ctx, Map<String, ? extends Object> context) throws Exception{
		
		Delegator delegator = ctx.getDelegator();
		String strActionTypeId = (String)context.get("actionTypeId");
		Boolean hasRatingValues = (Boolean)context.get("hasRatingValues");
		int actionTypeId = Integer.parseInt(strActionTypeId);
		
		OfbizDataModel dataModel = new OfbizDataModel(actionTypeId,hasRatingValues,delegator);
		Recommender recommender = new SlopeOneRecommender(dataModel);
		
		
		
		return ServiceUtil.returnSuccess();
	}
}
