package com.digiwack;

import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.digiwack.rest.CartItem;
import com.digiwack.rest.CustomerId;
import com.digiwack.rest.InventoryItem;
import com.digiwack.rest.InventoryManifest;
import com.digiwack.rest.ShoppingCart;
import com.digiwack.retailreward.AddItemRequest;
import com.digiwack.retailreward.CartItemType;
import com.digiwack.retailreward.CartStatusRequest;
import com.digiwack.retailreward.CartStatusResponse;
import com.digiwack.retailreward.CustomerType;
import com.digiwack.retailreward.InventoryRequest;
import com.digiwack.retailreward.InventoryResponse;
import com.digiwack.retailreward.RemoveItemRequest;

@RestController
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
	
	@GetMapping("/inventory")
	public InventoryManifest inventoryManifest() {
		InventoryManifest mnf=new InventoryManifest();
		Vector<String> keys=warehouse.getAllItems();
		for (String key:keys) {
			com.digiwack.retailreward.InventoryItem itm = warehouse.getItem(key);
			mnf.addItem(new InventoryItem(itm.getItemName(),itm.getItemCost()));
		}
		return mnf;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "CartStatusRequest")
	@ResponsePayload
	public CartStatusResponse getCartStatus(@RequestPayload CartStatusRequest request) {
		return getCartStatus(request.getCustomer());
	}
	@GetMapping("/shoppingCart")
	public ShoppingCart getShoppingCart(
			@RequestParam(value = "customerName", defaultValue = "Jim") String customerName,
			@RequestParam(value = "customerPhone", defaultValue = "just holler") String customerPhone) {
		CustomerId cid=new CustomerId(customerName, customerPhone);
		return getCartStatus(cid);
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddItemRequest")
	@ResponsePayload
	public CartStatusResponse addItemUpdate(@RequestPayload AddItemRequest request) {
		String custKey=warehouse.getCustomerKey(request.getCustomer());
		warehouse.addItem(custKey, request.getItemName());
		return getCartStatus(request.getCustomer());
	}
	@GetMapping("/shoppingCartAdd")
	public ShoppingCart addToShoppingCart(
			@RequestParam(value = "customerName", defaultValue = "Jim") String customerName,
			@RequestParam(value = "customerPhone", defaultValue = "just holler") String customerPhone,
			@RequestParam(value = "itemName", defaultValue = "nuthin") String itemName) {
		CustomerId cid=new CustomerId(customerName, customerPhone);
		String custKey=warehouse.getCustomerKey(cid);
		warehouse.addItem(custKey, itemName);
		return getCartStatus(cid);
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "RemoveItemRequest")
	@ResponsePayload
	public CartStatusResponse removeItemUpdate(@RequestPayload RemoveItemRequest request) {
		String custKey=warehouse.getCustomerKey(request.getCustomer());
		warehouse.removeItem(custKey, request.getItemName());
		return getCartStatus(request.getCustomer());
	}
	@GetMapping("/shoppingCartRemove")
	public ShoppingCart removeFromShoppingCart(
			@RequestParam(value = "customerName", defaultValue = "Jim") String customerName,
			@RequestParam(value = "customerPhone", defaultValue = "just holler") String customerPhone,
			@RequestParam(value = "itemName", defaultValue = "nuthin") String itemName) {
		CustomerId cid=new CustomerId(customerName, customerPhone);
		String custKey=warehouse.getCustomerKey(cid);
		warehouse.removeItem(custKey, itemName);
		return getCartStatus(cid);
	}
	private CartStatusResponse getCartStatus(CustomerType cust) {
		String custKey=warehouse.getCustomerKey(cust);
		Vector<CartItemType> cart=warehouse.getCartItems(custKey);
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
	private ShoppingCart getCartStatus(CustomerId cust) {
		String custKey=warehouse.getCustomerKey(cust);
		Vector<CartItemType> cart=warehouse.getCartItems(custKey);
		ShoppingCart ret=new ShoppingCart();
		for (CartItemType cit:cart) {
			CartItem ct=new CartItem(cit.getItemName(), cit.getItemCount(), cit.getItemCost());
			ret.addItem(ct);
			ret.setRewardPoints(computeRewardAmount(ret.getTotalCost()));
		}
		return ret;
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
