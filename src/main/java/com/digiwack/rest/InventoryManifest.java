package com.digiwack.rest;

import java.util.Vector;

public class InventoryManifest {

	private Vector<InventoryItem> manifest;
	public InventoryManifest() {
		manifest=new Vector<InventoryItem>();
	}
	public void addItem(InventoryItem itm) {
		manifest.add(itm);
	}
	public int getCount() {
		return manifest.size();
	}
	public InventoryItem[] getItems() {
		return manifest.toArray(new InventoryItem[manifest.size()]);
	}
	public InventoryItem getItem(int i) {
		if (0<=i && i<manifest.size()) {
			return manifest.get(i);
		} else {
			InventoryItem itm=new InventoryItem("nuthin", 0);
			return itm;
		}
	}
}
