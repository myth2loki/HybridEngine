package com.xhrd.mobile.hybridframework.framework.Manager;

import com.xhrd.mobile.hybridframework.BuildConfig;
import com.xhrd.mobile.hybridframework.Config;

/**
 * Created by wangqianyu on 15/7/10.
 */
public class ResManagerFactory {
    public static ResManager getResManager() {
        return Config.APP_LOADER ? AppLoaderResManager.getInstanceInternal() : ResManager.getInstanceInternal();
        //TODO 在eclipse中改用下面代码，但是不要提交：
//        return ResManager.getInstanceInternal();
    }
}
