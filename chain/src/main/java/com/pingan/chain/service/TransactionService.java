package com.pingan.chain.service;

public interface TransactionService {
    boolean sendEthTransaction(String from, String passWd, String to, long value);
    boolean sendEthTransactionWallet(String from, String passWd, String to, long value);
     boolean sendEthTransfer(String from, String passWd, String to, long value);
     boolean deployContract();
    }
