package com.digiwack.rest;

import java.util.Vector;

public class ShoppingCart {
	private Vector<CartItem> theCart;
	private int totalItemCount;
	private int totalCost=0;
	private int rewardPoints=0;
	public ShoppingCart() {
		theCart=new Vector<CartItem>();
	}
	public void addItem(CartItem itm) {
		theCart.add(itm);
		totalItemCount+=itm.getCount();
		totalCost+=itm.getCost();
	}
	public int getCount() {
		return theCart.size();
	}
	public int getTotalItemCount() {
		return totalItemCount;
	}
	public int getTotalCost() {
		return totalCost;
	}
	public void setRewardPoints(int p) {
		rewardPoints=p;
	}
	public int getRewards() {
		return rewardPoints;
	}
	public CartItem[] getItems() {
		return theCart.toArray(new CartItem[theCart.size()]);
	}
	public CartItem getItem(int i) {
		if (0<=i && i<theCart.size()) {
			return theCart.get(i);
		} else {
			CartItem itm=new CartItem("nuthin",0, 0);
			return itm;
		}
	}
}
