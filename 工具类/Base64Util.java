package com.cignacmb.iuss.web.common.hmcUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 通用Base64编解码工具类
 * @package com.restlet.util
 * @class Base64Util
 * @author colin.mei
 * @modified 2015-8-20 下午12:07:17
 */
public class Base64Util {
    


    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    /**
     * 编码方法
     * @param content 待编码的字符串
     * @return 编码后的字符串
     */
    public static String encode(String content) {
        return encode(content.getBytes());
    }

    /**
     * 编码方法
     * @param data 待编码的字节数据
     * @return 编码后的字符串
     * @creator MeiGaoBang
     * @createdate 2015-5-15 上午11:39:48
     */
    public static String encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);
        int end = len - 3;
        int i = start;
        int n = 0;
        while (i <= end) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 0x0ff) << 8)
                    | (((int) data[i + 2]) & 0x0ff);
            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append(legalChars[d & 63]);
            i += 3;
            if (n++ >= 14) {
                n = 0;
                buf.append(" ");
            }
        }
        if (i == start + len - 2) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 255) << 8);
            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            int d = (((int) data[i]) & 0x0ff) << 16;
            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append("==");
        }
        return buf.toString();
    }

    /**
     * 解码方法
     * @param c 待解码的字符
     * @return 解码后的字符对应的整型值
     * @creator MeiGaoBang
     * @createdate 2015-5-15 上午11:40:32
     */
    private static int decode(char c) {
        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else {
            switch (c) {
            case '+':
                return 62;
            case '/':
                return 63;
            case '=':
                return 0;
            default:
                throw new RuntimeException("unexpected code: " + c);
            }
        }
    }

    /**
     * 解码方法
     * @param s 待解码的字符串
     * @return 解码后的字节数组
     * @creator MeiGaoBang
     */
    public static String decode(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decode(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return new String(decodedBytes);
    }
    
    /**
     * 解码方法
     * @param s 待解码的字符串
     * @param os 字节输出流
     * @throws IOException 输入输出流异常
     * @creator MeiGaoBang
     */
    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;
        int len = s.length();
        while (true) {
            while (i < len && s.charAt(i) <= ' ')
                i++;
            if (i == len)
                break;
            int tri = (decode(s.charAt(i)) << 18)
                    + (decode(s.charAt(i + 1)) << 12)
                    + (decode(s.charAt(i + 2)) << 6)
                    + (decode(s.charAt(i + 3)));
            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);
            i += 4;
        }
    }
    
    public static void main(String[] args) throws Exception {   
//     String str = encode("{userId:1475220,policyNo:\"V820104755\",startDate:\"2017-3-28\","
//       + "endDate:\"2017-3-28\",tranxType:\"\",statusCode:\"\",customerNo:\"\"}");
     String str = encode("{tranxNo:\"12392021605315649\",changeType:\"PARTEXTRACT\"}");
     System.out.println(str);
     System.out.println(decode(str));
     System.out.println("abc%%@$@$534534545**()#@#@#$ %^&$!^$&*(@@$@$@ggadgadgdgasdgaeqte");
    }
}
