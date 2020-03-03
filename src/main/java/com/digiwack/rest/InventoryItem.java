package com.digiwack.rest;

/**
*Github seems to have forgotten to send this file down to my laptop when testing
*/
public class InventoryItem {

	private String name;
	private int price;
	public InventoryItem(String nme,int prc) {
		name=nme;
		price=prc;
	}
	public String getName() {
		return name;
	}
	public int getPrice() {
		return price;
	}
}
