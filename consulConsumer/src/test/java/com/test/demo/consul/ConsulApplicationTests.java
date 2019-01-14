package com.test.demo.consul;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsulApplicationTests {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	/**
	 * 获取所有服务 由consul实现提供
	 */
	@Test
	public void services() {
	List list =	discoveryClient.getInstances("service-consul");
	System.out.println("---------list:"+list);
	}
	
	

}

