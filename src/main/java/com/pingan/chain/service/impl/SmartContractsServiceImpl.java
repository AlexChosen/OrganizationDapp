package com.pingan.chain.service.impl;

import com.pingan.chain.common.ResponseMessage;
import com.pingan.chain.common.StateCode;
import com.pingan.chain.domain.*;
import com.pingan.chain.mapper.SmartContractsMapper;
import com.pingan.chain.service.SmartContractsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SmartContractsServiceImpl implements SmartContractsService {

	private static Logger log = LoggerFactory.getLogger(SmartContractsServiceImpl.class);
	
	@Resource
	private SmartContractsMapper smartContractsMapper;
	
	
	@Override
	public ResponseMessage<ChainAccount> queryCustomerInfo(String um, String productName) {
		log.info("查询用户的相关信息开始！");
		ResponseMessage<ChainAccount> result =  new ResponseMessage<ChainAccount>();
		try {
			ChainAccount chainAccount =  new ChainAccount();
			if(um == null){
				um = "aaa";
			}
			chainAccount = smartContractsMapper.queryCustomerInfo(um,productName);
			result.setCode(StateCode.Success.getCode());
			result.setMessage("查询用户的相关信息成功！");
			result.setData(chainAccount);
			log.info("查询用户的相关信息结束！");
		} catch (Exception e) {
			log.info("查询用户的相关信息异常！"+e);
			result.setCode(StateCode.Fail.getCode());
			result.setMessage("调用查询客户信息接口异常!");
			return result;
		}
		return result;
	}


	@Override
	public ResponseMessage<String> updateUserStatus(String um,String status) {
		log.info("更新用户状态信息开始！");
		ResponseMessage<String> result = new ResponseMessage<String>();
		try {
			smartContractsMapper.updateUserStatus(um, status);
		} catch (Exception e) {
			log.info("更新用户状态信息异常！");
			result.setCode(StateCode.Fail.getCode());
			result.setMessage("更新用户状态信息异常!");
			return result;
		}
		return result;
	}

	@Override
	public ResponseMessage<String> exchangeIntegral(ChangeIntegralInfo changeIntegralInfo) {
		log.info("用户积分兑换开始！");
		ResponseMessage<String> result = new ResponseMessage<String>();
		try {
			//查询用户状态
			ChainAccount chainAccount = smartContractsMapper.selectUserInfo(changeIntegralInfo.getUserId());
			if(CommonDefinition.frozenStatus.equals(chainAccount.getFrozen())){
				result.setCode(StateCode.Fail.getCode());
				result.setMessage("用户状态为冻结状态，不可兑换礼品！");
			}
			String returnValue = "true";
//			returnValue =  transferToUserByGroup(changeIntegralInfo.getUserAddress(),changeIntegralInfo.getExchangeBalance());
			if("true".equals(returnValue)){
				//用户兑换成功
				log.info("用户积分兑换成功！");
				smartContractsMapper.insertChangeIntegralInfo(changeIntegralInfo);
				//查询用户信息
				long balance = chainAccount.getBalance() - changeIntegralInfo.getExchangeBalance();
				chainAccount.setBalance(balance);
				smartContractsMapper.updateChainAccountInfo(chainAccount);
				result.setCode(StateCode.Success.getCode());
				result.setMessage("用户积分兑换成功！");
				return result;
			}else{
				result.setCode(StateCode.Fail.getCode());
				result.setMessage("用户积分兑换异常!");
				return result;
			}
		} catch (Exception e) {
			log.info("用户积分兑换异常！");
			result.setCode(StateCode.Fail.getCode());
			result.setMessage("用户积分兑换异常!");
			return result;
		}
	}


	@Override
	public ResponseMessage<String> transferAccounts(TransferRecordInfo transferRecordInfo) {
		log.info("转账接口开始！");
		ResponseMessage<String> result = new ResponseMessage<String>();
		try {
			ChainAccount chainAccount = smartContractsMapper.selectUserInfo(transferRecordInfo.getUserId());
			if(CommonDefinition.frozenStatus.equals(chainAccount.getFrozen())){
				result.setCode(StateCode.Fail.getCode());
				result.setMessage("用户状态为冻结状态，不可转账！");
			}
			String returnValue = "true";
//			returnValue =  transferToUserByGroup(changeIntegralInfo.getUserAddress(),changeIntegralInfo.getExchangeBalance());
			if("true".equals(returnValue)){
				//更新转账后用户信息表数据
				long balance = chainAccount.getBalance() + transferRecordInfo.getTransferBalance();
				chainAccount.setBalance(balance);
				smartContractsMapper.updateChainAccountInfo(chainAccount);
				//查询转账方数据信息
				ModelAccount modelAccount = new ModelAccount();
				modelAccount.setAddress(transferRecordInfo.getTransferLaunchAddress());
				modelAccount = smartContractsMapper.selectModelAccount(modelAccount);
				//更新转账方账户信息
				balance = modelAccount.getBalance() - transferRecordInfo.getTransferBalance();
				modelAccount.setBalance(balance);
				smartContractsMapper.updateModelAccount(modelAccount);
				result.setCode(StateCode.Success.getCode());
				result.setMessage("积分转账成功！");
				return result;
			}else{
				result.setCode(StateCode.Fail.getCode());
				result.setMessage("积分转账异常!");
				return result;
			}
		} catch (Exception e) {
			log.info("转账接口异常！");
			result.setCode(StateCode.Fail.getCode());
			result.setMessage("转账接口异常!");
			return result;
		}
	}


	@Override
	public ResponseMessage<String> setOrderRule(OrderRuleInfo orderRuleInfo) {
		log.info("设置先后事件规则信息接口开始！");
		ResponseMessage<String> result = new ResponseMessage<String>();
		try {
			//根据编号查询是否存在该事件
			OrderRuleInfo queryorderRuleInfo = new OrderRuleInfo();
			queryorderRuleInfo = smartContractsMapper.selectOrderRuleInfo(orderRuleInfo.getEventId());
			if(null == queryorderRuleInfo || queryorderRuleInfo.getEventId() ==""){//没有查询到有该事件信息
				smartContractsMapper.insertOrderRuleInfo(orderRuleInfo);
			}else{
				smartContractsMapper.updateOrderRuleInfo(orderRuleInfo);
			}
			result.setCode(StateCode.Success.getCode());
			result.setMessage("设置先后事件规则信息接口成功!");
			return result;
		} catch (Exception e) {
			log.info("设置先后事件规则信息接口异常！");
			result.setCode(StateCode.Fail.getCode());
			result.setMessage("设置先后事件规则信息接口异常!");
			return result;
		}
	}

	@Override
	public ResponseMessage<OrderRuleInfo> selectOrderRule(String eventId) {
		log.info("查询先后事件规则信息接口开始！");
		ResponseMessage<OrderRuleInfo> result = new ResponseMessage<OrderRuleInfo>();
		try {
			OrderRuleInfo orderRuleInfo = new OrderRuleInfo();
			orderRuleInfo = smartContractsMapper.selectOrderRuleInfo(orderRuleInfo.getEventId());
			int count  = orderRuleInfo.getCount()+1;
			orderRuleInfo.setCount(count);
			result.setCode(StateCode.Success.getCode());
			result.setMessage("查询先后事件规则信息接口成功!");
			return result;
		} catch (Exception e) {
			log.info("查询先后事件规则信息接口异常！");
			result.setCode(StateCode.Fail.getCode());
			result.setMessage("查询先后事件规则信息接口异常!");
			return result;
		}
	}
}
