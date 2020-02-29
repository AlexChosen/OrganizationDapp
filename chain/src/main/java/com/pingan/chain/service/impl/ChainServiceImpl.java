package com.pingan.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.pingan.chain.contract.PlatformControl;
import com.pingan.chain.domain.ChainAccount;
import com.pingan.chain.domain.ModelAccount;
import com.pingan.chain.mapper.ChainMappper;
import com.pingan.chain.mapper.ModelMapper;
import com.pingan.chain.service.ChainService;
import org.apache.ibatis.annotations.Param;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.web3j.abi.EventValues;
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
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

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
    ModelMapper modelMapper;

    Web3j web3j = Web3j.build(new HttpService());

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
        ModelAccount account = modelMapper.getModelAccount(modelName);
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(), account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
            TransactionReceipt send =contract.releaseDaily(account.getAddress()).send();
            for(PlatformControl.TransferEventResponse response:contract.getTransferEvents(send)){
                System.out.println(response);
                addBanlance = response.value.longValue();
            }
            System.out.println(addBanlance);
        }catch (Exception e){
            e.printStackTrace();
        }
        modelMapper.releasePoint(modelName, addBanlance);
    }

    @Override
    public void registerUser(String um, String password) {
        try {
            ChainAccount account = chainMappper.selectOne(new QueryWrapper<ChainAccount>().eq("name",um));
            if(account!=null){
                System.out.println("account exist: "+um);
                return;
            }
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
            chainMappper.insert(chainAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addModel(String modelName, String password) {
        try {
            ModelAccount model = modelMapper.selectOne(new QueryWrapper<ModelAccount>().eq("name", modelName));
            if(model!=null){
                System.out.println("model exist: "+ modelName);
                return ;
            }
            String walletFileName = creatAccount(modelName, password);
            Credentials credentials = loadAccount(walletFileName, password);
            String address= credentials.getAddress();
            ContractGasProvider gasProvider = new  DefaultGasProvider();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, gasProvider);
            TransactionReceipt send = contract.addAuthorizedGroup(address).send();
            for(PlatformControl.AddGroupEventResponse response: contract.getAddGroupEvents(send)){
                System.out.println(response.group);
            }


            ModelAccount modelAccount = new ModelAccount();
            modelAccount.setAddress(address);
            modelAccount.setFrozen("0");
            modelAccount.setName(modelName);
            modelAccount.setBalance(0L);
            modelAccount.setFileName(walletFileName);
            modelAccount.setPassword(password);
            modelMapper.insert(modelAccount);
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
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
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
            ModelAccount account = modelMapper.getModelAccount(modelName);

            //调用合约方法
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
            System.out.println(contract.isValid());
            TransactionReceipt send = contract.removeAuthorizedGroup(account.getAddress()).send();
            List<PlatformControl.RemoveGroupEventResponse> result = contract.getRemoveGroupEvents(send);

            System.out.println(send);
            modelMapper.frozenModel(modelName, "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transferByModel(String model, String user, long amount){
        ModelAccount account = modelMapper.getModelAccount(model);
        try {
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(),account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, credentials, new DefaultGasProvider());
            TransactionReceipt send = contract.transferToUserByGroup(userNameToAddress(user), BigInteger.valueOf(amount)).send();

            chainMappper.transferToUser(user, amount);
            modelMapper.transferFromModel(model, amount);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void userWithdraw(String model,String userId, long amount){
        ModelAccount account = modelMapper.getModelAccount(model);
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(),account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, credentials, new DefaultGasProvider());
            TransactionReceipt send = contract.userPayByGroup(userNameToAddress(userId), BigInteger.valueOf(amount)).send();
            chainMappper.userWithdraw(userId, amount);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ModelAccount getModel(String model) {
        ModelAccount account = new ModelAccount();
        account.setName(model);

        return modelMapper.selectOne(new QueryWrapper<ModelAccount>().eq("name", model));
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
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
            BigInteger a = contract.allowance(user, model).send();
            return a.longValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<ModelAccount> getModels() {
        return modelMapper.getModelAccounts();
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
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
            System.out.println(contract.isValid());
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
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
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
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
            BigInteger a = contract.dailyReleaseAmount().send();
            return a.longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean startVote(String issueName, String info,int type) {
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
            TransactionReceipt receipt = contract.voteBegin(issueName,info,BigInteger.valueOf(type)).send();
            for(PlatformControl.BeginVoteEventResponse response : contract.getBeginVoteEvents(receipt)){
                System.out.println(response.voteTime+":"+response.issue);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean voteForIssue(String issueName, String model, boolean view) {
        ModelAccount account = modelMapper.getModelAccount(model);
        try{
            BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
            Credentials credentials = loadAccount(account.getFileName(),account.getPassword());
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, credentials, new DefaultGasProvider());
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
            PlatformControl contract = PlatformControl.load(contractAddress, web3j, ownerCredentials, new  DefaultGasProvider());
            TransactionReceipt receipt = contract.calcVoteResult(issueName).send();
            for(PlatformControl.CalcVoteEventResponse response:contract.getCalcVoteEvents(receipt)){
                System.out.println(response.issue + ":" + response.result);
            }
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
