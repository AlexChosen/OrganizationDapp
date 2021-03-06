package com.pingan.chain.service.impl;

import com.pingan.chain.contract.PlatformControl;
import com.pingan.chain.domain.ChainAccount;
import com.pingan.chain.domain.ModelAccount;
import com.pingan.chain.mapper.ChainMappper;
import com.pingan.chain.service.ChainService;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ChainServiceImpl implements ChainService {

    @Resource
    ChainMappper chainMappper;

    @Autowired
    Web3j web3j;

    @Value("${contract.address}")
    String contractAddress;

    private Credentials ownerCredentials;

    private final String WallectPath="D:\\project\\geth\\data4\\keystore";



    public ChainServiceImpl() {
        try{
            ownerCredentials = WalletUtils.loadCredentials("123",
                    WallectPath+"\\UTC--2019-07-28T13-52-14.467051000Z--b4ebc56d9f99256619313594cc8fcf697f90ce10");

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void releaseDaily(String modelName) {
        Long addBanlance = 0L;
        ModelAccount account = chainMappper.getModelAccount(modelName);
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(), account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt send =contract.releaseDaily(account.getAddress()).send();
            addBanlance=Long.parseLong(send.getLogs().get(0).getData().substring(2),16);
            System.out.println(addBanlance);
        }catch (Exception e){
            e.printStackTrace();
        }
        chainMappper.releasePoint(modelName, addBanlance);
    }

    @Override
    public void registerUser(String um, String password) {
        try {
            String walletFileName = creatAccount(um, password);
            String address = loadAccount(walletFileName, password).getAddress();
            ChainAccount chainAccount = new ChainAccount();
            chainAccount.setAddress(address);
            chainAccount.setBalance(0L);
            chainAccount.setExchange(0L);
            chainAccount.setUserId(um);
            chainAccount.setFrozen("0");
            chainAccount.setFileName(walletFileName);
            chainAccount.setPassword(password);
            chainMappper.insertUser(chainAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addModel(String modelName, String password) {
        try {
            String walletFileName = creatAccount(modelName, password);
            Credentials credentials = loadAccount(walletFileName, password);
            String address= credentials.getAddress();
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt send = contract.addAuthorizedGroup(address).send();
            System.out.println(send);

            ModelAccount modelAccount = new ModelAccount();
            modelAccount.setAddress(address);
            modelAccount.setFrozen("0");
            modelAccount.setName(modelName);
            modelAccount.setBalance(0L);
            modelAccount.setFileName(walletFileName);
            modelAccount.setPassword(password);
            chainMappper.insertModel(modelAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void frozenUser(String um, Boolean isFrozen) {
        ChainAccount account = chainMappper.getChainAccount(um);
        try {
            //调用合约方法
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(), account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            contract.freezeAccount(account.getAddress(), isFrozen).send();
            if (isFrozen) {
                //冻结
                chainMappper.frozenUser(um, "1");
            } else {
                //解冻
                chainMappper.frozenUser(um, "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void frozenModel(String modelName) {
        try {
            ModelAccount account = chainMappper.getModelAccount(modelName);

            //调用合约方法
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(), account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt send = contract.removeAuthorizedGroup(account.getAddress()).send();
            System.out.println(send);
            chainMappper.frozenModel(modelName, "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transferByModel(String model, String user, long amount){
        ModelAccount account = chainMappper.getModelAccount(model);
        try {
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(),account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, credentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt send = contract.transferToUserByGroup(userNameToAddress(user), BigInteger.valueOf(amount)).send();
            chainMappper.transferToUser(user, amount);
            chainMappper.transferFromModel(model, amount);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void userWithdraw(String model,String userId, long amount){
        ModelAccount account = chainMappper.getModelAccount(model);
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(),account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, credentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt send = contract.userPayByGroup(userNameToAddress(userId), BigInteger.valueOf(amount)).send();
            chainMappper.userWithdraw(userId, amount);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ModelAccount getModel(String model) {
        return chainMappper.getModelAccount(model);
    }

    @Override
    public ChainAccount getUser(String user) {
        return chainMappper.getChainAccount(user);
    }

    @Override
    public List<HashMap<String, Long>> getAllowance(String user) {
        List<ModelAccount> models=null;
        List<HashMap<String, Long>> results= new ArrayList<>();
        try{
            models = getModels();
        }catch (Exception e){
            e.printStackTrace();
        }
        for(ModelAccount model:models){
            long value=getUserAllowance(userNameToAddress(user), modelNameToAddress(model.getName()));
            HashMap<String, Long> result = new HashMap<>();
            result.put(model.getName(), value);
            results.add(result);
        }

        return results;
    }

    private long getUserAllowance(String user, String model){
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            BigInteger a = contract.allowance(user, model).send();
            return a.longValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<ModelAccount> getModels() {
        return chainMappper.getModelAccounts();
    }

    @Override
    public List<ChainAccount> getUsers() {
        return chainMappper.getChainAccounts();
    }

    @Override
    public long getOriginPTS() {
        return 1000000000;
    }

    @Override
    public long getTotalPoints() {
        try {

            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            BigInteger a = contract.totalSupply().send();
            return a.longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getRemainPoints() {
        try {
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            BigInteger a = contract.balanceOf(ownerCredentials.getAddress()).send();
            return a.longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getDailyRelease() {
        try {
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            BigInteger a = contract.dailyReleaseAmount().send();
            return a.longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean startVote(String issueName, int type) {
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt receipt = new TransactionReceipt();
            receipt.setFrom("123");
            receipt = contract.voteBegin(issueName, BigInteger.valueOf(type)).send();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean voteForIssue(String issueName, String model, boolean view) {
        ModelAccount account = chainMappper.getModelAccount(model);
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(),account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, credentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt receipt = contract.voteByGroup(issueName, view).send();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean calcVote(String issueName) {
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasPrice, BigInteger.valueOf(3000000));
            TransactionReceipt receipt = contract.calcVoteResult(issueName).send();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private String creatAccount(String um, String password) throws Exception {
        String basePath = WallectPath;
        //钱包文件保存路径，请替换位自己的某文件夹路径
        String walletFileName = WalletUtils.generateNewWalletFile(password, new File(basePath, "/"), false);
        System.out.println("=====path=====" + basePath);
        System.out.println("=====web3j wallet file name ===" + walletFileName);
        return walletFileName;
    }

    private Credentials loadAccount(String fileName, String password) throws Exception {
        String path = WallectPath + "/" + fileName;
        Credentials credentials = WalletUtils.loadCredentials(
                password, path);
        System.out.println("====web3j====address====" + credentials.getAddress());
        return credentials;
    }

    /**
     * 获取项目根路径
     *
     * @return
     */
    private String getResourceBasePath() {
        // 获取跟目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            // nothing to do
        }
        if (path == null || !path.exists()) {
            path = new File("");
        }

        String pathStr = path.getAbsolutePath();
        // 如果是在eclipse中运行，则和target同级目录,如果是jar部署到服务器，则默认和jar包同级
        pathStr = pathStr.replace("\\target\\classes", "");

        return pathStr;
    }

    private String userNameToAddress(String name){
        return getUser(name).getAddress();
    }

    private String modelNameToAddress(String name){
        return getModel(name).getAddress();
    }
}
