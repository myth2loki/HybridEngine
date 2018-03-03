package com.xhrd.mobile.hybridframework.framework;

import android.content.Context;

import com.xhrd.mobile.hybridframework.engine.EngineEventListener;
import com.xhrd.mobile.hybridframework.util.SystemUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedList;

/**
 *
 * PluginManager工场，用来获取相应的manager（区分6.0系统）
 * Created by az on 16/1/6.
 */
class PluginManagerFactory {

    /**
     * 获取FrameworkManager
     * @return
     */
    public static PluginManagerBase getFrameworkManager(){
        if (SystemUtil.isMarshmallow()){
            return new FrameworkManager6();
        }else {
            return new FrameworkManager();
        }
    }


    /**
     * 获取PluginManager
     * @param plugins
     * @param mustInitObj
     * @param context
     * @return
     */
    public static PluginManagerBase getPluginManager(XmlPullParser plugins, LinkedList<EngineEventListener> mustInitObj, Context context){
        if (SystemUtil.isMarshmallow()){
            return new PluginManager6(plugins, mustInitObj, context);
        }else {
            return new PluginManager(plugins, mustInitObj, context);
        }
    }

}
