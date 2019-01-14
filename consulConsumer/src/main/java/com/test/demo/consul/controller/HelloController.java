package com.test.demo.consul.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author detang
 *
 */
@RestController
public class HelloController {
	
	@Autowired
	private LoadBalancerClient loadBalancerClient;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	 /**
	  * 使用：Discovery Client方式
     * 获取所有服务
     */
	@RequestMapping("/services")
	public Object services(){
		return discoveryClient.getInstances("service-consul");
	}
	/**
     * 从所有服务中选择一个服务（轮询）
     */
	@RequestMapping("/discover")
	public Object discover() {
	        return loadBalancerClient.choose("service-consul").getUri().toString();
	}
	
	

}
