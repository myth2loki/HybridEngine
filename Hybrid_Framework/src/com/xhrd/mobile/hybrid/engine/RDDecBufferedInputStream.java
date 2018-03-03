package com.xhrd.mobile.hybrid.engine;

import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 加密解密流
 * Created by lanjingmin on 2015/6/30.
 */
public class RDDecBufferedInputStream extends BufferedInputStream {
    private static final EncLruCache mCache = new EncLruCache();
    private String mUrl;
    private ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    private boolean mAlready;
    private boolean isEncrypted;

    public RDDecBufferedInputStream(String url, InputStream in) {
        super(in);
        mUrl = url;
    }

    @Override
    public synchronized int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        if (mAlready) {
            mAlready = false;
            return -1;
        }
        LruItem<ByteArrayInputStream> item = mCache.get(mUrl);
        if (item != null) {
            int ret = item.item.read(buffer, byteOffset, byteCount);
            //Log.e("1-----", "ret: " + ret + ", byteOffset: " + byteOffset + ", byteCount: " + byteCount + ", url: " + mUrl);
            if (ret == -1) {
                item.item.reset();
                mAlready = true;
            }
            return ret;
        }

        int ret = super.read(buffer, byteOffset, byteCount);
        if (ret == -1) {
            byte[] buff = mBaos.toByteArray();
            //Log.e("buffer size:", "size: " + buff.length + ", url: " + mUrl);
            ByteArrayInputStream bais = new ByteArrayInputStream(buff, 0, buff.length);
            item = new LruItem<ByteArrayInputStream>(bais, buff.length);
            mCache.put(mUrl, item);
            mBaos.close();
            mAlready = true;
        } else {
//            byte[] bs = null;
            if (!isEncrypted) {
                isEncrypted = RDEncryptHelper.check(buffer) == 1;
//                if (isEncrypted) {
//                    bs = new byte[ret - 8];
//                    System.arraycopy(buffer, 8, bs, 0, bs.length);
//                }
            }

            if (isEncrypted) {
                byte[] decBytes = null;
//                if (bs == null) {
                    decBytes = RDEncryptHelper.decrypt(buffer);
//                } else {
//                    decBytes = RDEncryptHelper.decrypt(bs);
//                }
                if (decBytes != null) {
                    System.arraycopy(decBytes, 0, buffer, 0, decBytes.length);
                    ret = decBytes.length;
                    //Log.e(mUrl, new String(decBytes));
                }
            }
            mBaos.write(buffer, 0, ret);
        }
        //Log.e("2-----", "ret: " + ret + ", byteOffset: " + byteOffset + ", byteCount: " + byteCount + ", url: " + mUrl);
        return ret;
    }

    /**
     * 填充缓存。
     * @param url
     * @return
     */
    public static boolean fillBuffer(String url) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            try {
                File file = new File(url.replace("file://", ""));
                if (!file.exists()) {
                    return false;
                }
                fis = new FileInputStream(file);
                baos = new ByteArrayOutputStream();

                boolean isEncrypted = false;
                if (file.length() > 8) {
                    byte[] buff = new byte[8];
                    int len = fis.read(buff);
                    isEncrypted = RDEncryptHelper.check(buff) == 1;
                    if (!isEncrypted) {
                        baos.write(buff, 0, len);
                    }
                }
                byte[] buff = new byte[1024 * 8];
                byte[] decBuf = null;
                int len = -1;
                while((len = fis.read(buff)) > -1) {
                    if (isEncrypted) {
                        decBuf = RDEncryptHelper.decrypt(buff);
                        baos.write(decBuf, 0, decBuf.length);
                    } else {
                        baos.write(buff, 0, len);
                    }
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                LruItem<ByteArrayInputStream> item = new LruItem<ByteArrayInputStream>(bais, baos.size());
                mCache.put(url, item);
                //Log.e("fill buff---->", url);
                return true;
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (baos != null) {
                    baos.close();
                }
            }
        } catch (IOException e) {
            Log.e(RDDecBufferedInputStream.class.getSimpleName(), "fillBuffer failed.", e);
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    /**
     * 判断当前地址是否已经有缓存。
     * @param url 要打开的网页的绝对地址
     * @return true为已经存在，否则为不存在
     */
    public static boolean isInCache(String url) {
        return mCache.get(url) != null;
    }

    public static class EncLruCache extends LruCache<String, LruItem<ByteArrayInputStream>> {
        private static final int MAX_SIZE = 1024 * 1024 * 2;

        public EncLruCache() {
            super(MAX_SIZE);
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, LruItem<ByteArrayInputStream> oldValue, LruItem<ByteArrayInputStream> newValue) {
            try {
                oldValue.item.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.entryRemoved(evicted, key, oldValue, newValue);
        }

        @Override
        protected int sizeOf(String key, LruItem<ByteArrayInputStream> value) {
            return value.size;
        }
    }

    public static class LruItem<T> {
        int size;
        T item;

        public LruItem(T item, int size) {
            this.item = item;
            this.size = size;
        }
    }
}