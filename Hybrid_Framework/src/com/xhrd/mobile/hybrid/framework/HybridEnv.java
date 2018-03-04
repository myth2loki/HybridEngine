package com.xhrd.mobile.hybrid.framework;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.xhrd.mobile.hybrid.engine.RDCloudScript;
import com.xhrd.mobile.hybrid.framework.manager.actionsheet.ActionSheet;
import com.xhrd.mobile.hybrid.framework.manager.appcontrol.AppWidget;
import com.xhrd.mobile.hybrid.framework.manager.appcontrol.Window;
import com.xhrd.mobile.hybrid.framework.manager.audio.Audio;
import com.xhrd.mobile.hybrid.framework.manager.camera.camera;
import com.xhrd.mobile.hybrid.framework.manager.device.device;
import com.xhrd.mobile.hybrid.framework.manager.device.display;
import com.xhrd.mobile.hybrid.framework.manager.device.networkInfo;
import com.xhrd.mobile.hybrid.framework.manager.device.os;
import com.xhrd.mobile.hybrid.framework.manager.device.screen;
import com.xhrd.mobile.hybrid.framework.manager.eventbus.EventListener;
import com.xhrd.mobile.hybrid.framework.manager.gallery.gallery;
import com.xhrd.mobile.hybrid.framework.manager.geolocation.geolocation;
import com.xhrd.mobile.hybrid.framework.manager.progress.progress;
import com.xhrd.mobile.hybrid.framework.manager.storage.storage;
import com.xhrd.mobile.hybrid.framework.manager.toast.ToastManager;
import com.xhrd.mobile.hybrid.util.ResourceUtil;

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
        ResourceUtil.init(context);

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
        sPluginManager.addPlugin(Audio.class);
        sPluginManager.addPlugin(camera.class);
        sPluginManager.addPlugin(Window.class);
        sPluginManager.addPlugin(geolocation.class);
        sPluginManager.addPlugin(storage.class);
        sPluginManager.addPlugin(gallery.class);
        sPluginManager.addPlugin(EventListener.class);
        sPluginManager.addPlugin(ToastManager.class);
        sPluginManager.addPlugin(gallery.class);
        sPluginManager.addPlugin(ActionSheet.class);
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
