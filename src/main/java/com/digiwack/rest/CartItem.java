package com.digiwack.rest;

public class CartItem {

	private String name;
	private int count;
	private int cost;
	public CartItem(String n,int ct,int cs) {
		name=n;
		count=ct;
		cost=cs;
	}
	public String getName() {
		return name;
	}
	public int getCount() {
		return count;
	}
	public int getCost() {
		return cost;
	}
}
