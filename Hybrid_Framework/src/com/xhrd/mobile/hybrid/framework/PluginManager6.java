package com.xhrd.mobile.hybrid.framework;

import android.content.Context;
import android.webkit.JsPromptResult;

import com.xhrd.mobile.hybrid.engine.EngineEventListener;
import com.xhrd.mobile.hybrid.engine.RDCloudView;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedList;

/**
 * 查件管理类，主要负责管理第三方插件。
 */
public class PluginManager6 extends PluginManager {

    public PluginManager6(XmlPullParser plugins, LinkedList<EngineEventListener> mustInitObj, Context context) {
        super(plugins, mustInitObj, context);
    }

    @Override
    protected Object invokePluginMethodByName(RDCloudView view, PluginBase base, PluginData pluginData, String methodName, Object[] params, JsPromptResult jsPromptResult) {
        return invokePluginMethodByNameInner6(view, base, pluginData, methodName, params, jsPromptResult);
    }
}
