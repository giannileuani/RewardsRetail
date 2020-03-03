package com.digiwack.rest;

public class CustomerId {

	private String name;
	private String phoneNum;
	public CustomerId(String n,String ph) {
		name=n;
		phoneNum=ph;
	}
	public String getName() {
		return name;
	}
	public String getPhone() {
		return phoneNum;
	}
}
