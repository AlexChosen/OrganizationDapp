package com.pingan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pingan.chain.mapper")
public class ChainApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChainApplication.class, args);
	}

}
