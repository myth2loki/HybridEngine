package com.xhrd.mobile.hybridframework.framework;

/**
 * Created by wangqianyu on 15/12/9.
 */
public class JSNumber implements IJSData {
    Number mNumber;
    boolean mHasDecPoint;

    /**
     * 初始化
     * @param number 要返回的javascript数字
     * @param hasDecPoint 是否含有小数点
     */
    public JSNumber(Number number, boolean hasDecPoint) {
        mNumber = number;
        mHasDecPoint = hasDecPoint;
    }

    /**
     * 初始化一个带小数点的数字
     * @param number 要返回的javascript数字
     */
    public JSNumber(Number number) {
        this(number, true);
    }

    public String getScript() {
        return String.valueOf(mHasDecPoint ? mNumber.doubleValue() : mNumber.longValue());
    }
}
