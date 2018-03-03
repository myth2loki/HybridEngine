package com.xhrd.mobile.hybrid.framework;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.xhrd.mobile.hybrid.engine.RDCloudScript;
import com.xhrd.mobile.hybrid.framework.Manager.I18n;
import com.xhrd.mobile.hybrid.framework.Manager.actionsheet.actionSheet;
import com.xhrd.mobile.hybrid.framework.Manager.appcontrol.AppWidget;
import com.xhrd.mobile.hybrid.framework.Manager.appcontrol.Window;
import com.xhrd.mobile.hybrid.framework.Manager.audio.audio;
import com.xhrd.mobile.hybrid.framework.Manager.camera.camera;
import com.xhrd.mobile.hybrid.framework.Manager.device.device;
import com.xhrd.mobile.hybrid.framework.Manager.device.display;
import com.xhrd.mobile.hybrid.framework.Manager.device.networkInfo;
import com.xhrd.mobile.hybrid.framework.Manager.device.os;
import com.xhrd.mobile.hybrid.framework.Manager.device.screen;
import com.xhrd.mobile.hybrid.framework.Manager.eventbus.EventListener;
import com.xhrd.mobile.hybrid.framework.Manager.gallery.gallery;
import com.xhrd.mobile.hybrid.framework.Manager.geolocation.geolocation;
import com.xhrd.mobile.hybrid.framework.Manager.http.HttpManager;
import com.xhrd.mobile.hybrid.framework.Manager.progress.progress;
import com.xhrd.mobile.hybrid.framework.Manager.properties.PropertiesManager;
import com.xhrd.mobile.hybrid.framework.Manager.storage.storage;
import com.xhrd.mobile.hybrid.framework.Manager.toast.ToastManager;

/**
 * Created by Administrator on 2018/3/2.
 */
public class HybridEnv {

    private static PluginManagerBase sPluginManager;
    private static Context sApplicationContext;

    public static void init(final Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().removeSessionCookie();
        CookieManager.getInstance().removeExpiredCookie();
        sApplicationContext = context.getApplicationContext();

        new Thread(new Runnable() {

            @Override
            public void run() {
                String funcHeader = RDCloudScript.RD + ".require=function(name){";
                String funcFirstPart = "if (!arguments || arguments.length == 0){return;} console.log(arguments[0]);";
                String funcFooter = "};";
                StringBuffer sb = new StringBuffer();
                sb.append(funcHeader);
                sb.append(funcFirstPart);
                sb.append(initFrameworkManager());
                sb.append(funcFooter);
                RDCloudScript.RDScript += sb.toString();
//                context.sendBroadcast(new Intent(WelcomeActivity.LOAD_FINISHED));
            }
        }).start();
    }

    /**
     * 初始化内部插件
     * @return
     */
    private static String initFrameworkManager() {
        sPluginManager = PluginManagerFactory.getFrameworkManager();

        sPluginManager.addPlugin(progress.class);
        sPluginManager.addPlugin(AppWidget.class);
        sPluginManager.addPlugin(device.class);
        sPluginManager.addPlugin(display.class);
        sPluginManager.addPlugin(networkInfo.class);
        sPluginManager.addPlugin(os.class);
        sPluginManager.addPlugin(screen.class);
        sPluginManager.addPlugin(I18n.class);
        sPluginManager.addPlugin(audio.class);
        sPluginManager.addPlugin(camera.class);
        sPluginManager.addPlugin(Window.class);
        sPluginManager.addPlugin(geolocation.class);
        sPluginManager.addPlugin(storage.class);
        sPluginManager.addPlugin(gallery.class);
        sPluginManager.addPlugin(EventListener.class);
        sPluginManager.addPlugin(ToastManager.class);
        sPluginManager.addPlugin(gallery.class);
        sPluginManager.addPlugin(PropertiesManager.class);
        sPluginManager.addPlugin(HttpManager.class);
        sPluginManager.addPlugin(actionSheet.class);
        return sPluginManager.genJavascript();
    }

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
