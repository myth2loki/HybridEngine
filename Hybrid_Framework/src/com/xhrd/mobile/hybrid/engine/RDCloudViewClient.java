package com.xhrd.mobile.hybrid.engine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xhrd.mobile.hybrid.framework.Manager.ResManager;
import com.xhrd.mobile.hybrid.framework.Manager.ResManagerFactory;

/**
 * WebViewClient
 * Created by wangqianyu on 15/4/14.
 */
public class RDCloudViewClient extends WebViewClient {
    public static final String RDCLOUD_LOAD_FINISHED = RDCloudViewClient.class.getName() + "_rdcloud_load_finished";
    public static final String RDCLOUD_WINDOW_NAME = RDCloudViewClient.class.getName() + "rdcloud_window_name";
    public static final String RDCLOUD_POPOVER_NAME = RDCloudViewClient.class.getName() + "rdcloud_popover_name";

    private RDCloudView mRDView;
    private boolean mIsLoadFinished;

    public RDCloudViewClient(RDCloudView rdView) {
        this.mRDView = rdView;
    }

    /**
     * 页面加载完成后，初始化JS环境。
     */
    @Override
    public synchronized void onPageFinished(final WebView view, String url) {
        if(!view.getSettings().getLoadsImagesAutomatically()) {
            view.getSettings().setLoadsImagesAutomatically(true);
        }
        view.loadUrl(RDCloudScript.RDScript);
        super.onPageFinished(view, url);
        view.loadUrl("javascript: rd.window._aaa = function(){this.name = arguments[0]}; rd.window._aaa('" + mRDView.getWindowName() + "');  delete rd.window._aaa;");
        RDCloudScript.jsFix(view);
//        System.out.println("RDScript==>"+RDCloudScript.RDScript);
        CookieSyncManager.getInstance().sync();
        //mRDView.disableLoadingBar();
        //或者移除自己。
        ((RDCloudView) view).onLoad();
        ((RDCloudView) view).onForeground();
        view.post(new Runnable() {
            @Override
            public void run() {
                RDCloudWindow window = ((RDCloudView) view).getRDCloudWindow();
                if (window != null) {
                    window.setHttpTitle(view.getTitle());
                }
            }
        });

        if (!mIsLoadFinished) {
            mIsLoadFinished = true;
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(view.getContext());

            Bundle bundle = new Bundle();
            bundle.putString(RDCLOUD_WINDOW_NAME, mRDView.getWindowName());
            bundle.putString(RDCLOUD_POPOVER_NAME, mRDView.getPopName());
            lbm.sendBroadcast(new Intent(RDCLOUD_LOAD_FINISHED).putExtras(bundle));


        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if(Build.VERSION.SDK_INT >= 19) {
            view.getSettings().setLoadsImagesAutomatically(true);
        } else {
            view.getSettings().setLoadsImagesAutomatically(false);
        }
        //if (Config.DEBUG) {
            //view.loadUrl("javascript: window.onerror = function(info, url, line) { alert(info + ' at ' + url + ' line: ' + line); return false; }");
        //}
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Activity activity = (Activity) view.getContext();
        if (url.startsWith("tel:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (url.startsWith("geo:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (url.startsWith("mailto:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (url.startsWith("sms:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String address = null;
                int parmIndex = url.indexOf('?');
                if (parmIndex == -1) {
                    address = url.substring(4);
                } else {
                    address = url.substring(4, parmIndex);
                    Uri uri = Uri.parse(url);
                    String query = uri.getQuery();
                    if ((query != null) && (query.startsWith("body="))) {
                        intent.putExtra("sms_body", query.substring(5));
                    }
                }
                intent.setData(Uri.parse("sms:" + address));
                intent.putExtra("address", address);
                intent.setType("vnd.android-dir/mms-sms");
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        boolean isUrl = url.startsWith("file") || url.startsWith("http");
        if (!isUrl) {
            return true;
        }
//        RDCloudView target = (RDCloudView) view;
//        if (target.isObfuscation()) {
//            target.updateObfuscationHistroy(url, EBrowserHistory.UPDATE_STEP_ADD, false);
//        }
//        if (target.shouldOpenInSystem() && url.startsWith("http")) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(url));
//            activity.startActivity(intent);
//            return true;
//        }
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        //错误时加载默认错误页面。
        view.loadUrl(ResManagerFactory.getResManager().getPath(ResManager.APP_URI) + "/error/404.html");
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        Log.e("-------->", "old scale: " + oldScale + ", newScale: " + newScale);
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Uri url = request.getUrl();
        WebResourceResponse resp = RDEncryptHelper.decode(url.toString());
        if (resp == null) {
            resp = super.shouldInterceptRequest(view, request);
        }
        return resp;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse resp = RDEncryptHelper.decode(url);
        if (resp == null) {
            resp = super.shouldInterceptRequest(view, url);
        }
        return resp;
    }
}

