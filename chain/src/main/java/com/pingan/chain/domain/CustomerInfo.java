package com.pingan.chain.domain;

import java.io.Serializable;

public class CustomerInfo implements Serializable{
	private static final long serialVersionUID = 1L;
    
	private String um;//用户UM号
	private String productName;//产品名称
	private String customerAddress;//用户地址
	private double customerBalance;//用户余额
	
	public String getUm() {
		return um;
	}
	public void setUm(String um) {
		this.um = um;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public double getCustomerBalance() {
		return customerBalance;
	}
	public void setCustomerBalance(double customerBalance) {
		this.customerBalance = customerBalance;
	}
}
