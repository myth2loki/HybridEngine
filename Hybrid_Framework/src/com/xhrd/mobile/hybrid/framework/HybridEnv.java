package com.xhrd.mobile.hybrid.framework;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.xhrd.mobile.hybrid.Config;
import com.xhrd.mobile.hybrid.engine.HybridScript;
import com.xhrd.mobile.hybrid.framework.manager.appcontrol.AppWidget;
import com.xhrd.mobile.hybrid.framework.manager.appcontrol.Window;
import com.xhrd.mobile.hybrid.framework.manager.camera.Camera;
import com.xhrd.mobile.hybrid.framework.manager.eventbus.EventBus;
import com.xhrd.mobile.hybrid.framework.manager.toast.ToastManager;
import com.xhrd.mobile.hybrid.util.ResourceUtil;

/**
 * 混合引擎初始化
 */
public class HybridEnv {
    private static final String TAG = "HybridEnv";
    private static final boolean DEBUG = Config.DEBUG;

    private static PluginManagerBase sPluginManager;
    private static Context sApplicationContext;

    /**
     * 初始化混合引擎
     * @param context
     */
    public static void init(final Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().removeSessionCookie();
        CookieManager.getInstance().removeExpiredCookie();
        sApplicationContext = context.getApplicationContext();
        ResourceUtil.init(context);

        new Thread(new Runnable() {

            @Override
            public void run() {
                String funcHeader = HybridScript.RD + ".require=function(name){";
                String funcFirstPart = "if (!arguments || arguments.length == 0){return;} console.log(arguments[0]);";
                String funcFooter = "};";
                StringBuffer sb = new StringBuffer();
                sb.append(funcHeader);
                sb.append(funcFirstPart);
                sb.append(initDefaultPlugins());
                sb.append(funcFooter);
                HybridScript.RDScript += sb.toString();
            }
        }).start();
    }

    /**
     * 初始化内部插件
     * @return
     */
    private static String initDefaultPlugins() {
        sPluginManager.addPlugin(AppWidget.class);
        sPluginManager.addPlugin(Window.class);
        sPluginManager.addPlugin(EventBus.class);
        sPluginManager.addPlugin(ToastManager.class);
        sPluginManager.addPlugin(Camera.class);
        return sPluginManager.genJavascript();
    }

    public static void addPlugin(Class<? extends PluginBase> clazz) {
        //懒加载
        if (sPluginManager == null) {
            sPluginManager = PluginManagerFactory.getPluginManager();
        }
        if (clazz == null) {
            if (DEBUG) {
                Log.d(TAG, "addPlugin: class of plugin is null.");
            }
        }
        sPluginManager.addPlugin(clazz);
    }

    /**
     * 是否打开WebView调试
     * @param enabled
     */
    public static void enableDebug(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(enabled);
        }
    }

    public static PluginManagerBase getPluginManager() {
        return sPluginManager;
    }

    public static Context getApplicationContext() {
        if (sApplicationContext == null) {
            throw new IllegalStateException("HybridEnv has not initialized yet, Please call init()");
        }
        return sApplicationContext;
    }
}
