package com.cignacmb.iuss.web.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * 解密工具类
 * 
 * @author Phantom
 * @date 2018年1月19日 下午4:59:38
 */
public class RsaUtil {
    /**
     * 日志对象
     */
    private static final Logger logger = Logger.getLogger(RsaUtil.class);
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String ALGORITHMNAME = "RSA";

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f' };

    public static final int ENCRYPT_LIMIT_POSTION = 117;
    public static final int DECRYPT_LIMIT_POSTION = 256;

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static final String KEY_STORE = "JKS";
    public static final String X509 = "X.509";
    public static final String PKCS12 = "PKCS12";
    public static final String CERTYPE_JKS = "JKS";
    public static final String CERTYPE_PFX = "PFX";
    public static final String CERTYPE_CER = "CER";

    /**
     * 读取证书文件
     * 
     * @param 证书文件路径
     * @return
     * @throws Exception
     */
    public static String loadKeyByFile(String path) throws Exception {
        FileReader fr = new FileReader(path);
        try {
            BufferedReader br = new BufferedReader(fr);
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("public key can't be read");
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("inputStream is null");
        } finally {

            fr.close();

        }
    }

    /**
     * 加载公钥文件字符串
     * 
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHMNAME);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 加载私钥文件字符串
     * 
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHMNAME);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 私钥加密
     * 
     * @param privateKeyStr
     *            私钥对象
     * @param plainTextData
     *            待加密数据字节码
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
        if (privateKey == null) {
            throw new Exception("加密私钥为空");
        }
        Cipher cipher = null;
        byte[] output = null;
        try {
            cipher = Cipher.getInstance(ALGORITHMNAME);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            int contextLength = plainTextData.length;
            ByteArrayOutputStream resultBytePipe = new ByteArrayOutputStream();
            if (ENCRYPT_LIMIT_POSTION < contextLength) {
                int index = 0;
                int limit;
                for (int i = 0; i <= contextLength / ENCRYPT_LIMIT_POSTION; i++) {
                    byte[] templateArray;
                    if (i == contextLength / ENCRYPT_LIMIT_POSTION) {
                        limit = index + contextLength % ENCRYPT_LIMIT_POSTION;
                    } else {
                        limit = index + ENCRYPT_LIMIT_POSTION;
                    }
                    templateArray = Arrays.copyOfRange(plainTextData, index, limit);
                    templateArray = cipher.doFinal(templateArray);
                    resultBytePipe.write(templateArray, 0, templateArray.length);
                    if (limit == contextLength) {
                        break;
                    }
                    index = limit;
                }
                output = resultBytePipe.toByteArray();
            } else {
                output = cipher.doFinal(plainTextData);
            }
            return output;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("加密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 公钥加密字符串
     * 
     * @param publicKey
     *            公钥对象
     * @param plainTextData
     *            明文字符串
     * @return 加密后密文字符串
     * @throws Exception
     *             运行时异常
     */
    public static String encrypt(RSAPublicKey publicKey, String plainTextData) throws Exception {
        byte[] bizcontentData = encrypt(publicKey, plainTextData.getBytes(DEFAULT_CHARSET));
        return Base64.encodeBase64String(bizcontentData);
    }

    /**
     * 公钥加密
     * 
     * @param privateKeyStr
     *            公钥
     * @param data
     *            待加密数据字节码
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        byte[] output = null;
        try {
            cipher = Cipher.getInstance(ALGORITHMNAME);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            int contextLength = plainTextData.length;
            ByteArrayOutputStream resultBytePipe = new ByteArrayOutputStream();
            if (ENCRYPT_LIMIT_POSTION < contextLength) {
                int index = 0;
                int limit;
                for (int i = 0; i <= contextLength / ENCRYPT_LIMIT_POSTION; i++) {
                    byte[] templateArray;
                    if (i == contextLength / ENCRYPT_LIMIT_POSTION) {
                        limit = index + contextLength % ENCRYPT_LIMIT_POSTION;
                    } else {
                        limit = index + ENCRYPT_LIMIT_POSTION;
                    }
                    templateArray = Arrays.copyOfRange(plainTextData, index, limit);
                    templateArray = cipher.doFinal(templateArray);
                    resultBytePipe.write(templateArray, 0, templateArray.length);
                    if (limit == contextLength) {
                        break;
                    }
                    index = limit;

                }
                output = resultBytePipe.toByteArray();
            } else {
                output = cipher.doFinal(plainTextData);
            }
            return output;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 私钥解密
     * 
     * @param privateKeyStr
     *            私钥对象
     * @param cipherData
     *            公钥加密后的字节码
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHMNAME);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int contextLength = cipherData.length;
            byte[] output;
            if (DECRYPT_LIMIT_POSTION < contextLength) {
                ByteArrayOutputStream resultBytePipe = new ByteArrayOutputStream();
                int index = 0;
                int limit;
                for (int i = 0; i <= contextLength / DECRYPT_LIMIT_POSTION; i++) {
                    byte[] templateArray;
                    if (i == contextLength / DECRYPT_LIMIT_POSTION) {
                        limit = index + contextLength % DECRYPT_LIMIT_POSTION;
                    } else {
                        limit = index + DECRYPT_LIMIT_POSTION;
                    }
                    templateArray = Arrays.copyOfRange(cipherData, index, limit);
                    templateArray = cipher.doFinal(templateArray);
                    resultBytePipe.write(templateArray, 0, templateArray.length);
                    if (limit == contextLength) {
                        break;
                    }
                    index = limit;
                }
                output = resultBytePipe.toByteArray();
            } else {
                output = cipher.doFinal(cipherData);
            }
            return output;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 公钥解密
     * 
     * @param publicKey
     *            公钥对象
     * @param cipherData
     *            私钥加密后的字节码
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHMNAME);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output = null;
            int contextLength = cipherData.length;
            if (DECRYPT_LIMIT_POSTION < contextLength) {
                ByteArrayOutputStream resultBytePipe = new ByteArrayOutputStream();
                int index = 0;
                int limit;
                for (int i = 0; i <= contextLength / DECRYPT_LIMIT_POSTION; i++) {
                    byte[] templateArray;
                    if (i == contextLength / DECRYPT_LIMIT_POSTION) {
                        limit = index + contextLength % DECRYPT_LIMIT_POSTION;
                    } else {
                        limit = index + DECRYPT_LIMIT_POSTION;
                    }
                    templateArray = Arrays.copyOfRange(cipherData, index, limit);
                    templateArray = cipher.doFinal(templateArray);
                    resultBytePipe.write(templateArray, 0, templateArray.length);
                    if (limit == contextLength) {
                        break;
                    }
                    index = limit;
                }
                output = resultBytePipe.toByteArray();
            } else {
                cipher.doFinal(cipherData);
            }
            return output;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 字节码转16进制字符串
     * 
     * @param input
     *            字节码
     * @return
     */
    public static String byteToHex(byte[] input) {
        char[] hexArray = new char[input.length * 2];
        int i = 0;
        for (byte b : input) {
            hexArray[i++] = DIGITS[b >>> 4 & 0xf];
            hexArray[i++] = DIGITS[b & 0xf];
        }
        return new String(hexArray);
    }

