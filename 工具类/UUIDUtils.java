package com.cignacmb.esales.utils;

import java.util.UUID;
/**
 * 
 *@Author:detang  
 *@Date:2018年10月29日 下午12:19:02
 */
public class UUIDUtils {
	 public static String getUUID(){
         return UUID.randomUUID().toString().replace("-", "");
    }
}
