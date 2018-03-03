package com.xhrd.mobile.hybrid.engine;

/**
 * Created by langjinbin on 15/5/6.
 */
public interface IAppCallback {
    void onBackground();
    void onForeground();
    void onDestroy();
    void onLoad();
}
