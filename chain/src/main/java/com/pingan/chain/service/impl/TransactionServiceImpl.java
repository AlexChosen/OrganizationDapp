package com.pingan.chain.service.impl;

import com.pingan.chain.mapper.ChainMappper;
import com.pingan.chain.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;
import static org.web3j.tx.Transfer.GAS_LIMIT;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    Admin admin;

    @Autowired
    Web3j web3j;

    @Autowired
    ChainMappper chainMappper;

    private static final String WallectPath="D:\\project\\geth\\data4\\keystore\\";

    private static final String OWNER_FILE="UTC--2019-07-28T13-52-14.467051000Z--b4ebc56d9f99256619313594cc8fcf697f90ce10";

    private static final String OWNER_ADDRESS = "0xb4ebc56d9f99256619313594cc8fcf697f90ce10";

    private static final String OWNER_PW = "123";

    private static BigInteger gasLimit = BigInteger.valueOf(21000);

    @Override
    public boolean sendEthTransaction(String from, String passWd, String to, long value) {
        try{
            //chainMappper.getModelAccount(from);

            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(OWNER_ADDRESS, OWNER_PW).send();
            if(personalUnlockAccount.accountUnlocked()){
                EthGetTransactionCount ethGetTransactionCount = admin.ethGetTransactionCount(OWNER_ADDRESS,
                        DefaultBlockParameterName.LATEST).sendAsync().get();
                BigInteger nonce = ethGetTransactionCount.getTransactionCount();

                EthGasPrice gasPrice = admin.ethGasPrice().send();
                BigInteger price = gasPrice.getGasPrice();

                Transaction transaction = Transaction.createEtherTransaction(OWNER_ADDRESS, nonce, price, gasLimit, to, BigInteger.valueOf(value));
                EthSendTransaction ethSendTransaction = admin.ethSendTransaction(transaction).send();
                System.out.println(ethSendTransaction.getTransactionHash());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean sendEthTransactionWallet(String from, String passWd, String to, long value){
        try {
            Credentials credentials = WalletUtils.loadCredentials(OWNER_PW, WallectPath + OWNER_FILE);

            EthGetTransactionCount ethGetTransactionCount = admin.ethGetTransactionCount(OWNER_ADDRESS,
                    DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();


            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, GAS_PRICE, GAS_LIMIT, to, BigInteger.valueOf(value));


            byte[] signedData = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedData);
            EthSendTransaction ethSendTransaction = admin.ethSendRawTransaction(hexValue).sendAsync().get();
            System.out.println(ethSendTransaction.getTransactionHash());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean sendEthTransfer(String from, String passWd, String to, long value){
        try {
            Credentials credentials = WalletUtils.loadCredentials(OWNER_PW, WallectPath + OWNER_FILE);
            TransactionReceipt receipt = Transfer.sendFunds(admin, credentials, to, BigDecimal.ONE, Convert.Unit.ETHER).sendAsync().get();
            System.out.println(receipt);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  true;
    }
}
