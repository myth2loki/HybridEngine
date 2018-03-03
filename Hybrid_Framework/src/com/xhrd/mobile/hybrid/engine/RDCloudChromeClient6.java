package com.xhrd.mobile.hybrid.engine;

import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.xhrd.mobile.hybrid.framework.JSFuncResult;
import com.xhrd.mobile.hybrid.framework.PluginManagerBase;
import com.xhrd.mobile.hybrid.util.SystemUtil;

/**
 * Web Chrome Client管理器。
 * Created by lang on 15/4/18.
 */
public class RDCloudChromeClient6 extends AbsRDCloudChromeClient {

    @Override
    protected void exec(WebView view, Object tempRet, JsPromptResult result) {
        if (tempRet == PluginManagerBase.PERMISSION_IN_PROGRESS) {// 有权限需申请
            return;
        }

        preResultBack((RDCloudView) view, result, tempRet);
    }

    @Override
    public void preResultBack(final RDCloudView view, final JsPromptResult result, Object tempRet) {
        final JSFuncResult funcRet;
        if (tempRet instanceof JSFuncResult) {
            funcRet = (JSFuncResult) tempRet;
        } else {
            funcRet = new JSFuncResult("", tempRet);
        }
        if (!TextUtils.isEmpty(funcRet.getPreScript())) {
            if (SystemUtil.isKitKat()) {
                view.evaluateJavascript(funcRet.getPreScript(), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        resultBack(result, funcRet);
                    }
                });
            } else {
                resultBack(result, funcRet);
            }
        } else {
            resultBack(result, funcRet);
        }
    }


}
