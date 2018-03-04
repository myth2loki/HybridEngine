package com.wangqianyu.hybrid.demo;

import android.app.Application;

import com.xhrd.mobile.hybrid.framework.HybridEnv;

/**
 * Created by Administrator on 2018/3/4.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HybridEnv.init(this);
    }
}
