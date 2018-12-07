package com.cignacmb.iuss.web.common.util;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AES加解密工具类(主要针对 配置文件加密PCIDSS)
 * 
 * @author r6yuxx
 *
 */
public class AesUtils {
	public static final String AES_DEF_ENCODE_KEY = "cignacmb-abcdefghi-1234567890";
	/**
	 * 密钥算法
	 */
	private static final String KEY_ALGORITHM = "AES";
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	// 加密
	public static String encrypt(String sKey, String sSrc) throws Exception {
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// "算法/模式/补码方式"
		cipher.init(Cipher.ENCRYPT_MODE, genKey(stringToByteArray(sKey)));
		byte[] encrypted = cipher.doFinal(stringToByteArray(sSrc));
		return Base64.encodeBase64String(encrypted);// 此处使用BAES64做转码功能
	}

	// 解密
	public static String decrypt(String sKey, String sSrc) throws Exception {
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, genKey(stringToByteArray(sKey)));
		byte[] original = cipher.doFinal(Base64.decodeBase64(sSrc));
		return byteArrayToString(original);
	}

	/**
	 * 生成密钥
	 * 
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	protected static Key genKey(byte[] key) throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(key);
		kg.init(128, random);
		SecretKey secretKey = kg.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		return new SecretKeySpec(enCodeFormat, KEY_ALGORITHM);
	}

	/**
	 * Converts a String into a byte array using UTF-8, falling back to the
	 * platform's default character set if UTF-8 fails.
	 * 
	 * @param input
	 *            the input (required)
	 * @return a byte array representation of the input string
	 */
	public static byte[] stringToByteArray(String input) {
		try {
			return input.getBytes("UTF-8");
		} catch (UnsupportedEncodingException fallbackToDefault) {
			return input.getBytes();
		}
	}

	/**
	 * Converts a byte array into a String using UTF-8, falling back to the
	 * platform's default character set if UTF-8 fails.
	 * 
	 * @param byteArray
	 *            the byte array to convert (required)
	 * @return a string representation of the byte array
	 */
	public static String byteArrayToString(byte[] byteArray) {
		try {
			return new String(byteArray, "UTF8");
		} catch (final UnsupportedEncodingException e) {
			return new String(byteArray);
		}
	}

	public static void main(String[] args) throws Exception {
		String source = "ECS";
		System.out.println(source);
		System.out.println(AesUtils.encrypt(AES_DEF_ENCODE_KEY, source));
		String value = AesUtils.encrypt(AES_DEF_ENCODE_KEY, source);
		System.out.println(AesUtils.decrypt(AES_DEF_ENCODE_KEY, value));
		
		//---------------
//		String s1 = "URL1&loginflag=no&rsa_random_num=AAAAAA&rsa_time_flag=HHMMSS";
//		//使用MD5算法生成摘要
//		String md5Encode = MD5Util.MD5Encode(s1, "utf-8");
//		System.out.println("摘要 ： " + md5Encode);
//		//使用私钥对摘要进行签名
//		PrivateKey privateKey = (PrivateKey) CertUtil.getKey("D:\\cer\\epay-rsa.pfx", "PFX", "epay123", true);
//		String sign = SignUtil.sign(privateKey , md5Encode, "utf-8");
//		String s2 = s1 + "&signvalue="+ sign + "|" + md5Encode;
//		System.out.println("s2 : " + s2);
//		byte[] bytes = s2.getBytes("utf-8");
//		//密钥长度设置为256位
//		byte[] key = new byte[32]; //key.length须满足16的整数倍
//		//偏移量
//		byte[] iv = new byte[16]; //iv.length须满足16的整数倍
//		// 设置key 全8，iv，全1，这里测试用
//		for (int i = 0; i < 32; i++) {
//			key[i] = 8;
//			if (i < 16) {
//				iv[i] = 1;
//			}
//		}
//		byte[] aes_cbc_encrypt = aes_cbc_encrypt(bytes, key, iv);
//		//加密的密文
//		System.out.println("1111111111:" + Base64.encodeBase64String(aes_cbc_encrypt));
//		byte[] aes_cbc_decrypt = aes_cbc_decrypt(aes_cbc_encrypt, key, iv);
//		//
//		System.out.println("2222222222:" + byteArrayToString(aes_cbc_decrypt));
//		//对密钥key进行rsa加密 商户提供，与手机银行通信用公钥
//		PublicKey merchantkey =  (PublicKey)CertUtil.getPublicKey("D:\\cer\\esales.cer");
//		byte[] encryptmerchantkey = CertUtil.encryptByPublicKey(key, merchantkey);
//		//偏移量IVStr进行rsa加密(商户提供，与手机银行通信用公钥)生成IV
//		PublicKey ivstrkey =  (PublicKey)CertUtil.getPublicKey("D:\\cer\\esales.cer");
//		byte[] encryivstrkey = CertUtil.encryptByPublicKey(key, ivstrkey);
//		String param = "key=" +Base64.encodeBase64String(encryptmerchantkey)+"&" + "iv="+Base64.encodeBase64String(encryivstrkey) + "&"+"cipher=" +Base64.encodeBase64String(aes_cbc_encrypt);
//		System.out.println("param =  " + param);
//		
//		String a = Base64.encodeBase64String(CertUtil.decryptByKey(encryptmerchantkey, (PrivateKey) CertUtil.getKey("D:\\cer\\esales.pfx", "PFX", "esales", true)));
//		if(a.equals(Base64.encodeBase64String(key))){
//			System.out.println("key=====" + Base64.encodeBase64String(key));
//			System.out.println("a == key");
//		}else{
//			System.out.println("a != key");
//		}
//	
//		String chinese = "招商信诺|1230987|123.00";
//		String encodechinese = java.net.URLEncoder.encode(chinese,"UTF-8");
//		System.out.println(encodechinese);
//		
//		String order = "乘机人|张三| 23";
//		System.out.println(java.net.URLEncoder.encode(order,"UTF-8"));
		
	}
	
	
	// PKCS5 和 PKCS7 填充问题
	private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
	
	/**
	 * 加密
	 * @param srcData  需要加密的信息
	 * @param key 密钥
	 * @param iv  偏移量
	 * @return 密文
	 * @throws Exception
	 */
    public static byte[] aes_cbc_encrypt(byte[] srcData,byte[] key,byte[] iv) throws Exception
    {
        SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] encData = cipher.doFinal(srcData);
        return encData;
    }

    /**
     * 解密
     * @param encData 密文
     * @param key 密钥
     * @param iv  偏移量
     * @return  明文
     * @throws Exception
     */
    public static byte[] aes_cbc_decrypt(byte[] encData,byte[] key,byte[] iv) throws Exception
    {
        SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] decbbdt = cipher.doFinal(encData);
        return decbbdt;
    }
}
