package com.cignacmb.iuss.web.common.util;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

public class DataCheckUtil {

    /**
     * 对象字段空校验
     * @param params
     * @param obj
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
	public static String paramCheck(String [] params,Object obj) throws NoSuchFieldException, IllegalAccessException{
		for (String  param : params) {
	    	Field field = obj.getClass().getDeclaredField(param);
	    	field.setAccessible(true);//设置属性是可以访问的
	    	Object object = field.get(obj);//得到此属性的值
            if(StringUtils.isEmpty((String)object)){
            	return param;
            }
		}	 
		return null;
	}
}
