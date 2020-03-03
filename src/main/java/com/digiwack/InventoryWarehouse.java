package com.digiwack;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.digiwack.rest.CustomerId;
import com.digiwack.retailreward.CartItemType;
import com.digiwack.retailreward.CustomerType;
import com.digiwack.retailreward.InventoryItem;

@Component
public class InventoryWarehouse {

	private static final Map<String,InventoryItem> inventoryItems=new HashMap<>();
	private static Map<String,Vector<CartItemType>> customerCarts=new HashMap<>();
	@PostConstruct
	public void initData() {
		Properties props=new Properties();
		ClassPathResource cpr=new ClassPathResource("static/inventory.properties");
		try {
			File f=cpr.getFile();
			if (f.exists()) {
				FileInputStream in=new FileInputStream(f);
				props.load(in);
				in.close();
			}			
		} catch (Exception err) {
			err.printStackTrace();
		}
		if (props.size()==0) {
			props.setProperty("tincan", "1");
		}
		for (Iterator iter=props.keySet().iterator(); iter.hasNext();) {
			Object key=iter.next();
			InventoryItem itm =new InventoryItem();
			itm.setItemName(key.toString());
			itm.setItemCost(Integer.parseInt(props.get(key).toString()));
			inventoryItems.put(itm.getItemName(), itm);
		}
	}
	public Vector<String> getAllItems() {
		Vector<String> ret=new Vector<String>();
		for (String key:inventoryItems.keySet()) {
			ret.add(key);
		}
		return ret;
	}
	public String getCustomerKey(CustomerType cust) {
		return cust.getCustomerName()+":"+cust.getCustomerPhone();
	}
	public String getCustomerKey(CustomerId cust) {
		return cust.getName()+":"+cust.getPhone();
	}
	public Vector<CartItemType> getCartItems(String customerKey) {
		if (customerCarts.containsKey(customerKey)) {
			return customerCarts.get(customerKey);
		} else {
			Vector<CartItemType> retCart=new Vector<CartItemType>();
			CartItemType cit=new CartItemType();
			cit.setItemName("nuthin");
			cit.setItemCount(0);
			cit.setItemCost(0);
			retCart.add(cit);
			return retCart;
		}
	}
	
	public InventoryItem getItem(String itemName) {
		if (inventoryItems.containsKey(itemName)) {
			return inventoryItems.get(itemName);
		} else {
			InventoryItem empty=new InventoryItem();
			empty.setItemName("Nuthin");
			empty.setItemCost(0);
			return empty;
		}
	}
	public void addItem(String customerKey,String item) {
		InventoryItem ii=getItem(item);
		if (customerCarts.containsKey(customerKey)) {
			Vector<CartItemType> custCart=customerCarts.get(customerKey);
			boolean found=false;
			for (CartItemType cit:custCart) {
				if (cit.getItemName().equals(item)) {
					cit.setItemCount(cit.getItemCount()+1);
					cit.setItemCost(cit.getItemCount()*ii.getItemCost());
					found=true;
				}
			}
			if (!found) {
				CartItemType cit=new CartItemType();
				
				cit.setItemName(ii.getItemName());
				cit.setItemCost(ii.getItemCost());
				cit.setItemCount(1);
				custCart.add(cit);
			}
			customerCarts.put(customerKey,custCart);
		} else {
			Vector<CartItemType> nucart=new Vector<CartItemType>();
			CartItemType cit=new CartItemType();
			
			cit.setItemName(ii.getItemName());
			cit.setItemCost(ii.getItemCost());
			cit.setItemCount(1);
			nucart.add(cit);
			customerCarts.put(customerKey, nucart);
		}
	}
	public void removeItem(String customerKey,String item) {
		InventoryItem ii=getItem(item);
		if (customerCarts.containsKey(customerKey)) {
			Vector<CartItemType> custCart=customerCarts.get(customerKey);
			for (Iterator<CartItemType> iter=custCart.iterator(); iter.hasNext();) {
				CartItemType cit=iter.next();
				if (cit.getItemName().equals(item)) {
					if (cit.getItemCount()>1) {
						cit.setItemCount(cit.getItemCount()-1);
						cit.setItemCost(cit.getItemCount()*ii.getItemCost());
					} else {
						iter.remove();
					}					
				}
			}
			customerCarts.put(customerKey,custCart);
		}
	}
}
