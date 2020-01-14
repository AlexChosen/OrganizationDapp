package com.pingan.chain.controller;

import com.pingan.chain.domain.ChainResponse;
import com.pingan.chain.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping("/sendEth")
    public ChainResponse sendEth(String from, String passWd, String to, long value){
        boolean result = transactionService.sendEthTransaction(from, passWd, to, value);
        return new ChainResponse(result);
    }

    @GetMapping("/sendRawEth")
    public ChainResponse sendRawEth(String from, String passWd, String to, long value){
        boolean result = transactionService.sendEthTransactionWallet(from, passWd, to, value);
        return new ChainResponse(result);
    }

    @GetMapping("/sendEthTransfer")
    public ChainResponse sendEthTransfer(String from, String passWd, String to, long value){
        boolean result = transactionService.sendEthTransfer(from, passWd, to, value);
        return new ChainResponse(result);
    }
}
