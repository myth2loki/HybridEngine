package com.xhrd.mobile.hybrid.framework.Manager;

import android.os.Environment;

import java.io.File;

/**
 * Created by wangqianyu on 15/7/9.
 */
public class AppLoaderResManager extends ResManager {
    private static AppLoaderResManager mInstance;

    synchronized static ResManager getInstanceInternal() {
        if (mInstance == null) {
            mInstance = new AppLoaderResManager();
            mInstance.init();
        }
        return mInstance;
    }

    @Override
    protected String getF_PATH() {
//        F_PATH = "/sdcard/rd";
        F_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rd";
        File folder = new File(F_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return F_PATH;
    }
}
