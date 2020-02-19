package com.pingan.chain;

import com.pingan.chain.domain.ModelAccount;
import com.pingan.chain.mapper.ChainMappper;
import com.pingan.chain.mapper.ModelMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChainApplicationTests {
	@Autowired
	ChainMappper chainMappper;

	@Autowired
	ModelMapper modelMapper;

	@Test
	public static void main(String[] args) {
		ChainApplicationTests tests = new ChainApplicationTests();
		List<ModelAccount> accounts = tests.modelMapper.selectList(null);
		accounts.forEach(System.out::println);
	}

	@Test
	public void test(){
		List<ModelAccount> accounts = modelMapper.selectList(null);
		accounts.forEach(System.out::println);
	}

}
