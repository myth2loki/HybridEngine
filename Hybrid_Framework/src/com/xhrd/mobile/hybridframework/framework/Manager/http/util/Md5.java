package com.xhrd.mobile.hybridframework.framework.Manager.http.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lilong on 15/7/12.
 */
public class Md5 {
    public static String getMD5(String val)   {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }
    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }
}
