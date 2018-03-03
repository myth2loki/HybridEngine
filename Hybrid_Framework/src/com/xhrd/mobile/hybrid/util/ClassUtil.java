package com.xhrd.mobile.hybrid.util;

import android.content.Context;
import android.util.Log;

import com.xhrd.mobile.hybrid.BuildConfig;

/**
 * Created by wangqianyu on 15/5/22.
 */
public class ClassUtil {

    public static Class<?> loadClass(Context context, String className) {
        Class<?> clazz = null;
        try {
            clazz = context.getClassLoader().loadClass(className);
            if (clazz == null) {
                clazz = Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            if (BuildConfig.DEBUG) {
                Log.d(ClassUtil.class.getSimpleName(), "load " + className + " failed.", e);
            }
        }
        return clazz;
    }
}
