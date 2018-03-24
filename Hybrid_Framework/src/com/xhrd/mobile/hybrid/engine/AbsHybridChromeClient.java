package com.xhrd.mobile.hybrid.engine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.EditText;

import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.IJSData;
import com.xhrd.mobile.hybrid.framework.JSFuncResult;
import com.xhrd.mobile.hybrid.framework.PluginManager;
import com.xhrd.mobile.hybrid.util.SystemUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Web Chrome Client管理器。
 */
public abstract class AbsHybridChromeClient extends WebChromeClient {

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder dia = new AlertDialog.Builder(view.getContext());
        dia.setTitle("提示消息");
        dia.setMessage(message);
        dia.setCancelable(false);
        dia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        dia.create();
        dia.show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder dia = new AlertDialog.Builder(view.getContext());
        dia.setMessage(message);
        dia.setTitle("确认消息");
        dia.setCancelable(false);
        dia.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
        dia.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
        dia.create();
        dia.show();
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
        if (PluginManager.isJavaScript(message)) {
            List<String> params = new ArrayList<>();
            if (!TextUtils.isEmpty(defaultValue)) {
                try {
                    JSONArray jo = new JSONArray(defaultValue);
                    for (int i = 0; i < jo.length(); i++) {
                        params.add(String.valueOf(jo.get(i)));
                    }
                } catch (JSONException e) {
                    //params = Arrays.asList(defaultValue.split(","));
                }
            }

            Object tempRet = HybridEnv.getPluginManager().exec((HybridView) view, message, params, result);
            exec(view, tempRet, result);
            return true;
        } else {
            AlertDialog.Builder dia = new AlertDialog.Builder(view.getContext());
            dia.setTitle(null);
            dia.setMessage(message);
            final EditText input = new EditText(view.getContext());
            if (defaultValue != null) {
                input.setText(defaultValue);
            }
            input.setSelectAllOnFocus(true);
            dia.setView(input);
            dia.setCancelable(false);
            dia.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm(input.getText().toString());
                        }
                    });
            dia.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            dia.create();
            dia.show();
        }
        return true;
    }

    void resultBack(JsPromptResult result, JSFuncResult funcResult) {
        Object ret = funcResult.getResult();
        if (ret != null) {
            if (ret instanceof IJSData) {
                IJSData data = (IJSData) ret;
                if (SystemUtil.isKitKat()) {
                    result.confirm(data.getScript());
                } else {
                    StringBuilder js = new StringBuilder();
                    if (!TextUtils.isEmpty(funcResult.getPreScript())) {
                        js.append(funcResult.getPreScript()).append(',');
                    }
                    js.append(data.getScript());
                    result.confirm(js.toString());
                }
            } else {
                if (SystemUtil.isKitKat()) {
                    result.confirm(String.valueOf(ret));
                } else {
                    StringBuilder js = new StringBuilder();
                    if (!TextUtils.isEmpty(funcResult.getPreScript())) {
                        js.append(funcResult.getPreScript()).append(',');
                    }
                    js.append(funcResult.getResult().toString());
                    result.confirm(js.toString());
                }
            }
        } else {
            result.cancel();
        }
    }

    /**
     * Report a JavaScript console message to the host application. The ChromeClient
     * should override this to process the log message as they see fit.
     *
     * @param consoleMessage Object containing details of the console message.
     * @return true if the message is handled by the client.
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d("js Console: ", consoleMessage.lineNumber()+", "+consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
    }

//    @Override
//    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
//    }
    

    /**
     * Tell the host application the current progress of loading a page.
     *
     * @param view        The WebView that initiated the callback.
     * @param newProgress Current page loading progress, represented by
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress >= 100) {
            ((HybridView) view).disableLoadingBar();
            HybridWindow window = ((HybridView) view).getHybridWindow();
            if (window != null) {
                window.setHttpProgress(newProgress);
            }
        }
//        if (newProgress == 25) {
//            view.loadUrl(String.format(RDCloudScript.Pre_RDScript, ((RDCloudView) view).getWindowName()));
//            view.loadUrl(RDCloudScript.RDScript);
//        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        HybridWindow window = ((HybridView) view).getHybridWindow();
        if (window != null) {
            window.setHttpTitle(title);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(requiredStorage * 2);
    }

    public void preResultBack(final HybridView view, final JsPromptResult result, Object tempRet) {
    }

     protected abstract void exec(WebView view, Object tempRet, final JsPromptResult result);
}
