package com.xhrd.mobile.hybridframework.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqianyu on 15/12/10.
 */
public class IJSDataList {
    private List<IJSData> mList = new ArrayList<IJSData>();

    public void addParamString(String str) {
        mList.add(new JSString(str));
    }

    public void addParamNumber(Number number, boolean hasDecPoint) {
        mList.add(new JSNumber(number, hasDecPoint));
    }

    public void addParamNumber(Number number) {
        addParamNumber(number, false);
    }

    public void addParamObject(String object) {
        mList.add(new JSObject(object));
    }

    List<IJSData> getList() {
        return mList;
    }
}
