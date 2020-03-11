package com.pingan.chain.service.impl;

import com.pingan.chain.contract.PlatformControl;
import com.pingan.chain.mapper.ChainMappper;
import com.pingan.chain.service.TransactionService;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;
import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static org.web3j.tx.Transfer.GAS_LIMIT;

@Service
public class TransactionServiceImpl implements TransactionService {
    Admin admin = Admin.build(new HttpService());

    @Value("${contract.address}")
    String contractAddress;

    private Credentials ownerCredentials;

    public TransactionServiceImpl(){
        String ownerWalletFile="\\UTC--2020-02-29T10-25-41.125814400Z--69df7c589c0d72a494b3d298c8b46f0e7e17a1ee";

        try{
            ownerCredentials = WalletUtils.loadCredentials("123",
                    WallectPath + ownerWalletFile);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Autowired
    ChainMappper chainMappper;

    private static final String WallectPath="D:\\project\\geth\\data5\\keystore\\";

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

    @Override
    public boolean deployContract(){

        try{
            System.out.println(admin.netVersion().send().getNetVersion());
            Credentials credentials = WalletUtils.loadCredentials(OWNER_PW, WallectPath+OWNER_FILE);
            PlatformControl platformControl = PlatformControl.deploy(admin, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT,
                    "TestCon", "PTS", BigInteger.valueOf(1), BigInteger.valueOf(1000000000)).sendAsync().get();


/*
            PlatformControl platformControl =PlatformControl.load(platformControl2.getContractAddress(),admin, credentials, GAS_PRICE, GAS_LIMIT);
*/
            if(!platformControl.isValid()){
                return false;
            }

            System.out.println("succ");
            TransactionReceipt receipt = platformControl.voteBegin("est", "test", BigInteger.ZERO).send();

        }catch (Exception e){
            System.out.println(e);
        }
        return true;
    }

    @Override
    public String ethCallTest(){
        try{
            testTransactionPolling();
            System.out.println("---------------");
            testTransactionQueuing();
            System.out.println("---------------");
            EthCall call =  ethCall();
            System.out.println(call.getValue());
            System.out.println(call.reverts());
            return call.getRevertReason();
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    private static final int COUNT = 4; // don't set too high if using a real Ethereum network
    private static final long POLLING_FREQUENCY = 1500;

    private void testTransactionPolling() throws Exception {

        List<Future<TransactionReceipt>> transactionReceipts = new LinkedList<>();
        FastRawTransactionManager transactionManager =
                new FastRawTransactionManager(
                        admin,
                        ownerCredentials,
                        new PollingTransactionReceiptProcessor(
                                admin, POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH));

        Transfer transfer = new Transfer(admin, transactionManager);
        BigInteger gasPrice = transfer.requestCurrentGasPrice();

        for (int i = 0; i < COUNT; i++) {

            Future<TransactionReceipt> transactionReceiptFuture =
                    createTransaction(transfer, gasPrice).sendAsync();
            transactionReceipts.add(transactionReceiptFuture);
        }

        for (int i = 0;
             i < DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH && !transactionReceipts.isEmpty();
             i++) {

            for (Iterator<Future<TransactionReceipt>> iterator = transactionReceipts.iterator();
                 iterator.hasNext(); ) {
                Future<TransactionReceipt> transactionReceiptFuture = iterator.next();

                if (transactionReceiptFuture.isDone()) {
                    TransactionReceipt transactionReceipt = transactionReceiptFuture.get();
                    System.out.println(transactionReceipt.getBlockHash().isEmpty());
                    iterator.remove();
                }
            }

            Thread.sleep(POLLING_FREQUENCY);
        }

        System.out.println(transactionReceipts.isEmpty());
    }

    public void testTransactionQueuing() throws Exception {

        Map<String, Object> pendingTransactions = new ConcurrentHashMap<>();
        ConcurrentLinkedQueue<TransactionReceipt> transactionReceipts =
                new ConcurrentLinkedQueue<>();

        FastRawTransactionManager transactionManager =
                new FastRawTransactionManager(
                        admin,
                        ownerCredentials,
                        new QueuingTransactionReceiptProcessor(
                                admin,
                                new Callback() {
                                    @Override
                                    public void accept(TransactionReceipt transactionReceipt) {
                                        transactionReceipts.add(transactionReceipt);
                                    }

                                    @Override
                                    public void exception(Exception exception) {}
                                },
                                DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH,
                                POLLING_FREQUENCY));

        Transfer transfer = new Transfer(admin, transactionManager);

        BigInteger gasPrice = transfer.requestCurrentGasPrice();

        for (int i = 0; i < COUNT; i++) {
            TransactionReceipt transactionReceipt = createTransaction(transfer, gasPrice).send();
            pendingTransactions.put(transactionReceipt.getTransactionHash(), new Object());
        }

        for (int i = 0;
             i < DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH && !pendingTransactions.isEmpty();
             i++) {
            for (TransactionReceipt transactionReceipt : transactionReceipts) {
                System.out.println((transactionReceipt.getBlockHash().isEmpty()));
                pendingTransactions.remove(transactionReceipt.getTransactionHash());
                transactionReceipts.remove(transactionReceipt);
            }

            Thread.sleep(POLLING_FREQUENCY);
        }

        System.out.println((transactionReceipts.isEmpty()));
    }



    private RemoteCall<TransactionReceipt> createTransaction(
            Transfer transfer, BigInteger gasPrice) {
        return transfer.sendFunds(
                "0x069a31b5e7ef337a829b8a034c4ce8afd2ab344c",
                BigDecimal.valueOf(1.0),
                Convert.Unit.KWEI,
                gasPrice,
                Transfer.GAS_LIMIT);
    }









    private EthCall ethCall() throws java.io.IOException {

        final Function function =
                new Function(
                        PlatformControl.FUNC_RELEASEDAILY,
                        Collections.singletonList(new Address(new String("0x069a31b5e7ef337a829b8a034c4ce8afd2ab344c"))),
                        Collections.emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        return admin.ethCall(
                Transaction.createEthCallTransaction(
                        ownerCredentials.getAddress(), contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .send();
    }

    @Override
    public String ethFilter() {
        Disposable disposable3=admin.pendingTransactionFlowable().subscribe(tx -> System.out.println(tx.getTransactionIndex()));
        disposable3.dispose();
        System.out.println("----------------");
        Disposable disposable = admin.blockFlowable(false).subscribe(ethBlock -> System.out.println(ethBlock.getBlock()));
        disposable.dispose();
        System.out.println("----------------");
        Disposable disposable2 = admin.transactionFlowable().subscribe(tx -> System.out.println(tx.getTransactionIndex()));
        disposable2.dispose();
        System.out.println("----------------");

        Disposable disposable4 = admin.replayPastBlocksFlowable(new DefaultBlockParameterNumber(0),
                new DefaultBlockParameterNumber(5), false)
                .subscribe(ethBlock -> System.out.println(ethBlock.getBlock()));
        disposable4.dispose();
        System.out.println("----------------");

        Disposable disposable5 = admin.replayPastTransactionsFlowable(new DefaultBlockParameterNumber(0),
                new DefaultBlockParameterNumber(5)).subscribe(tx -> System.out.println(tx.getTransactionIndex()));
        disposable5.dispose();
        System.out.println("----------------");

        EthBlock block = null;
        try {
            block = admin.ethGetBlockByNumber(DefaultBlockParameterName.LATEST,false).send();
        }catch (Exception e){
            e.printStackTrace();
        }
        BigInteger latestBlockNumber= block.getBlock().getNumber();

        Disposable disposable6=admin.replayPastAndFutureBlocksFlowable(new DefaultBlockParameterNumber(latestBlockNumber.subtract(BigInteger.ONE)),false).subscribe(ethBlock -> System.out.println(ethBlock.getBlock()));
        disposable6.dispose();
        System.out.println("----------------");

        Disposable disposable7=admin.replayPastAndFutureTransactionsFlowable(new DefaultBlockParameterNumber(latestBlockNumber.subtract(BigInteger.ONE))).subscribe(tx -> System.out.println(tx.getTransactionIndex()));
        disposable7.dispose();
        return "filter";
    }
}
