package com.xhrd.mobile.hybridframework;


/**
 * Created by wangqianyu on 15/6/8.
 */
public class TestNative {

    static {
        System.loadLibrary("testnative");
    }

    public native static String getStringFromNative();
}
