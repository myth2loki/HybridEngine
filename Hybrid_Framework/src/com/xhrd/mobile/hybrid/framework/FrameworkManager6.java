package com.xhrd.mobile.hybrid.framework;

import android.webkit.JsPromptResult;

import com.xhrd.mobile.hybrid.engine.HybridView;

import java.util.Map;

/**
 * 管理framework内部插件。
 * Created by wangqianyu on 15/4/16.
 */
public class FrameworkManager6 extends PluginManagerBase {
    @Override
    protected Object invokePluginMethodByName(HybridView view, PluginBase base, PluginData pluginData, String methodName, Object[] params, JsPromptResult jsPromptResult) {
        return invokePluginMethodByNameInner6(view, base, pluginData, methodName, params, jsPromptResult);
    }

    @Override
    protected Map<Class<?>, PluginBase> getWindowInjectedJSObj(HybridView view) {
        return view.getHybridWindow().getInjectedPluginJSObj();
    }

    @Override
    protected Map<Integer, PluginBase> getViewInjectedJSObj(HybridView view) {
        return view.getInjectedPluginJSObj();
    }
}
