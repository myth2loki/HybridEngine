package com.xhrd.mobile.hybrid.engine;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;

import com.xhrd.mobile.hybrid.framework.PluginBase;

import java.util.Map;

/**
 * WebView类，负责加载HTML界面，执行JS代码
 * Created by wangqianyu on 15/4/10.
 */
public interface RDCloudView extends PullToRefreshBase.OnRefreshListener {
    String JAVASCRIPT_HEAD = "javascript:";
    int DATA_TYPE_URL = 2;
    int DATA_TYPE_TEXT = 1;
    int DATA_TYPE_LOCAL = 0;

    String FROYO_USERAGENT = "Mozilla/5.0 (Linux; U; "
            + "Android " + Build.VERSION.RELEASE + "; en-us; " + Build.MODEL
            + " Build/FRF91) AppleWebKit/533.1 "
            + "(KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    String USERAGENT = FROYO_USERAGENT;

    /**
     * 获取上下文
     * @return
     */
    Context getContext();

    /**
     * 加入子视图
     * @param view
     */
    void addView(View view);

    AbsRDCloudChromeClient getChromeClient();

    void setVerticalScrollBarEnabled(boolean enabled);

    void setHorizontalFadingEdgeEnabled(boolean enabled);

    PullToRefreshWebView getRefresableParent();

    void setRefresableParent(PullToRefreshWebView refresableParent);


    String getOnPullUpCallBack();

    void setOnPullDownCallBack(String onPullDownCallBack);

    String getOnPullDownCallBack();

    void setOnPullUpCallBack(String onPullUpCallBack);

    /**
     * 载入数据。
     *
     * @param info
     */
    void load(final RDCloudWindowInfo info);

    void loadUrl(String url);

    /**
     * 注册插件
     *
     * @param plugin
     */
    void registerPlugin(Object plugin);

    /**
     * 获取窗口名字
     * @return 窗口名字，如果没有窗口绑定则返回null
     */
    String getWindowName();

    /**
     * 获取窗口
     * @return 窗口，如果没有窗口绑定则返回null
     */
    RDCloudWindow getRDCloudWindow();

    /**
     * 执行一段js脚本
     * @param script
     * @param resultCallback
     */
    void evaluateJavascript(String script, ValueCallback<String> resultCallback);

    /**
     * 设置pop窗口名字
     * @param name
     */
    void setPopName(String name);

    /**
     * 获取pop窗口名字
     * @return
     */
    String getPopName();

    /**
     * 是否是pop窗口
     * @return
     */
    boolean isPopover();

//    void setPopoverVisible(boolean visible);

    /**
     * 获取当前名字
     * @return 当前名字，如果是popover返回pop名字，如果是window返回window名字
     */
    String getName();

    /**
     * 触发window/pop的onLoad事件
     */
    void onLoad();
    void onForeground();
    void onBackground();
    void onDestroy();
    void onSizeChanged(Configuration newConfig);

    void reload();

    void goForward();
    void goBack();

    void disableLoadingBar();

    Map<Integer, PluginBase> getInjectedFMJSObj();

    Map<Integer, PluginBase> getInjectedPluginJSObj();

    Map<Class<?>, PluginBase> getInjectedLocalFMJSObj();

    Map<Class<?>, PluginBase> getInjectedLocalPluginJSObj();

    /**
     * 获取下拉刷新、上拉加载控件
     * @return
     */
    PullToRefreshWebView getPopoverParent();

    /**
     * 设置下拉刷新、上拉加载控件
     * @param parentView
     */
    void setPopoverParentView(PullToRefreshWebView parentView);

    boolean isChildrenRefreshable(MotionEvent event);

    boolean isChildInterceptTouchEventEnabled(MotionEvent event);

    boolean isChildPullLoading(MotionEvent event);

    boolean isChildPullRefreshing(MotionEvent event);

    /**
     * 触摸事件处理
     * @param event
     * @return
     */
    boolean onTouchEvent(MotionEvent event);

    /**
     * 获取本身视图
     * @return
     */
    View getSelfView();

    /**
     * 获取Y坐标
     * @return
     */
    int getScrollY();

    /**
     * 获取web内容高度
     * @return
     */
    int getContentHeight();

    /**
     * 获取缩放值
     * @return
     */
    float getScale();

    /**
     * 获取视图高度
     * @return
     */
    int getHeight();

    /**
     * 获取视图宽度
     * @return
     */
    int getWidth();
}
