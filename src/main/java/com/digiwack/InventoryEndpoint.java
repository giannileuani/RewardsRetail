package com.digiwack;

import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.digiwack.retailreward.AddItemRequest;
import com.digiwack.retailreward.CartItemType;
import com.digiwack.retailreward.CartStatusRequest;
import com.digiwack.retailreward.CartStatusResponse;
import com.digiwack.retailreward.CustomerType;
import com.digiwack.retailreward.InventoryRequest;
import com.digiwack.retailreward.InventoryResponse;
import com.digiwack.retailreward.RemoveItemRequest;

/**
*An Endpoint is roughly a *.wsdl's method functionality
*/
@Endpoint
public class InventoryEndpoint {
	/*
	 * Case-sensitive, is coming from the *.xsd file
	 */
	private static final String NAMESPACE_URI = "http://digiwack.com/retailReward";

	/*
	 * All items stored here
	 */
	private InventoryWarehouse warehouse;

	@Autowired
	public InventoryEndpoint(InventoryWarehouse warehouse) {
		this.warehouse = warehouse;
	}

	/**
	 * Empty request that pings the server to get a list of items in inventory
	 * @param request
	 * @return
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "InventoryRequest")
	@ResponsePayload
	public InventoryResponse getInventoryList(@RequestPayload InventoryRequest request) {
		InventoryResponse response = new InventoryResponse();
		Vector<String> keys=warehouse.getAllItems();
		for (String key:keys) {
			response.getAnItem().add(warehouse.getItem(key));
		}
		return response;
	}
	/**
	*Customer request to get back the customer's cart status
	*/
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "CartStatusRequest")
	@ResponsePayload
	public CartStatusResponse getCartStatus(@RequestPayload CartStatusRequest request) {
		return getCartStatus(request.getCustomer());
	}	
	/**
	*Customer and item name request to add said item to shopping cart
	*/
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddItemRequest")
	@ResponsePayload
	public CartStatusResponse addItemUpdate(@RequestPayload AddItemRequest request) {
		warehouse.addItem(request.getCustomer(), request.getItemName());
		return getCartStatus(request.getCustomer());
	}	
	/**
	*Customer and item name request to remove said item from shopping cart
	*/
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "RemoveItemRequest")
	@ResponsePayload
	public CartStatusResponse removeItemUpdate(@RequestPayload RemoveItemRequest request) {
		warehouse.removeItem(request.getCustomer(), request.getItemName());
		return getCartStatus(request.getCustomer());
	}
	/**
	*Construct the CartStatusResponse that will be turned into a SOAP object
	*sent back to client
	*/
	private CartStatusResponse getCartStatus(CustomerType cust) {
		Vector<CartItemType> cart=warehouse.getCartItems(cust);
		CartStatusResponse response = new CartStatusResponse();
		response.setItemCount(0);
		response.setRewardPoints(0);
		response.setTotalCost(0);
		for (CartItemType cit:cart) {
			response.getCartItem().add(cit);
			response.setItemCount(response.getItemCount()+cit.getItemCount());
			response.setTotalCost(response.getTotalCost()+cit.getItemCost());
			response.setRewardPoints(computeRewardAmount(response.getTotalCost()));
		}		
		return response;
	}
	/**
	 * The original feature as requested.
	 * totalCost <=50 -- 0 reward points
	 * totalCost 51~100 -- 1 reward point for every dollar over $50
	 * totalCost >=101 -- 50 reward points plus 2 reward points for every dollar over $100
	 * @param totalCost 
	 * @return
	 */
	private int computeRewardAmount(int totalCost) {
		if (51<=totalCost && totalCost<101) {
			return totalCost-50;
		} else if (totalCost>=101) {
			return 50+(totalCost-100)*2;
		} else {
			return 0;
		}
	}
}
