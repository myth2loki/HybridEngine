package com.xhrd.mobile.hybrid.framework.manager;

import com.xhrd.mobile.hybrid.framework.manager.res.ResManager;

/**
 * Created by wangqianyu on 15/7/10.
 */
public class ResManagerFactory {
    public static ResManager getResManager() {
        return ResManager.getInstance();

    }
}
