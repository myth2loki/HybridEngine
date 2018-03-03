package com.xhrd.mobile.hybridframework.framework;

import android.webkit.JsPromptResult;

import com.xhrd.mobile.hybridframework.engine.RDCloudView;

import java.util.Map;

/**
 * 管理framework内部插件。
 * Created by wangqianyu on 15/4/16.
 */
public class FrameworkManager6 extends PluginManagerBase {
    @Override
    protected Object invokePluginMethodByName(RDCloudView view, PluginBase base, PluginData pluginData, String methodName, Object[] params, JsPromptResult jsPromptResult) {
        return invokePluginMethodByNameInner6(view, base, pluginData, methodName, params, jsPromptResult);
    }

    @Override
    protected Map<Class<?>, PluginBase> getLocalViewInjectedJSObj(RDCloudView view) {
        return view.getInjectedLocalFMJSObj();
    }

    @Override
    protected Map<Class<?>, PluginBase> getWindowInjectedJSObj(RDCloudView view) {
        return view.getRDCloudWindow().getInjectedFMJSObj();
    }

    @Override
    protected Map<Integer, PluginBase> getViewInjectedJSObj(RDCloudView view) {
        return view.getInjectedFMJSObj();
    }
}
