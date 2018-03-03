package com.xhrd.mobile.hybridframework.framework;

/**
 * 本地代理功能返回结果
 * Created by wangqianyu on 15/11/27.
 */
public class JSFuncResult {
    private JSScript mPreScript;
    private Object mResult;

    /**
     * 初始化
     * @param preScript 前置脚本
     * @param result 返回值
     */
    public JSFuncResult(JSScript preScript, Object result) {
        if (result instanceof JSScript) {
            throw new IllegalArgumentException("could not support JSScript instance.");
        }
        this.mPreScript = preScript;
        this.mResult = result;
    }

    /**
     * 初始化
     * @param preScript 前置脚本
     * @param result 返回值
     */
    public JSFuncResult(String preScript, Object result) {
        this(new JSScript(preScript), result);
    }

    /**
     * 获取前置执行脚本
     * @return
     */
    public String getPreScript() {
        return mPreScript.getScript();
    }

    /**
     * 返回的结果
     * @return
     */
    public Object getResult() {
        return mResult;
    }

    public boolean isIJSData() {
        return mResult instanceof IJSData;
    }
}
