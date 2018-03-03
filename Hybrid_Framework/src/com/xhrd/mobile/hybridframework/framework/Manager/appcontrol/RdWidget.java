package com.xhrd.mobile.hybridframework.framework.Manager.appcontrol;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.framework.PluginData;
import com.xhrd.mobile.hybridframework.framework.PluginBase;


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
