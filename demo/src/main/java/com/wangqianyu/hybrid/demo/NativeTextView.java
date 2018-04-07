package com.wangqianyu.hybrid.demo;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.UIPluginBase;

/**
 * Created by Administrator on 2018/4/7.
 */

public class NativeTextView extends UIPluginBase {

    private String mText;

    @Override
    protected View genUI(HybridView view) {
        TextView tv = new TextView(view.getContext());
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(Color.RED);
        if (!TextUtils.isEmpty(mText)) {
            tv.setText(mText);
        }
        return tv;
    }

    public void setText(HybridView view, String[] params) {
        if (params.length > 0) {
            mText = params[0];
            TextView tv = (TextView) getUI(view);
            tv.setText(mText);
        }
    }

    @Override
    public String getDefaultDomain() {
        return "nativeTextView";
    }

    @Override
    protected void addMethodProp(PluginData data) {
        super.addMethodProp(data);
        data.addMethod("setText");
    }
}
