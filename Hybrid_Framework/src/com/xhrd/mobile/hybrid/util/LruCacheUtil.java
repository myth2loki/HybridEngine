package com.xhrd.mobile.hybrid.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

import com.xhrd.mobile.hybrid.engine.HybridResourceManager;

import java.io.File;

/**
 * Created by maxinliang on 15/6/29.
 */
public class LruCacheUtil {

    private static LruCacheUtil mInstance = new LruCacheUtil();

    /**
     * 通过传入context获得ResoureFinder实例以后调用方法可以不用传入context
     *
     * @return
     */
    public synchronized static LruCacheUtil getInstance() {
        if (mInstance == null) {
            mInstance = new LruCacheUtil();
        }
        return mInstance;
    }

    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 线程池
     */
//    private ExecutorService mImageThreadPool = null;


    private LruCacheUtil() {
        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        //给LruCache分配1/8 4M
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }


    /**
     * 获取Bitmap, 内存中没有就去手机或者sd卡中获取
     *
     * @param path
     * @return
     */
    public Bitmap getBitmap(final String path) {
        Bitmap bitmap = null;
        bitmap = getBitmapFromMemCache(path);
        if (bitmap != null) {
            return bitmap;
        } else {
            File file = new File(path);
            if (file.exists() && file.length() != 0) {
                //从SD卡获取手机里面获取Bitmap
                bitmap = BitmapFactory.decodeFile(path);
                //将Bitmap 加入内存缓存
                addBitmapToMemoryCache(path, bitmap);
                return bitmap;
            }
            return null;
        }
    }


    public Drawable getDrawable(String path) {
        Bitmap bitmap = getBitmap(path);
        if (bitmap != null) {
            return new BitmapDrawable(HybridResourceManager.getInstance().getResources(), bitmap);
        }
        return null;
    }

    /**
     * 从内存缓存中获取一个Bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 添加Bitmap到内存缓存
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     *
     * @return
     */
//    public ExecutorService getThreadPool() {
//        if (mImageThreadPool == null) {
//            synchronized (ExecutorService.class) {
//                if (mImageThreadPool == null) {
//                    //为了下载图片更加的流畅，我们用了2个线程来下载图片
//                    mImageThreadPool = Executors.newFixedThreadPool(2);
//                }
//            }
//        }
//        return mImageThreadPool;
//    }

    //    /**
    //     * 异步下载图片的回调接口
    //     * @author len
    //     *
    //     */
    //    public interface onImageLoaderListener{
    //        void onImageLoader(Bitmap bitmap, String url);
    //    }
}
