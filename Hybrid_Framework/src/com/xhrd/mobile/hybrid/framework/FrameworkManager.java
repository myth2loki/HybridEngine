package com.xhrd.mobile.hybrid.framework;

import android.webkit.JsPromptResult;

import com.xhrd.mobile.hybrid.engine.HybridView;

import java.util.Map;

/**
 * 管理插件。
 */
public class FrameworkManager extends PluginManagerBase {

    @Override
    protected Object invokePluginMethodByName(HybridView view, PluginBase base, PluginData pluginData, String methodName, Object[] params, JsPromptResult jsPromptResult) {
        return invokePluginMethodByNameInner(view, base, methodName, params);
    }

    @Override
    protected Map<Class<?>, PluginBase> getWindowInjectedJSObj(HybridView view) {
        return view.getRDCloudWindow().getInjectedPluginJSObj();
    }

    @Override
    protected Map<Integer, PluginBase> getViewInjectedJSObj(HybridView view) {
        return view.getInjectedPluginJSObj();
    }
}
