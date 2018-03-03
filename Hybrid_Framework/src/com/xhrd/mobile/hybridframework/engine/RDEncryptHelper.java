package com.xhrd.mobile.hybridframework.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;

import com.xhrd.mobile.hybridframework.BuildConfig;
import com.xhrd.mobile.hybridframework.framework.HybridEnv;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 解密辅助类
 * Created by wangqianyu on 15/6/8.
 */
public class RDEncryptHelper {
    private static char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static MimeTypeMap mMimeType = MimeTypeMap.getSingleton();
    private static RDDecBufferedInputStream.EncLruCache mCache = new RDDecBufferedInputStream.EncLruCache();

    static {
        System.loadLibrary("encryption");
        init();
    }

    public native static void init();

    public native static void close();

    //public native static byte[] encrypt(byte[] content);
    public native static byte[] decrypt(byte[] content);

    public native static int check(byte[] content);

    public native static int getType();

    public native static int getVersion();

    private static boolean isEncrypted(String url) {
        //判断协议头和文件头
//        boolean ret = URLUtil.isFileUrl(url) && (url.endsWith(".js") || url.endsWith(".css") || url.endsWith(".html") || url.endsWith(".htm")
//                            || url.endsWith(".jpg"));
//        boolean ret = url.endsWith(".jpg");
//        return ret;

        boolean ret = URLUtil.isFileUrl(url) && (url.endsWith(".js") || url.endsWith(".css") || url.endsWith(".html") || url.endsWith(".htm"));
        return ret;
    }

    public static WebResourceResponse decode(String url) {
        if (isEncrypted(url)) {
            FileInputStream fis = null;
            try {
                File file = new File(url.replace("file://", ""));
                fis = new FileInputStream(file);
                int lastIndex = url.lastIndexOf('.');
                String ext = url.substring(lastIndex + 1);
                String mime = mMimeType.getMimeTypeFromExtension(ext);
                byte[] buffer = new byte[(int) file.length()];

                RDDecBufferedInputStream.LruItem<ByteArrayInputStream> item = mCache.get(url);
                InputStream is = null;
                if (item != null) {
                    if (item.item != null) {
                        item.item.reset();
                    }
                    is = item.item;
                } else {
                    fis.read(buffer);
                    if (check(buffer) == 1) {
                        buffer = decrypt(buffer);
                    }
                    is = new ByteArrayInputStream(buffer);
                    item = new RDDecBufferedInputStream.LruItem<ByteArrayInputStream>((ByteArrayInputStream) is, buffer.length);
                    mCache.put(url, item);
                }

                return new WebResourceResponse(mime, "utf-8", is);
//                return new WebResourceResponse(mime, "utf-8", new RDDecBufferedInputStream(url, fis));
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(RDEncryptHelper.class.getSimpleName(), "decode " + url + " failed.", e);
                }
            }
        }
        return null;
    }

    public static String getMD5() {
        try {
            Context context = HybridEnv.getApplicationContext();
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                //md = MessageDigest.getInstance("SHA1");
                md = MessageDigest.getInstance("MD5");
                md.update(signature.toByteArray());

                byte[] bytes = md.digest();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < bytes.length; i++) {
                    byte b = bytes[i];
                    char c = HEX_DIGITS[b >> 4 & 0xf];
                    sb.append(c);
                    c = HEX_DIGITS[b & 0xf];
                    sb.append(c).append(':');
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString();
            }

        } catch (Exception e) {
            Log.e(RDEncryptHelper.class.getSimpleName(), "getMD5 failed.", e);
        }
        return "";
    }

    /**
     * 解密（解密的解密返回，否则返回原数据）
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static InputStream getDecryptInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        int check = RDEncryptHelper.check(buffer);
        if (check == 1) {// 判断是否加密
            buffer = RDEncryptHelper.decrypt(buffer);
        } else {
            // 没有加密，暂不处理
        }
        inputStream = new ByteArrayInputStream(buffer);
        inputStream.close();
        return inputStream;
    }

}

