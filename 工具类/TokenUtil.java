package com.cignacmb.iuss.web.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class TokenUtil {

	public static String generalToken(String prefix) {
		return prefix + UUID.randomUUID().toString();
	}
	
	public static String getUserToken(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    log.info("user-token 值为::"+request.getHeader("user-token"));
	    if (cookies == null) {
			log.info("cookies 为null 同时获取的 user-token 值为::"+request.getHeader("user-token"));

			return request.getHeader("user-token");
	    }
	    for (Cookie cookie : cookies) {
	     if (cookie.getName().equalsIgnoreCase("user-token")) {
	      return cookie.getValue();
	     }
	    }
//	    System.out.println(request.getHeaderNames());
	    return request.getHeader("user-token");
	   }
	
}
