package com.pingan.chain.domain;

import lombok.Data;

@Data
public class ChainAccount {

    private Integer id;

    private String userId;

    private String address;

    private Long balance;

    private Long exchange;

    private String fileName;

    private String password;


    private String frozen;


}
