package com.xhrd.mobile.hybridframework.framework;

/**
 * Created by wangqianyu on 15/12/9.
 */
public class JSString implements IJSData {
    String mScript = "";

    /**
     * 初始化
     * @param script 要返回的javascript字符串
     */
    public JSString(String script) {
        mScript = script;
    }

    public String getScript() {
        return "'" + mScript + "'";
    }
}
