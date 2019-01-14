package com.test.demo.consul.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "service-consul",decode404 = true)
public interface HelloFeignService {
	
	@RequestMapping(value = "/hello",method = RequestMethod.GET)
	String getHello();

}
