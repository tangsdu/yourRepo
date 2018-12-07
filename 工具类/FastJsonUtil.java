package com.cignacmb.iuss.web.common.hmcUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

/**
 * 通用JSON工具类
 * @author j1mei
 * 2017年1月6日 上午10:28:48
 */
public class FastJsonUtil {
    private FastJsonUtil(){}
    public static String toJson(Object object) {
        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }
    
    public static <T> T toBean(String jsonString, Class<T> cls) {
        return JSON.parseObject(jsonString, cls); 
    }
    
    public static <T> List<T> toList(String jsonString, Class<T> cls) {
        return JSON.parseArray(jsonString, cls);
    }
      
    public static List<Map<String, Object>> toListMap(String jsonString) {
        return JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>(){});
    }
    
    public static String toJsonWithFormat(Object object, String dateFormat) {
        if (null == dateFormat || "".equals(dateFormat)) return toJson(object);
        return JSON.toJSONStringWithDateFormat(object, dateFormat);
    }

    public static <T> Map<String, T> toMap(String jsonString) {
        return JSON.parseObject(jsonString, new TypeReference<Map<String, T>>(){});
    }
    
}
