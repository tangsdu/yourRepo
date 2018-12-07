package com.cignacmb.iuss.web.common.hmcUtil;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.*;

/**
 * 通用签名算法工具类
 * @author j1mei
 * 2016年7月13日 下午12:04:47
 */
@SuppressWarnings("rawtypes")
public class SignUtil {

    /** 日志对象 **/
    private static final Logger LOG = Logger.getLogger(SignUtil.class);
    
    /** 十六进制数组静态常量 **/
    private final static String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    /** 签名Key，配置在数据库 **/
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 默认构造方法
     */
    public SignUtil() {
    }

    /**
     * 带参数构造方法
     * @param key 签名Key
     */
    public SignUtil(String key) {
        setKey(key);
    }

    /**
     * 字节数组转换16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for(byte aB : b){
            builder.append(byteToHexString(aB));
        }
        return builder.toString();
    }

    /**
     * 转换byte到16进制
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if(n<0){
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1]+HEX_DIGITS[d2];
    }

    /**
     * MD5编码
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     * @throws Exception 异常
     */
    public static String encodeByMd5(String origin) throws Exception {
        String resultString = origin;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(resultString.getBytes("UTF-8"));
        resultString = byteArrayToHexString(md.digest());
        return resultString;
    }

    /**
     * 签名算法
     * @param o 要参与签名的数据对象
     * @return 签名字符串
     * @throws Exception 异常
     */
    public String buildSign(Object o) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if(isNotNull(field.get(o))){
                list.add(field.getName()+"="+field.get(o)+"&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<size;i++){
            builder.append(arrayToSort[i]);
        }
        String result = builder.toString();
        result += "key="+getKey();
        LOG.info(""+result+"");
        result = encodeByMd5(result).toUpperCase();
        LOG.info(""+result+"");
        return result;
    }

    /**
     * 签名算法
     * @param map 参数MAP
     * @return 签名字符串
     * @throws Exception 异常
     */
    public String buildSign(Map<String, Object> map) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String, Object> entry : map.entrySet()){
            Object obj = entry.getValue();
            if(isNotNull(obj) && !"sign".equals(entry.getKey())){
                list.add(entry.getKey()+"="+obj+"&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<size;i++){
            builder.append(arrayToSort[i]);
        }
        String result = builder.toString();
        result += "key="+getKey();
        LOG.info("签名字符串:"+result+"");
        result = encodeByMd5(result).toUpperCase();
        LOG.info("MD5签名字符串："+result+"");
        return result;
    }

    /**
     * 验签算法
     * @param notifyParams 验签参数
     * @return true-验签通过  false-验签失败
     * @throws Exception 异常
     */
    public Boolean checkSign(Map<String, Object> notifyParams) throws Exception {
        String signFromAPIResponse = (String)notifyParams.get("sign");
        if(isBlank(signFromAPIResponse)){
            LOG.info("sign from api interface does not exist");
            return false;
        }
        LOG.info("sign from api interface is "+signFromAPIResponse+"");
        /** 清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名 **/
        notifyParams.remove("sign");
        /** 将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较 **/
        String signForAPIResponse = buildSign(notifyParams);
        if(!signForAPIResponse.equals(signFromAPIResponse)){
            /** 签名验不过，表示这个API返回的数据有可能已经被篡改了 **/
            LOG.info("sign from api interface is incorrect");
            return false;
        }
        LOG.info("success, sign is correct");
        return true;
    }
    
    /**
     * 判断对象是否为空
     * @param obj 验证的对象
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:07:00
     */
    public static boolean isNull(Object obj) {
        return (null == obj || "".equals(obj)) ? true : false;
    }

    /**
     * 判断对象是否不为空
     * @param obj 验证的对象
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:07:00
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }
    
    /**
     * 判断字符串是否为空
     * @param str 验证字符串
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:05:26
     */
    public static boolean isBlank(String str) {
        if (null == str || "".equals(str) || str.length() == 0
                || "null".equals(str) || "".equals(str.replaceAll(" ", ""))) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断字符串是否不为空
     * @param str 验证字符串
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:05:26
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 测试用例
     * @param args 参数
     * @throws Exception 异常
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("channelSrc", "BAILIHUI");
        params.put("dataSrc", "WEB");
        params.put("userId", "6033123");
        Map<String, Object> integralMap = new HashMap<String, Object>();
        integralMap.put("totalIntegral", "8000");
        integralMap.put("totalCount", "1");
        List<Map<String, Object>> detaiList = new ArrayList<Map<String, Object>>();
        Map<String, Object> detailMap = new HashMap<String, Object>();
        detailMap.put("changeIntegral", "8000");
        detailMap.put("businessType", "EXCHANGE");
        detailMap.put("productCode", "ASACDVFERG");
        detailMap.put("changeType", "reduce");
        detaiList.add(detailMap);
        Map<String, Object> detailMap2 = new HashMap<String, Object>();
        detailMap2.put("changeIntegral", "8000");
        detailMap2.put("changeType", "reduce");
        detailMap2.put("businessType", "EXCHANGE");
        detailMap2.put("productCode", "ASACDVFERG");
        detaiList.add(detailMap2);
        integralMap.put("integralDetailList", detaiList);
        params.put("integralData", integralMap);
        String key = "96212f8bea9b45b199e9e224cf600ba3";
        SignUtil signUtil = new SignUtil(key);
        signUtil.buildSign(params);
        
        String lastSign = "CD9FB5A4F3068C16F6D5915E485E754A";
        LOG.info("   params sign result ("+lastSign+")");
        String jsonString = FastJsonUtil.toJson(params);
        LOG.info("Json字符串:"+jsonString);
        Map<String, Object> newMap = FastJsonUtil.toBean(jsonString, Map.class);
        LOG.info("Mapa数据    :"+newMap);
        Map<String, Object> map = (Map<String, Object>)newMap.get("integralData");
        Object object = map.get("integralDetailList");
        System.out.println(object);
        List<Map<String, Object>> list = (List<Map<String, Object>>)object;
        for (Map<String, Object> map2 : list) {
            System.out.println(map2.get("businessType"));
        }
        System.out.println(list);
    }
    
}
