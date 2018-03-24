package com.wangqianyu.hybrid.demo;

import android.app.Application;

import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.manager.actionsheet.ActionSheet;
import com.xhrd.mobile.hybrid.framework.manager.audio.Audio;

/**
 * Created by Administrator on 2018/3/4.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HybridEnv.enableDebug(true);

        HybridEnv.addPlugin(ActionSheet.class);
        HybridEnv.addPlugin(Audio.class);
        HybridEnv.init(this);
    }
}
