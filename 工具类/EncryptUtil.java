package com.cignacmb.iuss.web.common.hmcUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * 通用加解密算法工具类<br>
 * 包含DES、DES3加解密算法、MD5加密算法和AES加解密算法
 * @class EncryptUtil
 * @package com.restlet.util
 * @creator MeiGaoBang
 * @createdate 2015-5-14 下午3:11:20
 */
public class EncryptUtil {

    /**
     * DES3加解密转换方式(算法/模式/填充)
     */
    private final static String DES3_TRANSFORMATION = "desede/CBC/PKCS5Padding";

    /**
     * 加解密统一使用的编码方式
     */
    private final static String CHARSET = "utf-8";
    
    /**
     * DES加密(兼容PHP)
     * @param data 待加密字符串
     * @param key 加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws Exception 运行时异常
     */
    public static String desEncrypt(String data, String key) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key.getBytes(CHARSET));
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DES3_TRANSFORMATION);
        IvParameterSpec ips = new IvParameterSpec(key.substring(0, 8).getBytes(CHARSET));
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(data.getBytes(CHARSET));
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(encryptData);
    }
    
    /**
     * DES解密(兼容PHP)
     * @param strMi 待解密字符串
     * @param key 解密私钥，长度不能够小于8位
     * @return 解密后的字符串
     * @throws Exception 运行时异常
     */
    public static String desDecrypt(String strMi, String key) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key.getBytes(CHARSET));
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DES3_TRANSFORMATION);
        IvParameterSpec ips = new IvParameterSpec(key.substring(0, 8).getBytes(CHARSET));
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decryptData = cipher.doFinal(decoder.decodeBuffer(strMi));
        return new String(decryptData, CHARSET);
    }

    /**
     * 测试用例
     * @param args
     * @creator MeiGaoBang
     * @createdate 2015-5-14 下午4:56:06
     */
    public static void main(String[] args) throws Exception {
        String plain = "1111112255";
        String desKey = "4de8e594480f4368a16ee657";
        System.out.println("1加密后："+desEncrypt(plain, desKey));
    }

}