package com.xhrd.mobile.hybrid.framework.Manager.appcontrol;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.PluginBase;


public class RdWidget  extends PluginBase {

	@Override
	public void addMethodProp(PluginData data) {
        data.addMethod("startWidget");
        data.addMethod("finishWidget");
        data.addMethod("removeWidget");
	}
	@JavascriptFunction
	public void startWidget() {
		// TODO Auto-generated method stub

	}
	@JavascriptFunction
	public void finishWidget() {
		// TODO Auto-generated method stub

	}
	@JavascriptFunction
	public void removeWidget() {
		// TODO Auto-generated method stub

	}
	 
	
	
	
	

}
