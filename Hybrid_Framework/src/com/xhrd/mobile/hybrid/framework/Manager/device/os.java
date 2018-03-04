package com.xhrd.mobile.hybrid.framework.Manager.device;

import android.os.Build;

import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.PluginBase;

import java.util.Locale;

/**
 * Created by wangqianyu on 15/4/24.
 */
public class os extends PluginBase {

    @Override
    public void addMethodProp(PluginData data) {
        data.addProperty("language", Locale.getDefault().getLanguage());
        data.addProperty("version", Build.VERSION.RELEASE);
        data.addProperty("name", "Android");
        data.addProperty("vendor", Build.MANUFACTURER);
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.App;
    }
}
