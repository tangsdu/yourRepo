package com.test.demo.consul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableDiscoveryClient //支持服务发现
public class ConsulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsulApplication.class, args);
	}

}

