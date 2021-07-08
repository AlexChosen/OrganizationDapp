package com.pingan.chain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class KungFuHeroServiceImpl {
    Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/028ff208ea7146dca229802fd115063c"));

    public final String KungFuHero = "";
    public final String BAYC = "0xBC4CA0EdA7647A8aB7C2061c2E118A18a936f13D";
    public final String Meebits = "0x7Bd29408f11D2bFC23c34f18275bBf23bB716Bc7";


    public int balanceOfAddress(String contractAddress, String userAddress)  {
        int tokenNum = 0;

        Function function = new Function(
                        "balanceOf",
                        Arrays.asList(new Address(userAddress)),
                        Arrays.asList(new TypeReference<Uint256>() {})
                );
        String encodedFunction = FunctionEncoder.encode(function);

        try {
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST).sendAsync().get();

            List<Type> result = FunctionReturnDecoder.decode(
                    response.getValue(), function.getOutputParameters());
            tokenNum = Integer.parseInt(result.get(0).getValue().toString());

        }catch (Exception e){
            log.error("ethCall error {}", e.getMessage());
        }
        return tokenNum;
    }


    public void test(){
        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            String web3ClientVersionString = web3ClientVersion.getWeb3ClientVersion();
            System.out.println("Web3 client version: " + web3ClientVersionString);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        KungFuHeroServiceImpl kungFuHeroService = new KungFuHeroServiceImpl();
        //测试节点的可用性
        kungFuHeroService.test();

        System.out.println(System.currentTimeMillis());
        int num1 = kungFuHeroService.balanceOfAddress(kungFuHeroService.BAYC, "0xccFCEecf8451D8Cfd1Edd0b859c4301F60D3E948");
        System.out.println(System.currentTimeMillis());
        System.out.println(num1);
        int num2 = kungFuHeroService.balanceOfAddress(kungFuHeroService.Meebits, "0xccFCEecf8451D8Cfd1Edd0b859c4301F60D3E948");
        System.out.println(System.currentTimeMillis());
        System.out.println(num2);
    }
}

