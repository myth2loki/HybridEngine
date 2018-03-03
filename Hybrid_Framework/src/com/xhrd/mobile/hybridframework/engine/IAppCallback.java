package com.xhrd.mobile.hybridframework.engine;

import android.app.Activity;

/**
 * Created by langjinbin on 15/5/6.
 */
public interface IAppCallback {
    void onBackground();
    void onForeground();
    void onDestroy();
    void onLoad();
}
