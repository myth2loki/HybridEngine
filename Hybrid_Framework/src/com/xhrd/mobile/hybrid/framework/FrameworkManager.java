package com.xhrd.mobile.hybrid.framework;

import android.webkit.JsPromptResult;

import com.xhrd.mobile.hybrid.engine.RDCloudView;

import java.util.Map;

/**
 * 管理插件。
 */
public class FrameworkManager extends PluginManagerBase {

    @Override
    protected Object invokePluginMethodByName(RDCloudView view, PluginBase base, PluginData pluginData, String methodName, Object[] params, JsPromptResult jsPromptResult) {
        return invokePluginMethodByNameInner(view, base, methodName, params);
    }

    @Override
    protected Map<Class<?>, PluginBase> getWindowInjectedJSObj(RDCloudView view) {
        return view.getRDCloudWindow().getInjectedPluginJSObj();
    }

    @Override
    protected Map<Integer, PluginBase> getViewInjectedJSObj(RDCloudView view) {
        return view.getInjectedPluginJSObj();
    }
}
