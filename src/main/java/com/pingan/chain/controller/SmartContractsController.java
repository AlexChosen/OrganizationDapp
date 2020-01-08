package com.pingan.chain.controller;

import com.pingan.chain.common.ResponseMessage;
import com.pingan.chain.domain.*;
import com.pingan.chain.service.SmartContractsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SmartContractsController {

	@Autowired
	private SmartContractsService smartContractsService;

	@RequestMapping("/queryCustomerInfo")
    @ResponseBody
    public ResponseMessage<ChainAccount> queryCustomerInfo(String um, String productName){
    	ResponseMessage<ChainAccount> result =  new ResponseMessage<ChainAccount>();
    	result = smartContractsService.queryCustomerInfo(um, productName);
    	return result;
    }
	
	@RequestMapping("/thawUserStatus")
    @ResponseBody
    public ResponseMessage<String> thawUserStatus(String um){
    	ResponseMessage<String> result =  new ResponseMessage<String>();
    	String status = CommonDefinition.thawStatus;//状态为解冻状态
    	result = smartContractsService.updateUserStatus(um,status);
    	return result;
    }
	
	@RequestMapping("/frozenUserStatus")
    @ResponseBody
    public ResponseMessage<String> frozenUserStatus(String um){
    	ResponseMessage<String> result =  new ResponseMessage<String>();
    	String status = CommonDefinition.frozenStatus;//状态为冻结状态
    	result = smartContractsService.updateUserStatus(um,status);
    	return result;
    }
	
	@RequestMapping("/exchangeIntegral")
    @ResponseBody
    public ResponseMessage<String> exchangeIntegral(ChangeIntegralInfo changeIntegralInfo){
    	ResponseMessage<String> result =  new ResponseMessage<String>();
    	result = smartContractsService.exchangeIntegral(changeIntegralInfo);
    	return result;
    }
	
	@RequestMapping("/transferAccounts")
    @ResponseBody
    public ResponseMessage<String> transferAccounts(TransferRecordInfo transferRecordInfo){
    	ResponseMessage<String> result =  new ResponseMessage<String>();
    	result = smartContractsService.transferAccounts(transferRecordInfo);
    	return result;
    }
	
	@RequestMapping("/setOrderRule")
    @ResponseBody
    public ResponseMessage<String> setOrderRule(OrderRuleInfo orderRuleInfo){
    	ResponseMessage<String> result =  new ResponseMessage<String>();
    	result = smartContractsService.setOrderRule(orderRuleInfo);
    	return result;
    }
	
	@RequestMapping("/selectOrderRule")
    @ResponseBody
    public ResponseMessage<OrderRuleInfo> selectOrderRule(String eventId){
    	ResponseMessage<OrderRuleInfo> result =  new ResponseMessage<OrderRuleInfo>();
    	result = smartContractsService.selectOrderRule(eventId);
    	return result;
    }
}
