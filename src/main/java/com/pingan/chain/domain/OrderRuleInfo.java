package com.pingan.chain.domain;
/**
 * 先后权重配置规则表
 * @author xhxia
 *
 */
public class OrderRuleInfo {

	//事件编号
	private String eventId;
	//事件名称
	private String eventName;
	//开始比例
    private String beginRatio;
    //开始比例
    private String decrementRatio;
    //开始比例
    private String endRatio;
    //计数 
    private int count;
    
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getBeginRatio() {
		return beginRatio;
	}
	public void setBeginRatio(String beginRatio) {
		this.beginRatio = beginRatio;
	}
	public String getDecrementRatio() {
		return decrementRatio;
	}
	public void setDecrementRatio(String decrementRatio) {
		this.decrementRatio = decrementRatio;
	}
	public String getEndRatio() {
		return endRatio;
	}
	public void setEndRatio(String endRatio) {
		this.endRatio = endRatio;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
