package com.xhrd.mobile.hybrid.framework;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangqianyu on 15/12/9.
 */
public class JSFunction implements IJSData {
    private String mFunction = "";
    private List<IJSData> mParamList = new ArrayList<IJSData>();
    private boolean isRemove;

    /**
     *
     * @param function JS函数体，不能是代码片段
     * @param isRemove 是否删除JS端代理
     */
    public JSFunction(String function, boolean isRemove) {
        this.isRemove = isRemove;
        this.mFunction = function;
    }

    /**
     * 添加参数
     * @param data
     */
    public final void addParam(IJSData data) {
        if (data != null) {
            mParamList.add(data);
        }
    }

    /**
     * 添加字符串参数
     * @param str
     */
    public final void addParamString(String str) {
        if (!TextUtils.isEmpty(str)) {
            addParam(new JSString(str));
        }
    }

    /**
     * 添加数字参数
     * @param number 数字
     * @param hasDecPoint 是否还有小数点
     */
    public final void addParamNumber(Number number, boolean hasDecPoint) {
        if (number != null) {
            addParam(new JSNumber(number, hasDecPoint));
        }
    }

    /**
     * 添加浮点数字参数
     * @param number
     */
    public final void addParamNumber(Number number) {
        if (number != null) {
            addParam(new JSNumber(number));
        }
    }

    /**
     * 添加对象参数
     * @param script 对象脚本
     */
    public final void addParamObject(String script) {
        if (!TextUtils.isEmpty(script)) {
            addParam(new JSObject(script));
        }
    }

    /**
     * 添加多个参数
     * @param dataList
     */
    public final void addParams(List<IJSData> dataList) {
        if (dataList != null) {
            mParamList.addAll(dataList);
        }
    }

    /**
     * 添加多个参数
     * @param datas
     */
    public final void addParams(IJSData... datas) {
        if (datas != null) {
            mParamList.addAll(Arrays.asList(datas));
        }
    }

    /**
     * 添加多个参数
     * @param dataList
     */
    public final void addParams(IJSDataList dataList) {
        if (dataList != null) {
            for (IJSData data : dataList.getList()) {
                mParamList.add(data);
            }
        }
    }

    @Override
    public String getScript() {
        if (!TextUtils.isEmpty(mFunction)) {
            StringBuilder paramStr = new StringBuilder();
            for (IJSData param : mParamList) {
                paramStr.append(param.getScript()).append(',');
            }
            if (paramStr.charAt(paramStr.length() - 1) == ',') {
                paramStr.deleteCharAt(paramStr.length() - 1);
            }

            List<String> keyAndFunc = PluginBase.getKeyAndFunc(mFunction);
            StringBuffer js = new StringBuffer("javascript:var f =").append(keyAndFunc.get(0)).append("; f(").append(paramStr).append(");");
            if (isRemove) {
                PluginBase.addRemoveAfterCall(js, keyAndFunc);
            }
            return js.toString();
        }
        return "";
    }
}
