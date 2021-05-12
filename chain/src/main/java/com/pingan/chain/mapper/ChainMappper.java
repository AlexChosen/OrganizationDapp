package com.pingan.chain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.chain.domain.ChainAccount;
import com.pingan.chain.domain.ModelAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChainMappper extends BaseMapper<ChainAccount> {
    void frozenUser(@Param(value = "userId") String userId, @Param(value = "frozen") String frozen);

    ChainAccount getChainAccount(@Param(value = "userId")String userId);

    void transferToUser(String userId, Long addBanlance);

    void userWithdraw(String userId, Long addBanlance);

    List<ChainAccount> getChainAccounts();
}
