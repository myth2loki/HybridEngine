package com.xhrd.mobile.hybrid.framework.manager.res;

import android.net.Uri;
import android.text.TextUtils;

import com.xhrd.mobile.hybrid.annotation.JavascriptConfig;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

/**
 * Created by Administrator on 2018/3/3.
 */

@JavascriptConfig(domain = "ResManager", scope = PluginData.Scope.App)
public class ResManager extends PluginBase {
    public static final String DOMAIN = "ResManager";

    public static final String RES_URI = "res";
    public static final String INTERNAL_URI = "internal";
    public static final String CACHE_URI = "cache";

    private static ResManager sInstance;

    private ResManager() {

    }

    public synchronized static ResManager getInstance() {
        if (sInstance == null) {
            sInstance = new ResManager();
        }
        return sInstance;
    }

    @JavascriptFunction(name = "getPath", hasReturn = true, convertJS = true)
    private String getPath(HybridView view, String[] params) {
        if (params.length == 0) {
            return null;
        }

        Uri uri = Uri.parse(params[0]);
        return getPath(uri);
    }

    public String getPath(Uri uri) {
        String path = null;
        if (RES_URI.equalsIgnoreCase(uri.getScheme())) {
            path = "file:///android_assets" + uri.getPath();
        } else if (INTERNAL_URI.equalsIgnoreCase(uri.getScheme())) {
            path = HybridEnv.getApplicationContext().getFilesDir().getParentFile().getAbsolutePath();
        } else if (CACHE_URI.equalsIgnoreCase(uri.getScheme())) {
            path = HybridEnv.getApplicationContext().getCacheDir().getAbsolutePath();
        } else {
            path = uri.toString();
        }
        return path;
    }

    public String getPath(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        return getPath(Uri.parse(uri));
    }
}
