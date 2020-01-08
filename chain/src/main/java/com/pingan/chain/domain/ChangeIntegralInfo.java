package com.pingan.chain.domain;
/**
 * 积分兑换明细表
 * @author xhxia
 *
 */
public class ChangeIntegralInfo {

	
	private String userId;//用户编号
	private String userAddress;//用户地址
	private String giftId;//礼品编号
	private String giftName;//礼品名称
	private long exchangeBalance;//兑换积分
	private String exchangeTime;//兑换时间
	private String platformName;//平台名称
	private String platformAddress;//平台地址·
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	public String getGiftId() {
		return giftId;
	}
	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}
	public String getGiftName() {
		return giftName;
	}
	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}
	public long getExchangeBalance() {
		return exchangeBalance;
	}
	public void setExchangeBalance(long exchangeBalance) {
		this.exchangeBalance = exchangeBalance;
	}
	public String getExchangeTime() {
		return exchangeTime;
	}
	public void setExchangeTime(String exchangeTime) {
		this.exchangeTime = exchangeTime;
	}
	public String getPlatformName() {
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	public String getPlatformAddress() {
		return platformAddress;
	}
	public void setPlatformAddress(String platformAddress) {
		this.platformAddress = platformAddress;
	}
	
}
