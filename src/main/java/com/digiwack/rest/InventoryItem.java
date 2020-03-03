package com.digiwack.rest;

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
