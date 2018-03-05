package com.xhrd.mobile.hybrid.framework;

import android.content.Context;

import com.xhrd.mobile.hybrid.engine.EngineEventListener;
import com.xhrd.mobile.hybrid.util.SystemUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedList;

/**
 *
 * PluginManager工场，用来获取相应的manager（区分6.0系统）
 */
class PluginManagerFactory {

    /**
     * 获取FrameworkManager
     * @return
     */
    public static PluginManagerBase getPluginManager(){
        if (SystemUtil.isMarshmallow()){
            return new FrameworkManager6();
        }else {
            return new FrameworkManager();
        }
    }

}
