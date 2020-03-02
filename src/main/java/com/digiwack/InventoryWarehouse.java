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
	private String getCustomerKey(CustomerType cust) {
		return cust.getCustomerName()+":"+cust.getCustomerPhone();
	}
	public Vector<CartItemType> getCartItems(CustomerType cust) {
		String cKey=getCustomerKey(cust);
		if (customerCarts.containsKey(cKey)) {
			return customerCarts.get(cKey);
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
	public void addItem(CustomerType cust,String item) {
		InventoryItem ii=getItem(item);
		String cKey=getCustomerKey(cust);
		if (customerCarts.containsKey(cKey)) {
			Vector<CartItemType> custCart=customerCarts.get(cKey);
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
			customerCarts.put(cKey,custCart);
		} else {
			Vector<CartItemType> nucart=new Vector<CartItemType>();
			CartItemType cit=new CartItemType();
			
			cit.setItemName(ii.getItemName());
			cit.setItemCost(ii.getItemCost());
			cit.setItemCount(1);
			nucart.add(cit);
			customerCarts.put(cKey, nucart);
		}
	}
	public void removeItem(CustomerType cust,String item) {
		InventoryItem ii=getItem(item);
		String cKey=getCustomerKey(cust);
		if (customerCarts.containsKey(cKey)) {
			Vector<CartItemType> custCart=customerCarts.get(cKey);
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
			customerCarts.put(cKey,custCart);
		}
	}
}
