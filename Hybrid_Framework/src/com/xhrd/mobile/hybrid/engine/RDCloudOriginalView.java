package com.xhrd.mobile.hybrid.engine;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.xhrd.mobile.hybrid.BuildConfig;
import com.xhrd.mobile.hybrid.Config;
import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.Manager.ResManager;
import com.xhrd.mobile.hybrid.framework.Manager.ResManagerFactory;
import com.xhrd.mobile.hybrid.framework.UIPluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.RDComponentInfo;
import com.xhrd.mobile.hybrid.framework.RDComponentInfoManager;
import com.xhrd.mobile.hybrid.util.SystemUtil;

import java.io.File;
import java.io.FileNotFoundException;
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
    private Map<Integer, PluginBase> mInjectedFMJSObj = new HashMap<Integer, PluginBase>();
    private Map<Integer, PluginBase> mInjectedPluginJSObj = new HashMap<Integer, PluginBase>();
    private Map<Class<?>, PluginBase> mInjectedLocalFMJSObj = new HashMap<Class<?>, PluginBase>();
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
        //setLayerType(LAYER_TYPE_SOFTWARE, null);
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
        //RDCloudApplication.getApp().getFrameworkManager().registerPlugin(this);
    	//this.setBackgroundColor(0);
//    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
//    		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//    	}

        if (SystemUtil.isKitKat() && (Config.DEBUG || Config.APP_LOADER)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
//         this.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            this.setBackground(null);
            this.setBackgroundColor(Color.TRANSPARENT);
        } else {
            this.setBackgroundResource(android.R.color.transparent);
        }
//        this.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
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
                //getRDCloudWindow().closeWindow(getWindowName());
            }
        });
