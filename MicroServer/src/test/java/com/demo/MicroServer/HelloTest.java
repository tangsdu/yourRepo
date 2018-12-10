package com.demo.MicroServer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;




@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MicroServerApplication.class)
//@WebAppConfiguration  开启web配置
public class HelloTest {
	
	@Test
	public void test(){
		System.out.println("1213213");
	}
}
