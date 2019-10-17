package com.zzj.data.util;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
public class RowKeyUtil {

    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f" };

    /**
     * @Description: 生成rowkey @return String rowkey @throws
     */
    public static String createRowKey(String key) {

        if (StringUtils.isEmpty(key)) {
            return getRowkey(UUID.randomUUID().toString().replace("-", ""));
        } else {
            return getRowkey(key);
        }
    }

    private static String encodeByMD5(String originString) {
        if (originString != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] pks = md.digest(originString.getBytes());
                String pk = byteArrayToHexString(pks);
                return pk.toUpperCase();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * @Description: 将一个字节转化成十六进制形式的字符串
     * @return String
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private final static Pattern wipeExtraSpacesRegex = Pattern.compile("\\s{2,}|\\t|\\n|\\r");

    /**
     * 去除多余的空格，即连续多空格处替换为一个空格,并且去除所有换行或回车或制表符，用空格替换
     *
     * @param str
     * @return
     * @author tangxr
     * @since 2014-6-28
     */
    public static String wipeExtraSpaces(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        return wipeExtraSpacesRegex.matcher(str).replaceAll(Matcher.quoteReplacement(" "));
    }

    /**
     * 字符串是否为null或空
     *
     * @param str
     * @return
     * @author tangxr
     * @since 2014-7-2
     */
    public static boolean isBlank(String str) {
        if (str == null || str.trim().equals("") || str.trim().equals(" ")) {
            return true;
        }
        return false;
    }

    /**
     * 字符串是否为非null且非空
     *
     * @param str
     * @return
     * @author tangxr
     * @since 2014-7-2
     */
    public static boolean isNotBlank(String str) {

        return !isBlank(str);
    }

    /**
     * 将字符串转化为16位的MD5密文
     *
     * @param str
     *            字符串
     * @return
     * @author tangxr
     * @since 2014-7-11
     */
    public static String encrypt(String str) {
        if (str == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            return hash.toString(16);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 得到一个唯一的标示符
     *
     * @return
     */
    public synchronized static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 得到一个唯一的标示符数组
     *
     * @param number
     * @return
     */
    public static String[] getUUID(int number) {
        if (number < 1) {
            return null;
        }
        String[] uList = new String[number];
        for (int i = 0; i < number; i++) {
            uList[i] = getUUID();
        }
        return uList;
    }

    public static String[] removeNull(String[] str) {
        ArrayList<String> list = new ArrayList<String>();
        for (String s : str) {
            if (s != null) {
                list.add(s);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static Integer[] removeNull(Integer[] str) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (Integer s : str) {
            if (s != null) {
                list.add(s);
            }
        }
        return list.toArray(new Integer[list.size()]);
    }

    /**
     * 得到rowkey的值
     *
     * @author kecoo
     * @since 下午5:42:53
     * @doc DataIntegrationThread2.java
     */
    public static String getRowkey(String key) {
        return encrypt(key).substring(0, 4) + "_" + key;
    }

    /**
     * 得到rowkey的值
     *
     * @author kecoo
     * @since 下午5:42:53
     * @doc DataIntegrationThread2.java
     */
    public static String getHbaseRowkey(String key) {
        return encrypt(key).substring(0, 4)+ "_" + key;
    }
}
