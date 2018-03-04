package com.xhrd.mobile.hybrid.engine;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.xhrd.mobile.hybrid.Config;
import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.UIPluginBase;
import com.xhrd.mobile.hybrid.util.SystemUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebView类，负责加载HTML界面，执行JS代码
 * Created by wangqianyu on 15/4/10.
 */
class RDCloudOriginalView extends WebView implements RDCloudView {
    private Map<Integer, PluginBase> mInjectedPluginJSObj = new HashMap<Integer, PluginBase>();
    private Map<Class<?>, PluginBase> mInjectedLocalPluginJSObj = new HashMap<Class<?>, PluginBase>();

    private AbsRDCloudChromeClient mChromeClient;
    private WebSettings mWebSetting;
    private RDCloudWindow mWindow;
    private PullToRefreshWebView mRefresableParent;
    private String popName;
    private PullToRefreshWebView mPopoverParentView;
    private String onPullUpCallBack;
    private String onPullDownCallBack;
    private int mCurrentOrientation;


    //加载蒙板
    private LinearLayout mLoadingBar;
    private ProgressBar mLoadingPb;
    public RDCloudOriginalView(RDCloudWindow window) {
        super(window.getContext());
        mWindow = window;
        registerPlugins();
        init();
    }

    @Override
    public AbsRDCloudChromeClient getChromeClient() {
        return mChromeClient;
    }

    public PullToRefreshWebView getRefresableParent() {
        return mRefresableParent;
    }

    public void setRefresableParent(PullToRefreshWebView refresableParent) {
        this.mRefresableParent = refresableParent;
    }


    public String getOnPullUpCallBack() {
        return onPullUpCallBack;
    }

    public void setOnPullDownCallBack(String onPullDownCallBack) {
        this.onPullDownCallBack = onPullDownCallBack;
    }

    public String getOnPullDownCallBack() {
        return onPullDownCallBack;
    }

