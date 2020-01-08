package com.pingan.chain.service;


import com.pingan.chain.common.ResponseMessage;
import com.pingan.chain.domain.ChainAccount;
import com.pingan.chain.domain.ChangeIntegralInfo;
import com.pingan.chain.domain.OrderRuleInfo;
import com.pingan.chain.domain.TransferRecordInfo;

public interface SmartContractsService {

	//查询用户相关信息
	ResponseMessage<ChainAccount> queryCustomerInfo(String um, String productName);
	//更新用户状态
	ResponseMessage<String> updateUserStatus(String um, String status);
	//兑换积分接口
	ResponseMessage<String> exchangeIntegral(ChangeIntegralInfo changeIntegralInfo);
	//转账接口
	ResponseMessage<String> transferAccounts(TransferRecordInfo transferRecordInfo);
	//设置先后规则信息
	ResponseMessage<String> setOrderRule(OrderRuleInfo orderRuleInfo);
	//查询先后规则信息
	ResponseMessage<OrderRuleInfo> selectOrderRule(String eventId);
}
