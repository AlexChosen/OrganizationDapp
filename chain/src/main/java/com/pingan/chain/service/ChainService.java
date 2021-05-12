package com.pingan.chain.service;

import com.pingan.chain.domain.ChainAccount;
import com.pingan.chain.domain.ModelAccount;

import java.util.HashMap;
import java.util.List;

public interface ChainService {


    boolean releaseDaily(String modelName);

    void frozenModel(String modelName);

    void addModel(String modelName, String password);

    void frozenUser(String um,Boolean isFrozen);

    boolean registerUser(String um, String password);

    void transferByModel(String model, String user, long amount);

    void userWithdraw(String model, String userId, long amount);

    ModelAccount getModel(String model);

    ChainAccount getUser(String user);

    List<HashMap<String, Long>> getAllowance(String user);

    List<ModelAccount> getModels();

    List<ChainAccount> getUsers();

    long getOriginPTS();

    long getTotalPoints();

    long getRemainPoints();

    long getDailyRelease();

    boolean startVote(String issueName, String info,int type);

    boolean voteForIssue(String issueName, String model, boolean view);

    boolean calcVote(String issueName);
}
