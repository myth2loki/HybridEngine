package com.xhrd.mobile.hybrid.framework;

/**
 * Created by wangqianyu on 15/12/9.
 */
public class JSObject implements IJSData {
    String mScript = "";

    /**
     * 初始化
     * @param script 要返回的javascript对象
     */
    public JSObject(String script) {
        mScript = script;
    }

    public String getScript() {
        return String.valueOf(mScript);
    }
}
