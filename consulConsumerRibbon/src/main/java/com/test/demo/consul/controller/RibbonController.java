package com.test.demo.consul.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.test.demo.consul.service.HelloFeignService;

@RestController
public class RibbonController {
	
	/**
	 * 注入RestTemplate
	 */
	@Autowired
	private RestTemplate restTemplate;
	

	 @Autowired
	 private LoadBalancerClient loadBalancerClient;
	 
	 @Autowired
	 private HelloFeignService helloFeignService;
	 /**
	  * 使用resttemplate方式 1
	  * @return
	  */
	@RequestMapping("/consulTest")
	public String helloWord(){
		 	ServiceInstance serviceInstance = loadBalancerClient.choose("service-consul");
	        System.out.println("服务地址：" + serviceInstance.getUri());
	        System.out.println("服务名称：" + serviceInstance.getServiceId());
	        String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hello", String.class);
	        System.out.println(callServiceResult);
	        return callServiceResult;

	}
	
	/**
	 * 使用resttemplate + 方式
	 * @return
	 */
	@RequestMapping("/consulTemp")
	public String helloWord2(){			
		return this.restTemplate.getForObject("http://service-consul/hello",String.class);

	}
	
	/**
	 * 使用feign方式调用远程接口
	 */
	@RequestMapping(value = "/feignWord")
	public String feignWord(){
		return helloFeignService.getHello();
	}
	
}
