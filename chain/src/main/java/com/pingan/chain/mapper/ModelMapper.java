package com.pingan.chain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.chain.domain.ModelAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModelMapper extends BaseMapper<ModelAccount> {
    void frozenModel(@Param(value = "name") String name, @Param(value = "frozen") String frozen);
    ModelAccount getModelAccount(@Param(value = "name")String name);
    List<ModelAccount> getModelAccounts();
    void releasePoint(String modelName, Long addBanlance);
    void transferFromModel(String modelName, Long addBanlance);

}
