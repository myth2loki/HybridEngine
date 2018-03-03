package com.xhrd.mobile.hybridframework.framework;

/**
 * Created by wangqianyu on 15/12/9.
 */
public class JSBoolean implements IJSData {
    private boolean mBoolean;

    /**
     * 初始化
     * @param bool 要返回的javascript布尔值
     */
    public JSBoolean(boolean bool) {
        mBoolean = bool;
    }

    public String getScript() {
        return String.valueOf(mBoolean);
    }
}
