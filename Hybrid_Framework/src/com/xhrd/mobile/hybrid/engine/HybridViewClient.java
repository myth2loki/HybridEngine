package com.xhrd.mobile.hybrid.engine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebViewClient
 */
public class HybridViewClient extends WebViewClient {
    public static final String RDCLOUD_LOAD_FINISHED = HybridViewClient.class.getName() + "_rdcloud_load_finished";
    public static final String RDCLOUD_WINDOW_NAME = HybridViewClient.class.getName() + "rdcloud_window_name";
    public static final String RDCLOUD_POPOVER_NAME = HybridViewClient.class.getName() + "rdcloud_popover_name";

    private HybridView mRDView;
    private boolean mIsLoadFinished;

    public HybridViewClient(HybridView rdView) {
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
        view.loadUrl(HybridScript.RDScript);
        super.onPageFinished(view, url);
        view.loadUrl("javascript: (function(){rd.window.name = arguments[0]})('" + mRDView.getWindowName() + "');");
        HybridScript.jsFix(view);
        CookieSyncManager.getInstance().sync();
        //或者移除自己。
        ((HybridView) view).onLoad();
        ((HybridView) view).onForeground();
        view.post(new Runnable() {
            @Override
            public void run() {
                HybridWindow window = ((HybridView) view).getRDCloudWindow();
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
            //view.loadUrl("javascript: Window.onerror = function(info, url, line) { alert(info + ' at ' + url + ' line: ' + line); return false; }");
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
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        //错误时加载默认错误页面。
        view.loadUrl("file:///android_assets/hybrid/app/error/404.html");
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return super.shouldInterceptRequest(view, url);
    }
}

