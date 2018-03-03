package com.xhrd.mobile.hybrid.framework.Manager.progress;

import com.xhrd.mobile.hybrid.annotation.JavascriptConfig;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.annotation.JavascriptProperty;
import com.xhrd.mobile.hybrid.engine.RDCloudView;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maxinliang on 15/6/27.
 */
@JavascriptConfig(scope = PluginData.Scope.app, properties = {
        @JavascriptProperty(name = "ANIMATIONTYPE_FADE", value = "0"),
        @JavascriptProperty(name = "ANIMATIONTYPE_ZOOM", value = "1")
})
public class progress extends PluginBase {

    private static final int ANIMATIONTYPE_FADE = 0;//进度提示框渐隐渐显动画呈现
    private static final int ANIMATIONTYPE_ZOOM = 1;//进度提示框缩放动画呈现

    private RDDialog dialog;

    @JavascriptFunction(name = "showProgress")
    public void showProgress(RDCloudView rdCloudView, String[] params) {

        if (params.length == 1) {
            String configJson = params[0];
            dialog = getProgress(configJson, false);
            dialog.show();
        } else if (params.length == 2) {
            //        int anim = Integer.parseInt(params[0]);
            //        if (ANIMATIONTYPE_FADE == anim){
            //        } else if(ANIMATIONTYPE_ZOOM == anim){
            //        }

            String configJson = params[1];
            dialog = getProgress(configJson, false);
            dialog.show();
        }else{
            jsErrCallbackParamsLengthError(rdCloudView);
        }
    }

    /**
     * @param rdCloudView
     * @param params
     */
    @JavascriptFunction(name = "showToast")
    public void showToast(RDCloudView rdCloudView, String[] params) {

        if (params.length < 2) {
            return;
        }

        int time = Integer.parseInt(params[0]);
        String configJson = params[1];
        dialog = getProgress(configJson, true);
        dialog.show();

        // 显示的时间（如果time=0,则一直不消失，等待执行hideProgress()方法）
        rdCloudView.getRDCloudWindow().postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (dialog) {
                    if (dialog != null)
                        dialog.dismiss();
                }
            }
        }, time * 1000);
    }

    /**
     * 获取progress
     *
     * @param config
     */
    private RDDialog getProgress(String config, boolean isToast) {
        try {
            JSONObject obj = new JSONObject(config);

            // 背景色
            String bgColor = obj.isNull("bgColor") ? null : obj.getString("bgColor");
            // 背景alpha值（0-1）
            double bgOpacity = obj.isNull("bgOpacity") ? 0.8 : obj.getDouble("bgOpacity");
            int alpha = (int) (bgOpacity * 255);// 转换alpha值

            // 标题
            String titleName = null;
            String titleFontSize = null;
            String titleColor = null;
            if (!obj.isNull("title")) {
                JSONObject title = obj.getJSONObject("title");
                titleName = title.isNull("name") ? null : title.getString("name");
                titleFontSize = title.isNull("fontSize") ? null : title.getString("fontSize");
                titleColor = title.isNull("color") ? null : title.getString("color");
            }

            // 子标题
            String detailTitleName = null;
            String detailTitleFontSize = null;
            String detailTitleColor = null;
            if (!obj.isNull("detailTitle")) {
                JSONObject detailTitle = obj.getJSONObject("detailTitle");
                detailTitleName = detailTitle.isNull("name") ? null : detailTitle.getString("name");
                detailTitleFontSize = detailTitle.isNull("fontSize") ? null : detailTitle.getString("fontSize");
                detailTitleColor = detailTitle.isNull("color") ? null : detailTitle.getString("color");
            }

            // 图片
            if (!obj.isNull("images")) { // 显示图片
                JSONArray images = obj.getJSONArray("images");
                String[] imgs = new String[images.length()];
                for (int i = 0; i < images.length(); i++) {
                    imgs[i] = images.getString(i);
                }
                return ProgressUtil.createProgressDialog2(HybridActivity.getInstance(), bgColor, alpha, imgs, titleName, titleFontSize, titleColor, detailTitleName, detailTitleFontSize, detailTitleColor);
            } else {                    // 显示菊花
                if (isToast) {
                    return ProgressUtil.createProgressDialog2(HybridActivity.getInstance(), bgColor, alpha, null, titleName, titleFontSize, titleColor, detailTitleName, detailTitleFontSize, detailTitleColor);
                } else {
                    return ProgressUtil.createProgressDialog(HybridActivity.getInstance(), bgColor, alpha, titleName, titleFontSize, titleColor, detailTitleName, detailTitleFontSize, detailTitleColor);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @JavascriptFunction(name = "hideProgress")
    public void hideProgress(RDCloudView rdCloudView, String[] params) {
        if (dialog != null)
            dialog.dismiss();
    }


    @Override
    protected void addMethodProp(PluginData data) {
    }


}