//        float density = getContext().getResources().getDisplayMetrics().density;
//        if (density <= 2.0f) {
//            setInitialScale(100);
//        } else if (density <= 2.0f) {
//            setInitialScale(100);
//        } else if (density <= 3.0f) {
//            setInitialScale(90);
//        } else if (density > 3.0f) {
//            setInitialScale(50);
//        }
        //setInitialScale(100);
        setVerticalScrollbarOverlay(true);
        setHorizontalScrollbarOverlay(true);
        setLayoutAnimation(null);
        setAnimation(null);
        setNetworkAvailable(true);
        setFontSize();
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
//        startAnimation(inAnim);
        mLoadingBar.startAnimation(outAnim);
    }

    protected void enableLoadingBar() {
        mLoadingBar.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("deprecation")
    private void setFontSize() {
        //TODO 这段代码有什么用？
//        if (mWebSetting.getTextSize() == WebSettings.TextSize.SMALLEST) {
//            mWebSetting.setTextSize(WebSettings.TextSize.SMALLEST);
//        } else if (mWebSetting.getTextSize() == WebSettings.TextSize.SMALLER) {
//            mWebSetting.setTextSize(WebSettings.TextSize.SMALLER);
//        } else if (mWebSetting.getTextSize() == WebSettings.TextSize.NORMAL) {
//            mWebSetting.setTextSize(WebSettings.TextSize.NORMAL);
//        } else if (mWebSetting.getTextSize() == WebSettings.TextSize.LARGER) {
//            mWebSetting.setTextSize(WebSettings.TextSize.LARGER);
//        } else if (mWebSetting.getTextSize() == WebSettings.TextSize.LARGEST) {
//            mWebSetting.setTextSize(WebSettings.TextSize.LARGEST);
//        }
    }

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
//        if (webApp) {
//            mWebSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//            return;
//        }
//        mWebSetting.setDomStorageEnabled(true);
//        mWebSetting.setDatabaseEnabled(true);
//        mWebSetting.setDatabasePath(getContext().getCacheDir().getAbsolutePath());
//        mWebSetting.setAppCacheEnabled(true);
//        mWebSetting.setAppCachePath(getContext().getCacheDir().getAbsolutePath());
//        mWebSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // disables the actual onscreen controls from showing up
        mWebSetting.setBuiltInZoomControls(false);
        // disables the ability to zoom
        mWebSetting.setSupportZoom(false);

    }

    private void initHttpSettings() {

//        mWebSetting.setSaveFormData(true);
        mWebSetting.setSupportZoom(true);
//        mWebSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
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
     * 获取Page的绝对路径。
     *
     * @param compName 模块名
     * @param pageUrl  页面Url
     * @return 页面的绝对路径
     */
    private String getAbsolutePath(String compName, String pageUrl) {
        if (!TextUtils.isEmpty(compName) && !TextUtils.isEmpty(pageUrl)) {
            String path = ResManagerFactory.getResManager().getPath(ResManager.COMPONENTS_URI);
            //TODO
//            String exPath = AppLoader.getInstance(getContext()).getComponentPath();
//            System.out.println("exPath---->" + exPath);
//            if (!TextUtils.isEmpty(exPath)) {
//                path = exPath;
//            }
            //TODO END


            String relativePath = path + File.separator + compName + File.separator + pageUrl;
//            return "file:///android_asset/" + relativePath;
            relativePath = "file://" + relativePath;
            Log.e("load url-------->", relativePath);
            return relativePath;
        }
        return null;

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
                if (info.type == RDCloudOriginalView.DATA_TYPE_LOCAL) {
                    //String path = getAbsolutePath(info.componentName, info.data);
                    String compName = getRDCloudWindow().getRDCloudComponent().getName();
                    RDComponentInfo compInfo = null;
                    try {
                        compInfo = RDComponentInfoManager.getInstance().getComponentInfo(compName);
                        String path = "file://" + compInfo.path + File.separator + info.data;
                        loadUrl(path);
                    } catch (FileNotFoundException e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(getClass().getSimpleName(), "can not find component " + compName , e);
                        }
                    }
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
        if (plugin instanceof UIPluginBase) {
            if (mInjectedPluginJSObj.containsKey(((UIPluginBase) plugin).getId())) {
                return;
            }
            UIPluginBase base = (UIPluginBase) plugin;
            base.onRegistered(this);
            mInjectedPluginJSObj.put(base.getId(), base);
        } else if (plugin instanceof PluginBase) {
            if (mInjectedFMJSObj.containsKey(((PluginBase) plugin).getId())) {
                return;
            }
            PluginBase base = (PluginBase) plugin;
            base.onRegistered(this);
            mInjectedFMJSObj.put(base.getId(), base);
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
        pluginList.addAll(mInjectedFMJSObj.values());
        pluginList.addAll(mInjectedPluginJSObj.values());
        pluginList.addAll(mInjectedLocalFMJSObj.values());
        pluginList.addAll(mInjectedLocalPluginJSObj.values());
        mInjectedFMJSObj.clear();
        mInjectedPluginJSObj.clear();
        mInjectedLocalFMJSObj.clear();
        mInjectedLocalPluginJSObj.clear();

        for (PluginBase base : pluginList) {
            base.onDeregistered(this);
            if (base.getPluginData().mScope == PluginData.Scope.createNew) {
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

        // to do 推送回调处理
        SharedPreferences mSp = getContext().getSharedPreferences("push", Context.MODE_PRIVATE);
        if (mSp != null && mSp.contains("push")) {
            String msg = mSp.getString("push", "");
            String type = mSp.getString("push_type", "");
            if (!TextUtils.isEmpty(msg) && getWindowName().equals("root")) {
                String func = "javascript:if(plugin_push){plugin_push('" + type + "'," + msg + ");}";
                Log.e("push "+getWindowName(), func + ".............");
                loadUrl(func);
                mSp.edit().clear().apply();
            }
        }
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

//    public void setPopoverRect(int x, int y, int width, int height) {
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
//        if (lp == null) {
//            lp = new FrameLayout.LayoutParams(width, height);
//        } else {
//            lp.width = width;
//            lp.height = height;
//        }
//        lp.leftMargin = x;
//        lp.topMargin = y;
//
//        setLayoutParams(lp);
//    }

    public Map<Integer, PluginBase> getInjectedFMJSObj() {
        return mInjectedFMJSObj;
    }

    public Map<Integer, PluginBase> getInjectedPluginJSObj() {
        return mInjectedPluginJSObj;
    }

    public Map<Class<?>, PluginBase> getInjectedLocalFMJSObj() {
        return mInjectedLocalFMJSObj;
    }

    public Map<Class<?>, PluginBase> getInjectedLocalPluginJSObj() {
        return mInjectedLocalPluginJSObj;
    }

    public void setPopoverVisible(boolean visible) {
        setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
