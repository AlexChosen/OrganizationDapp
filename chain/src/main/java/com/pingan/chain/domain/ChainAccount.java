package com.pingan.chain.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "chain_account")
public class ChainAccount {

    private Integer id;

    @TableField(value = "user_id")
    private String userId;

    private String address;

    private Long balance;

    private Long exchange;

    @TableField(value = "file_name")
    private String fileName;

    private String password;

    private String frozen;

}
