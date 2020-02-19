package com.pingan.chain.controller;

import com.pingan.chain.domain.ChainResponse;
import com.pingan.chain.domain.ModelAccount;
import com.pingan.chain.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.protocol.Web3j;

import javax.annotation.Resource;

@Controller
public class ChainController {

    @Autowired
    private ChainService chainService;


    @RequestMapping("/registerUser")
    @ResponseBody
    public ChainResponse registerUser(String um, String password) {

        chainService.registerUser(um, password);
        return new ChainResponse();
    }

    @RequestMapping("/addModel")
    @ResponseBody
    public ChainResponse addModel(String modelName, String password) {

        chainService.addModel(modelName, password);
        return new ChainResponse();
    }

    /**
     * 用户冻结
     * @param um
     * @param isFrozen
     * @return
     */
    @RequestMapping("/frozenUser")
    @ResponseBody
    public ChainResponse frozenUser(String um,Boolean isFrozen ) {
        System.out.println("接口进来了");
        chainService.frozenUser(um,isFrozen);
        return new ChainResponse();
    }

    @RequestMapping("/frozenModel")
    @ResponseBody
    public ChainResponse frozenModel(String modelName) {

        chainService.frozenModel(modelName);
        return new ChainResponse();
    }


    @RequestMapping("/releaseDaily")
    @ResponseBody
    public ChainResponse releaseDaily(String modelName) {

        chainService.releaseDaily(modelName);
        return new ChainResponse();
    }

    @RequestMapping("/transfer")
    @ResponseBody
    public ChainResponse transferByModel(String model, String user, long amount) {

        chainService.transferByModel(model,user, amount);
        return new ChainResponse();
    }

    @RequestMapping("/withdraw")
    @ResponseBody
    public ChainResponse userWithdraw(String modelName,String user, long amount) {

        chainService.userWithdraw(modelName, user, amount);
        return new ChainResponse();
    }


    @RequestMapping("/getModel")
    @ResponseBody
    public ChainResponse getModel(String modelName) {

        return new ChainResponse<>(chainService.getModel(modelName));
    }

    @RequestMapping("/getAllowance")
    @ResponseBody
    public ChainResponse getAllowance(String user) {
        return new ChainResponse<>(chainService.getAllowance(user));
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public ChainResponse getUser(String user) {
        return new ChainResponse<>(chainService.getUser(user));
    }

    @RequestMapping("/getModels")
    @ResponseBody
    public ChainResponse getModels() {
        return new ChainResponse<>(chainService.getModels());
    }

    @RequestMapping("/getUsers")
    @ResponseBody
    public ChainResponse getUsers() {
        return new ChainResponse<>(chainService.getUsers());
    }

    @RequestMapping("/hello")
    public String hello(){
        return "/pages/hello.html";
    }


    @RequestMapping("/originPTS")
    @ResponseBody
    public ChainResponse getOriginPTS() {
        return new ChainResponse<>(chainService.getOriginPTS());
    }

    @RequestMapping("/totalPTS")
    @ResponseBody
    public ChainResponse getTotalPTS() {
        return new ChainResponse<>(chainService.getTotalPoints());
    }



    @RequestMapping("/remainPTS")
    @ResponseBody
    public ChainResponse getRemainPTS() {
        return new ChainResponse<>(chainService.getRemainPoints());
    }

    @RequestMapping("/dailyRelease")
    @ResponseBody
    public ChainResponse getDailyRelease() {
        return new ChainResponse<>(chainService.getDailyRelease());
    }


    @RequestMapping("/startVote")
    @ResponseBody
    public ChainResponse startVote(String issueName, String info, int type) {
        return new ChainResponse<>(chainService.startVote(issueName, info, type));
    }


    @RequestMapping("/voteForIssue")
    @ResponseBody
    public ChainResponse voteForIssue(String issueName, String model, boolean view) {
        return new ChainResponse<>(chainService.voteForIssue(issueName,model, view));
    }

    @RequestMapping("/calcVote")
    @ResponseBody
    public ChainResponse calcVote(String issueName) {
        return new ChainResponse<>(chainService.calcVote(issueName));
    }
}