    public void setOnPullUpCallBack(String onPullUpCallBack) {
        this.onPullUpCallBack = onPullUpCallBack;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void init() {
        if (SystemUtil.isKitKat() && (Config.DEBUG || Config.APP_LOADER)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundColor(Color.TRANSPARENT);
        } else {
            this.setBackgroundResource(android.R.color.transparent);
        }
        //添加加载蒙板
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLoadingBar = new LinearLayout(getContext());
        //mLoadingBar.setBackgroundColor(Color.WHITE);
        mLoadingBar.setGravity(Gravity.CENTER);
        mLoadingPb = new ProgressBar(getContext());
        mLoadingBar.addView(mLoadingPb, lp);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mLoadingBar, flp);

        mWebSetting = getSettings();
        initBaseSetting();

        setWebViewClient(new RDCloudViewClient(this));

        if (SystemUtil.isMarshmallow()){
            mChromeClient = new RDCloudChromeClient6();
        }else {
            mChromeClient = new RDCloudChromeClient();
        }
        setWebChromeClient(mChromeClient);
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent itt = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(itt);
                getRDCloudWindow().closeWindow(getRDCloudWindow().getName());
            }
        });
        setVerticalScrollbarOverlay(true);
        setHorizontalScrollbarOverlay(true);
        setLayoutAnimation(null);
        setAnimation(null);
        setNetworkAvailable(true);
        mCurrentOrientation = getResources().getConfiguration().orientation;
    }

    public void disableLoadingBar() {
//        Animation inAnim = new AlphaAnimation(0.0f, 1.0f);
//        inAnim.setDuration(500);
//        inAnim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                post(new Runnable() {
//                    @Override
//                    public void run() {
//                        setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

        Animation outAnim = new AlphaAnimation(1.0f, 0.0f);
        outAnim.setDuration(500);
        outAnim.setStartOffset(350);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLoadingBar.startAnimation(outAnim);
    }

    protected void enableLoadingBar() {
        mLoadingBar.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
    public void initBaseSetting(/*boolean webApp*/) {
        //mWebApp = webApp;
        mWebSetting.setSaveFormData(false);
        mWebSetting.setSavePassword(false);
        mWebSetting.setLightTouchEnabled(false);
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setNeedInitialFocus(false);
        mWebSetting.setSupportMultipleWindows(false);
        mWebSetting.setAllowFileAccess(true);
        // mWebSetting.setNavDump(false);
        //mWebSetting.setPluginsEnabled(true);
        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(false);
        mWebSetting.setUseWideViewPort(false);
        mWebSetting.setLoadsImagesAutomatically(true);
        mWebSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebSetting.setUserAgentString(USERAGENT);
        mWebSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebSetting.setDefaultTextEncodingName("UTF-8");
        if (Build.VERSION.SDK_INT <= 7) {
            invokeHtml5(mWebSetting);
        }

        mWebSetting.setDomStorageEnabled(true);
        mWebSetting.setDatabaseEnabled(true);
        mWebSetting.setDatabasePath(getContext().getCacheDir().getAbsolutePath());
        mWebSetting.setAppCacheEnabled(true);
        mWebSetting.setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        mWebSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // disables the actual onscreen controls from showing up
        mWebSetting.setBuiltInZoomControls(false);
        // disables the ability to zoom
        mWebSetting.setSupportZoom(false);

    }

    private void initHttpSettings() {
        mWebSetting.setSupportZoom(true);
        mWebSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @SuppressWarnings("rawtypes")
    private void invokeHtml5(WebSettings setting) {
        try {
            String path = getContext().getDir("database", 0).getPath();
            Class[] paramTypes = new Class[]{boolean.class};
            Class[] param1 = new Class[]{String.class};
            Method databaseEnabled = WebSettings.class.getMethod("setDatabaseEnabled", paramTypes);
            databaseEnabled.invoke(setting, true);
            Method domStorageEnabled = WebSettings.class.getMethod("setDomStorageEnabled", paramTypes);
            domStorageEnabled.invoke(setting, true);
            Method databasePath = WebSettings.class.getMethod("setDatabasePath", param1);
            databasePath.invoke(setting, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 载入数据。
     *
     * @param info
     */
    public void load(final RDCloudWindowInfo info) {
        //将预加载javascript放到这里，保证4.4以前版本加载正确。
        String name = TextUtils.isEmpty(getPopName()) ? getWindowName() : getPopName();
        loadUrl(String.format(RDCloudScript.Pre_RDScript, name));
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (info.type == RDCloudOriginalView.DATA_TYPE_ASSET) {
                    String path = "file:///android_asset/hybrid/App" + File.separator + info.data ;
                    loadUrl(path);
                } else if (info.type == RDCloudOriginalView.DATA_TYPE_TEXT) {
                    loadData(info.data, "text/html; charset=utf-8", null);
                } else if (info.type == RDCloudOriginalView.DATA_TYPE_URL) {
                    initHttpSettings();
                    loadUrl(info.data);
                }
            }
        }, 0);
    }

    /**
     * 注册所有公共插件
     */
    private void registerPlugins() {
        HybridEnv.getPluginManager().registerPlugin(this);
    }

    /**
     * 注册插件
     *
     * @param plugin
     */
    public void registerPlugin(Object plugin) {
        if (getRDCloudWindow() == null) {
            //已经关闭
            return;
        }
        if (plugin instanceof PluginBase) {
            if (mInjectedPluginJSObj.containsKey(((PluginBase) plugin).getId())) {
                return;
            }
            PluginBase base = (PluginBase) plugin;
            base.onRegistered(this);
            mInjectedPluginJSObj.put(base.getId(), base);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerPlugins();
    }

    @Override
    protected void onDetachedFromWindow() {
        mWindow = null;

        List<PluginBase> pluginList = new ArrayList<PluginBase>();
        pluginList.addAll(mInjectedPluginJSObj.values());
        pluginList.addAll(mInjectedLocalPluginJSObj.values());
        mInjectedPluginJSObj.clear();
        mInjectedLocalPluginJSObj.clear();

        for (PluginBase base : pluginList) {
            base.onDeregistered(this);
            if (base.getPluginData().mScope == PluginData.Scope.New) {
                try {
                    base.onDestroy();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), base.getPluginData().mClass.getName() + "'s onDestroy() failed.", e);
                }
            }
        }
        super.onDetachedFromWindow();
    }

    public String getWindowName() {
        if (mWindow == null) {
            return null;
        }
        return mWindow.getName();
    }

    public RDCloudWindow getRDCloudWindow() {
        return mWindow;
    }

    @SuppressLint("NewApi")
    @Override
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        if (Build.VERSION.SDK_INT < 19) {
            loadUrl(JAVASCRIPT_HEAD + script);
        } else {
            super.evaluateJavascript(script, resultCallback);
        }

    }

    //ViewPager里非首屏WebView点击事件不响应，
    //
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//
//        }

        return super.onTouchEvent(ev);
    }



    public void setPopName(String name) {
        popName = name;
    }

    public String getPopName() {
        return popName;
    }

    public PullToRefreshWebView getPopoverParent() {
        return mPopoverParentView;
    }

    public void setPopoverParentView(PullToRefreshWebView parentView) {
        mPopoverParentView = parentView;
    }

    public boolean isPopover() {
        return !TextUtils.isEmpty(popName);
    }

    /**
     * 获取当前名字
     * @return 当前名字，如果是popover返回pop名字，如果是window返回window名字
     */
    public String getName() {
        return isPopover() ? getPopName() : getWindowName();
    }

    public void onLoad() {
        loadUrl("javascript:if(typeof(onLoad) != 'undefined'){onLoad();}");
        loadUrl("javascript:if(rd.onLoad){rd.onLoad();}");
    }

    public void onForeground() {
        loadUrl("javascript:if(typeof(onForeground) != 'undefined'){onForeground();}");
        loadUrl("javascript:if(rd.onForeground){rd.onForeground();}");
    }

    public void onBackground() {
        loadUrl("javascript:if(typeof(onBackground) != 'undefined'){onBackground();}");
        loadUrl("javascript:if(rd.onBackground){rd.onBackground();}");
    }

    public void onDestroy() {
        loadUrl("javascript:if(typeof(onDestroy) != 'undefined'){onDestroy();}");
        loadUrl("javascript:if(rd.onDestroy){rd.onDestroy();}");
    }

    public void onSizeChanged(Configuration newConfig) {
        loadUrl("javascript:if(typeof(onSizeChanged) != 'undefined'){onSizeChanged();}");
        loadUrl("javascript:if(rd.onSizeChanged){rd.onSizeChanged();}");

        if (mCurrentOrientation != newConfig.orientation) {
            loadUrl("javascript:if(typeof(onOrientation) != 'undefined'){onOrientation();}");
            loadUrl("javascript:if(rd.onOrientation){rd.onOrientation();}");
        }
        mCurrentOrientation = newConfig.orientation;
    }

    public Map<Integer, PluginBase> getInjectedPluginJSObj() {
        return mInjectedPluginJSObj;
    }


    public Map<Class<?>, PluginBase> getInjectedLocalPluginJSObj() {
        return mInjectedLocalPluginJSObj;
    }

    public boolean isChildrenRefreshable(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int count = getChildCount();
        PullToRefreshWebView ptrView;
        RDCloudOriginalView rdView;
        for (int i = 0; i < count; i++) {
           View view = getChildAt(i);
            RectF rect = null;
            if (SystemUtil.isHoneyComb()) {
                rect = new RectF(view.getX(), view.getY(), view.getWidth(), view.getHeight());
            } else {
                rect = new RectF(view.getLeft(), view.getTop(), view.getWidth(), view.getHeight());
            }
            if (rect.contains(x, y) && (view instanceof PullToRefreshWebView)) {
                ptrView = (PullToRefreshWebView)view;
                boolean ret = ptrView.isPullLoadEnabled() || ptrView.isPullRefreshEnabled();
                if (ret) {
                    return ret;
                }
            }
        }
        return false;
    }

    public boolean isChildInterceptTouchEventEnabled(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int count = getChildCount();
        PullToRefreshWebView ptrView;
        RDCloudOriginalView rdView;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);

            RectF rect = null;
            if (SystemUtil.isHoneyComb()) {
                rect = new RectF(view.getX(), view.getY(), view.getWidth(), view.getHeight());
            } else {
                rect = new RectF(view.getLeft(), view.getTop(), view.getWidth(), view.getHeight());
            }
            if (rect.contains(x, y) && (view instanceof PullToRefreshWebView)) {
                ptrView = (PullToRefreshWebView)view;
                boolean ret = ptrView.isInterceptTouchEventEnabled();
                if (ret) {
                    return ret;
                }
            }
        }
        return false;
    }

    public boolean isChildPullLoading(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int count = getChildCount();
        PullToRefreshWebView ptrView;
        RDCloudOriginalView rdView;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            RectF rect = null;
            if (SystemUtil.isHoneyComb()) {
                rect = new RectF(view.getX(), view.getY(), view.getWidth(), view.getHeight());
            } else {
                rect = new RectF(view.getLeft(), view.getTop(), view.getWidth(), view.getHeight());
            }
            if (rect.contains(x, y) && (view instanceof PullToRefreshWebView)) {
                ptrView = (PullToRefreshWebView)view;
                boolean ret = ptrView.isPullLoading();
                if (ret) {
                    return ret;
                }
            }
        }
        return false;
    }

    public boolean isChildPullRefreshing(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int count = getChildCount();
        PullToRefreshWebView ptrView;
        RDCloudOriginalView rdView;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            RectF rect = null;
            if (SystemUtil.isHoneyComb()) {
                rect = new RectF(view.getX(), view.getY(), view.getWidth(), view.getHeight());
            } else {
                rect = new RectF(view.getLeft(), view.getTop(), view.getWidth(), view.getHeight());
            }
            if (rect.contains(x, y) && (view instanceof PullToRefreshWebView)) {
                ptrView = (PullToRefreshWebView)view;
                boolean ret = ptrView.isPullRefreshing();
                if (ret) {
                    return ret;
                }
            }
        }
        return false;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        onPullCallback(getOnPullDownCallBack());
    }
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        onPullCallback(getOnPullUpCallBack());
    }
    private void  onPullCallback(String func){
        List<String> keyAndFunc = PluginBase.getKeyAndFunc(func);
        func = keyAndFunc.get(0);
        Handler mHandler = new Handler(HybridActivity.getInstance().getMainLooper());
        final String f = "javascript:var f = " + func + "; f()";
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                 loadUrl(f);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public View getSelfView() {
        return this;
    }
}
