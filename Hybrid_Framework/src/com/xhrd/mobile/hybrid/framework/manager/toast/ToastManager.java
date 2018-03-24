package com.xhrd.mobile.hybrid.framework.manager.toast;

import android.util.Log;
import android.widget.Toast;

import com.xhrd.mobile.hybrid.annotation.JavascriptConfig;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.engine.HybridWindow;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

/**
 * Toast插件
 */

@JavascriptConfig(domain = "toast", scope = PluginData.Scope.App)
public class ToastManager extends PluginBase {
    private static final String TAG = "Toast";

    /**
     * Toast显示
     *
     * @param view
     * @param params
     */
    @JavascriptFunction(name = "show")
    public void showToast(HybridView view, String[] params) {
        Toast.makeText(view.getContext().getApplicationContext(), ""+params[0], Toast.LENGTH_SHORT).show();
        Log.d(TAG, "showToast: message = " + params[0]);
    }
}
