package com.test.demo.consul.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author detang
 *
 */
@RestController
public class HelloController {
	
	@RequestMapping("/hello")
	public String helloWord(){
		return "hello consul Two";
	}

}
