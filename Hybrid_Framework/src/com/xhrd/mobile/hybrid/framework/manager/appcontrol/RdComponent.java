package com.xhrd.mobile.hybrid.framework.manager.appcontrol;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.PluginBase;

/**
 * Created by lilong on 15/4/27.
 */
public class RdComponent extends PluginBase {


    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("openWindow");
        data.addMethod("closeWindow");
    }

   @JavascriptFunction
   public void openWindow(String[]  params){







   }
    @JavascriptFunction
    public void closeWindow(String[]  params){

    }



}
