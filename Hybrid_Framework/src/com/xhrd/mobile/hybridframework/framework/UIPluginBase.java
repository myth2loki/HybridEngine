package com.xhrd.mobile.hybridframework.framework;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.engine.RDCloudView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 供外部插件使用。
 * Created by wangqianyu on 15/4/13.
 */
public abstract class UIPluginBase extends PluginBase {
    private int x, y, w, h;
    private View mView;

    public UIPluginBase() {
    }


    /**
     * 设置窗口位置和大小
     *
     * @param rdCloudView
     * @param params     [x, y, width, height]
     */
    @PluginManagerBase.JavascriptUiFunction
    @JavascriptFunction(name = "setFrame")
    public void setFrame(RDCloudView rdCloudView, String[] params) {
        RDCloudView view = rdCloudView;
//        if (params.length < 1) {
//            jsErrCallbackParamsLengthError(view);
//            //jsErrCallback(view, EXCEPTION_JSON, view.getContext().getString(RDResourceManager.getInstance().getStringId("params_length_error")));
//            return;
//        }
        try {
            JSONObject jo = new JSONObject(params[0]);
            x = jo.getInt("x");
            y = jo.getInt("y");
            w = jo.getInt("width");
            if (w == 0) {
                w = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            h = jo.getInt("height");
            if (h == 0) {
                h = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "parse parameters of frame failed", e);
        }

    }

    /**
     * 显示
     *
     * @param rdCloudView
     * @param params
     */
    @PluginManagerBase.JavascriptUiFunction
    @JavascriptFunction(name = "show", hasReturn = true)
    public final void show(RDCloudView rdCloudView, String[] params) {
        final RDCloudView target = rdCloudView;
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(w, h);
        final WebView.LayoutParams lp = new WebView.LayoutParams(vlp);
        lp.x = x;
        lp.y = y;
        //显示
        target.getSelfView().post(new Runnable() {
            @Override
            public void run() {
                if (mView == null) {
                    mView = genUI(target);
                    mView.setLayoutParams(lp);
                    onShow(target, mView);
                    target.addView(mView);
                } else {
                    if (mView.getParent() == null) {
                        mView.setLayoutParams(lp);
                        onShow(target, mView);
                        target.addView(mView);
                    } else {
                        onShow(target, mView);
                        mView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 即将显示时回调
     * @param rdCloudView
     * @param view
     */
    protected void onShow(RDCloudView rdCloudView, View view) {}

    /**
     * 隐藏当前视图
     *
     * @param rdCloudView
     * @param params
     */
    @PluginManagerBase.JavascriptUiFunction
    @JavascriptFunction(name = "hide", hasReturn = true)
    public final void hide(RDCloudView rdCloudView, String[] params) {
        if (mView != null && mView.getVisibility() == View.VISIBLE) {
            onHide(rdCloudView, mView);
            mView.setVisibility(View.GONE);
        }
    }

    /**
     * 即将隐藏时回调
     * @param rdCloudView
     * @param view
     */
    protected void onHide(RDCloudView rdCloudView, View view) {}

    /**
     * 移除当前视图
     *
     * @param rdCloudView
     * @param params
     */
    @PluginManagerBase.JavascriptUiFunction
    @JavascriptFunction(name = "remove")
    public final void remove(RDCloudView rdCloudView, String[] params) {
        final RDCloudView target = rdCloudView;
        if (mView != null) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup container = (ViewGroup) target;
                    onRemove(target, mView);
                    container.removeView(mView);
                    mView = null;
                }
            });
        }
    }

    /**
     * 即将删除时回调
     * @param rdCloudView
     * @param view
     */
    protected void onRemove(RDCloudView rdCloudView, View view) {}

    @PluginManagerBase.JavascriptUiFunction
    @JavascriptFunction(name = "isShowing", convertJS = true)
    public boolean isShowing(RDCloudView rdCloudView, String[] params) {
        return mView != null && mView.getVisibility() == View.VISIBLE;
    }

    /**
     * 覆写此方法，表明是否是UI插件。
     *
     * @return
     */
    public boolean isUI() {
        return false;
    }

    protected void addMethodProp(PluginData data) {
        if (isUI()) {
            data.addMethod("setFrame");
            data.addMethodWithReturn("show");
            data.addMethodWithReturn("hide");
            data.addMethod("remove");
            data.addMethodWithConvertReturn("isShowing");
        }
    }

    /**
     * 获取genUI生成的View;
     *
     * @return
     */
    protected View getUI(RDCloudView target) {
        if (mView == null) {
            mView = genUI(target);
        }
        return mView;
    }


    @Override
    public final PluginData.Scope getScope() {
        return getPluginData().mScope;
    }

    /**
     * 生成UI视图。
     *
     * @param view 当前RDCloudView
     * @return
     */
    protected abstract View genUI(RDCloudView view);
}
