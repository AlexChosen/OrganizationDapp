package com.pingan.chain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "model_account")
public class ModelAccount {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String address;

    private Long balance;

    private String frozen;

    @TableField(value = "release_time")
    private Date releaseTime;

    @TableField(value = "file_name")
    private String fileName;

    private String password;


}
