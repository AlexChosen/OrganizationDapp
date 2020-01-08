package com.pingan.chain.mapper;

import com.pingan.chain.domain.ChainAccount;
import com.pingan.chain.domain.ModelAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChainMappper {
    void insertUser(ChainAccount um);

    void insertModel(ModelAccount modelAccount);

    void frozenUser(@Param(value = "userId") String userId, @Param(value = "frozen") String frozen);

    void frozenModel(@Param(value = "name") String name,  @Param(value = "frozen") String frozen);

    ModelAccount getModelAccount(@Param(value = "name")String name);

    ChainAccount getChainAccount(@Param(value = "userId")String userId);

    void releasePoint(String modelName, Long addBanlance);

    void transferToUser(String userId, Long addBanlance);

    void transferFromModel(String modelName, Long addBanlance);

    void userWithdraw(String userId, Long addBanlance);

    List<ModelAccount> getModelAccounts();

    List<ChainAccount> getChainAccounts();
}
