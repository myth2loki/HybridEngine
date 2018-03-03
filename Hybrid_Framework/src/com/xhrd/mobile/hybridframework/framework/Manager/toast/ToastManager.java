package com.xhrd.mobile.hybridframework.framework.Manager.toast;

import android.widget.Toast;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.engine.HybridActivity;
import com.xhrd.mobile.hybridframework.framework.PluginBase;
import com.xhrd.mobile.hybridframework.framework.PluginData;

/**
 * Toast插件
 * Created by kTian on 2015/5/25.
 */
public class ToastManager extends PluginBase {

    /**
     * Toast显示
     *
     * @param windowName
     * @param params
     */
    @JavascriptFunction
    public void showToast(String windowName, String[] params) {
        Toast.makeText(HybridActivity.getInstance(), ""+params[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("showToast");
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }

}
