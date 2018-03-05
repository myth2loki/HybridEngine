package com.xhrd.mobile.hybrid.engine;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.PluginManagerBase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 窗口类，负责管理View。
 * Created by wangqianyu on 15/4/10.
 */
public class HybridWindow extends RelativeLayout {
    private LinkedList<HybridView> mViewList = new LinkedList<HybridView>();
    private PullToRefreshWebView mMainView;
    private String mName;
    private HybridActivity mActivity;
    private Map<Class<?>, PluginBase> mInjectedFMJSObj = new HashMap<Class<?>, PluginBase>();

    private TextView mHttpTitleTV;
    private ProgressBar mHtppProgressBar;

    /**
     * 构建一个带名字的窗口。
     *
     * @param name      window名字
     */
    public HybridWindow(HybridActivity activity, String name, int type, String data) {
        super(activity);
        setBackgroundColor(Color.WHITE);
        mActivity = activity;
        mName = name;
        openWindow(name, type, data);
    }

    public HybridView getmMainView() {
        return mMainView.getRefreshableView();
    }

    /**
     * 打开一个窗口。如果窗口重名就在之前的窗口中打开。此方法并不会改变window栈的顺序。
     *
     * @param windowName 要打开的窗口名字
     * @param type       0 - url，1 - html
     * @param data       0 - url，1 - html内容
     */
    public void openWindow(String windowName, int type, String data) {
        HybridWindowInfo info = new HybridWindowInfo();
        info.windowName = windowName;
        info.width = LayoutParams.MATCH_PARENT;
        info.height = LayoutParams.MATCH_PARENT;
        info.type = type;
        info.data = data;
        //处理二次加载导致js失效。需优化
        if (mMainView != null) {
            removeView(mMainView);
        }
        mMainView = new PullToRefreshWebView(this);
        if (type == HybridView.DATA_TYPE_URL) {
            addTopBottomBar(mMainView);
        } else {
            addView(mMainView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        HybridView view = mMainView.getRefreshableView();
        mMainView.setOnRefreshListener(view);
        view.load(info);
    }

    //TODO 标题栏
    private void addTopBottomBar(final PullToRefreshWebView webView) {
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(android.R.color.white);

        final HybridResourceManager rdrm = HybridResourceManager.getInstance();
        RelativeLayout titleBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(rdrm.getLayoutId("layout_url_title_bar"), null);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(rdrm.getResDimenId("url_bottom_bar_height")));
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        addView(titleBar, lp1);

        mHttpTitleTV = (TextView) titleBar.findViewById(rdrm.getId("type_url_title_bar_title"));
        mHtppProgressBar = (ProgressBar) titleBar.findViewById(rdrm.getId("type_url_top_bar_progress"));

        ImageView backButton = (ImageView) titleBar.findViewById(rdrm.getId("type_url_title_bar_back"));
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        RelativeLayout bottomBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(rdrm.getLayoutId("layout_url_bottom_bar"), null);
        lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(rdrm.getResDimenId("url_bottom_bar_height")));
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        addView(bottomBar, lp1);
        final ImageView leftButton = (ImageView) bottomBar.findViewById(rdrm.getId("type_url_bottom_bar_left"));
        final ImageView centerButton = (ImageView) bottomBar.findViewById(rdrm.getId("type_url_bottom_bar_center"));
        final ImageView rightButton = (ImageView) bottomBar.findViewById(rdrm.getId("type_url_bottom_bar_right"));
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == rdrm.getId("type_url_bottom_bar_left")) {
                    webView.getRefreshableView().goBack();
                } else if (v.getId() == rdrm.getId("type_url_bottom_bar_center")) {
                    webView.getRefreshableView().reload();
                } if (v.getId() == rdrm.getId("type_url_bottom_bar_right")) {
                    webView.getRefreshableView().goForward();
                }
                //leftButton.setEnabled(webView.getRefreshableView().canGoBack());
                //rightButton.setEnabled(webView.getRefreshableView().canGoForward());
            }
        };
        leftButton.setOnClickListener(listener);
        centerButton.setOnClickListener(listener);
        rightButton.setOnClickListener(listener);

        LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ABOVE, bottomBar.getId());
        lp.addRule(RelativeLayout.BELOW, titleBar.getId());
        addView(webView, lp);
    }

    /**
     *
     * @param title
     */
    void setHttpTitle(String title) {
        if (mHttpTitleTV != null) {
            mHttpTitleTV.setText(title);
        }
    }

    void setHttpProgress(int progress) {
        if (mHtppProgressBar != null) {
            mHtppProgressBar.setProgress(progress);
            if (progress >= 100) {
                //mHtppProgressBar.setVisibility(View.GONE);
            } else {
                if (mHtppProgressBar.getVisibility() == View.GONE) {
                    //mHtppProgressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 关闭窗口
     *
     * @param windowName 窗口名字，如果为空或空值则关闭当前页面， 非空值则关闭window
     */
    public void closeWindow(String windowName) {
        mActivity.closeWindow(windowName);
    }


    /**
     * 关闭自己
     */
    public void closeSelf() {
        closeWindow(getName());
    }


    /**
     * The communication between windows.
     *
     * @param targetWindowName 目标窗口名字
     * @param script           执行的script
     */
    public void evaluateJavascript(String targetWindowName, String script) {
        HybridWindow destWindow = mActivity.findWindowByName(targetWindowName);
        HybridView destView = destWindow.mMainView.getRefreshableView();
        destView.evaluateJavascript(script, null);
    }

    public void evalutateJavascript(String script) {
        mMainView.getRefreshableView().evaluateJavascript(script, null);
    }

    /**
     * 浮动窗口的通信
     */

    /**
     * 是否能直接退出
     *
     * @return
     */
    protected boolean needExit() {
//        return mViewList.size() == 0;
        return true;
    }

    /**
     * 注册javascript响应对象。
     *
     * @param rdView
     */
    private void registerPlugins(HybridView rdView) {
        PluginManagerBase fm = HybridEnv.getPluginManager();
        fm.registerPlugin(rdView);
    }

    @Override
    protected void onDetachedFromWindow() {
        PluginData pluginData = null;
        for (PluginBase base : mInjectedFMJSObj.values()) {
            pluginData = base.getPluginData();

            if (pluginData.mScope == PluginData.Scope.Window) {
                base.onDestroy();
            }
        }
        mInjectedFMJSObj.clear();

        super.onDetachedFromWindow();
    }

    public String getName() {
        return mName;
    }

    public boolean canBack() {
        return mViewList.size() > 0;
    }

    public void back() {
        closeWindow(getName());
    }

    public void onLoad() {
        mMainView.getRefreshableView().onLoad();
    }

    public void onForeground() {
        mMainView.getRefreshableView().onForeground();
    }

    public void onBackground() {
        mMainView.getRefreshableView().onBackground();
    }

    public void onDestroy() {
        mMainView.getRefreshableView().onDestroy();
    }

    public void onWindowSizeChanged(Configuration newConfig) {
        mMainView.getRefreshableView().onSizeChanged(newConfig);
    }

    public void setWindowFrame(int x, int y, int width, int height) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
        if (lp == null) {
            lp = new RelativeLayout.LayoutParams(width, height);
        } else {
            lp.width = width;
            lp.height = height;
        }
        lp.leftMargin = x;
        lp.topMargin = y;
        setLayoutParams(lp);
    }

    public void setWindowVisible(boolean visible) {
        setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 打开activity。
     *
     * @param view
     * @param intent
     * @param requestCode
     * @param pluginId
     */
    public void startActivityForResult(HybridView view, Intent intent, int requestCode, int pluginId) {
        mActivity.startActivityForResult(intent, requestCode, this, pluginId);
    }

    public void onActivityResult(int pluginId, int requestCode, int resultCode, Intent data) {
        PullToRefreshWebView rdView = mMainView;
        PluginBase base;
        Map<Integer, PluginBase> map1 = rdView.getRefreshableView().getInjectedPluginJSObj();
        base = map1.get(pluginId);
        if (base != null) {
            base.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void requestPermissions(HybridView view, String[] permissions, int requestCode, int pluginId) {
        mActivity.requestPermissions(permissions, requestCode, this, pluginId);
    }

    public void onRequestPermissionsResult(String[] permissions, int[] grantResults, int requestCode, int pluginId) {
        PullToRefreshWebView rdView = mMainView;
        PluginBase plugin;

        if (HybridEnv.getPluginManager().onRequestPermissionsResult(pluginId, requestCode, permissions, grantResults)) {
            return;
        }

        Map<Integer, PluginBase> pluginMap = rdView.getRefreshableView().getInjectedPluginJSObj();
        plugin = pluginMap.get(pluginId);
        if (plugin != null) {
            plugin.onRequestPermissionsResultInner(requestCode, permissions, grantResults);
        }
    }

    public Map<Class<?>, PluginBase> getInjectedPluginJSObj() {
        return mInjectedFMJSObj;
    }

}
