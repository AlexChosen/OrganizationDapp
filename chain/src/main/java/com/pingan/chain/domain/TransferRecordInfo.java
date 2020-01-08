package com.pingan.chain.domain;

public class TransferRecordInfo {
	
	 private String transferLaunchId;
	 private String transferLaunchAddress;
	 private String userId;
	 private long transferBalance;
	 private String transferTime;
	 
	public String getTransferLaunchId() {
		return transferLaunchId;
	}
	public void setTransferLaunchId(String transferLaunchId) {
		this.transferLaunchId = transferLaunchId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getTransferBalance() {
		return transferBalance;
	}
	public void setTransferBalance(long transferBalance) {
		this.transferBalance = transferBalance;
	}
	public String getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}
	public String getTransferLaunchAddress() {
		return transferLaunchAddress;
	}
	public void setTransferLaunchAddress(String transferLaunchAddress) {
		this.transferLaunchAddress = transferLaunchAddress;
	}
	
}