    /**
     * 16进制字符串转字节码
     * 
     * @param hexStr
     *            字符串
     * @return
     */
    public static byte[] hexToByte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 从证书中读取公钥
     * 
     * @param path
     *            字符串 证书路径
     * @return
     */
    public static RSAPublicKey getRSAPublicKeyByLocalCert(String path) throws Exception {
        // 证书路径
        File file = new File(path);
        X509Certificate cert;
        PublicKey publicKey;
        String publicKeyString;
        RSAPublicKey rsaPublicKey = null;
        try {
            cert = X509Certificate.getInstance(new FileInputStream(file));
            publicKey = cert.getPublicKey();
            publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
            rsaPublicKey = RsaUtil.loadPublicKeyByStr(publicKeyString);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("找不到证书文件");
        } catch (CertificateException e) {
            logger.error(e.getMessage(), e);
            throw new Exception("不是合法的证书");
        }
        return rsaPublicKey;
    }

    /**
     * 加签
     * 
     * @param key
     * @param str
     * @param charset
     * @return
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String sign(PrivateKey key, String str, String charset) throws SignatureException,
            InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(str.getBytes(charset));
        // 验证签名是否正常
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 验签
     * 
     * @param key
     * @param data
     * @param bsign
     * @return
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static boolean verifySign(PublicKey key, byte[] data, byte[] sign)
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(data);
        // 验证签名是否正常
        return signature.verify(sign);
    }

    /**
     * 从pfx文件中获取公钥或私钥
     * 
     * @param pfxFile
     * @param keystore_password
     * @param isPrivateKey
     *            true私钥 false公钥
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     */
    public static Key getKey(String pfxFile, String fileType, String keystorePassword,
            boolean isPrivateKey) throws IOException, NoSuchAlgorithmException,
            CertificateException, KeyStoreException, UnrecoverableKeyException {
        KeyStore inputKeyStore = KeyStore.getInstance(getKSType(fileType));
        FileInputStream fis = new FileInputStream(pfxFile);
        char[] nPassword = keystorePassword.toCharArray();
        inputKeyStore.load(fis, nPassword);
        fis.close();

        Enumeration<String> enumas = inputKeyStore.aliases();
        String keyAlias = null;
        if (enumas.hasMoreElements()) {
            keyAlias = enumas.nextElement();
        }

        if (isPrivateKey) {
            return inputKeyStore.getKey(keyAlias, nPassword);
        }
        Certificate cert = inputKeyStore.getCertificate(keyAlias);
        return cert.getPublicKey();
    }

    public static String getKSType(String fileType) {
        if (CERTYPE_JKS.equalsIgnoreCase(fileType)) {
            return KEY_STORE;
        }
        return PKCS12;
    }

    /**
     * 加密
     * 
     * @param data
     * @param certificatePath
     * @return
     * @throws Exception
     */
    public static byte[] encryptByKey(byte[] data, Key key) throws Exception {
        return useKey(data, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByKey(byte[] data, Key key) throws Exception {
        return useKey(data, key, Cipher.DECRYPT_MODE);
    }

    /**
     * 核心处理方法
     * 
     * @param data
     * @param key
     * @param mode
     * @return
     * @throws Exception
     */
    private static byte[] useKey(byte[] data, Key key, int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(mode, key);

        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int offSet = 0;
        byte[] cache;
        int i = 0;
        int block = Cipher.DECRYPT_MODE == mode ? MAX_DECRYPT_BLOCK : MAX_ENCRYPT_BLOCK;
        // 对数据分段解密
        while (inputLen - offSet > 0) {

            if (inputLen - offSet > block) {
                cache = cipher.doFinal(data, offSet, block);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            i++;
            offSet = i * block;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
}
