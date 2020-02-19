package com.pingan.chain.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ModelAccount {

    private Integer id;

    private String name;

    private String address;

    private Long balance;

    private String frozen;

    private Date releaseTime;

    private String fileName;

    private String password;


}
