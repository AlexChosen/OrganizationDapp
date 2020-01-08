package com.pingan.chain.mapper;

import com.pingan.chain.domain.ChainAccount;
import com.pingan.chain.domain.ChangeIntegralInfo;
import com.pingan.chain.domain.ModelAccount;
import com.pingan.chain.domain.OrderRuleInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmartContractsMapper {

	//查询用户的信息
	ChainAccount queryCustomerInfo(String um, String productName);
	//更新用户的状态
	void updateUserStatus(String um, String status);
	//查询用户状态
	String selectUserStatus(String um);
	//插入用户积分兑换情况
	void insertChangeIntegralInfo(ChangeIntegralInfo changeIntegralInfo);
	//查询用户信息
	ChainAccount selectUserInfo(String userId);
	//更新用户信息
	void updateChainAccountInfo(ChainAccount chainAccount);
	//查询平台账户信息
	ModelAccount selectModelAccount(ModelAccount modelAccount);
	//更新平台账户信息
	void updateModelAccount(ModelAccount modelAccount);
	//查询事件信息
	OrderRuleInfo selectOrderRuleInfo(String eventId);
	//插入该事件信息
	void insertOrderRuleInfo(OrderRuleInfo orderRuleInfo);
	//更新该事件信息
	void updateOrderRuleInfo(OrderRuleInfo orderRuleInfo);
}
