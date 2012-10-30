package org.ofbiz.order.shoppingcart;

import org.ofbiz.entity.Delegator;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.LocalDispatcher;

public class FlexCheckOutHelper extends CheckOutHelper {

	public FlexCheckOutHelper(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart) {
        super(dispatcher,delegator,cart);
    }
	
	
}
