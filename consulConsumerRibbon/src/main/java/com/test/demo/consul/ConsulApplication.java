package com.test.demo.consul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient //支持服务发现 ,通过@EnableDiscoveryClient向服务中心注册
@EnableFeignClients   //Feign用的是@EnableFeignClients 表示会自动扫描所有带@feignCleint注解的类处理
public class ConsulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsulApplication.class, args);
	}
	
	@Bean         //将对象注册为bean
	@LoadBalanced //表明这个restRemplate开启负载均衡的功能。
	public RestTemplate  restTemplate (){
		return new RestTemplate();
	}

}

