package com.xhrd.mobile.hybrid.framework.Manager.progress;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhrd.mobile.hybrid.engine.RDResourceManager;
import com.xhrd.mobile.hybrid.framework.Manager.res.ResManager;
import com.xhrd.mobile.hybrid.util.LruCacheUtil;

/**
 * Created by maxinliang on 15/6/28.
 */
public class ProgressUtil {

    /**
     * 创建带菊花的ProgressDialog
     *
     * @param context
     * @return
     */
    public static RDDialog createProgressDialog(Context context, String bgFillColor, int alpha, String title, String titleSize, String titleColor, String detailTitle, String detailTitleSize, String detailTitleColor) {
        RDDialog progressDialog = new RDDialog(context);// 创建自定义样式dialog
        progressDialog.setCancelable(true);// 可用“返回键”取消
        progressDialog.setCanceledOnTouchOutside(false);// 不可点击dialog外部取消

        View v = LayoutInflater.from(context).inflate(RDResourceManager.getInstance().getLayoutId("progress_rd_dialog"), null);// 得到加载view

        setProgressPublicData(v, bgFillColor, alpha, title, titleSize, titleColor, detailTitle, detailTitleSize, detailTitleColor);

        progressDialog.setContentView(v);
        return progressDialog;
    }


    /**
     * 创建带图片的ProgressDialog
     *
     * @param context
     * @return
     */
    public static RDDialog createProgressDialog2(Context context, String bgFillColor, int alpha, String[] imgPaths, String title, String titleSize, String titleColor, String detailTitle, String detailTitleSize, String detailTitleColor) {
        RDDialog progressDialog = new RDDialog(context);// 创建自定义样式dialog
        progressDialog.setCancelable(true);// 可用“返回键”取消
        progressDialog.setCanceledOnTouchOutside(false);// 不可点击dialog外部取消

        View v = LayoutInflater.from(context).inflate(RDResourceManager.getInstance().getLayoutId("progress_rd_dialog2"), null);// 得到加载view

        setProgressPublicData(v, bgFillColor, alpha, title, titleSize, titleColor, detailTitle, detailTitleSize, detailTitleColor);

        final ImageView spaceshipImage = (ImageView) v.findViewById(RDResourceManager.getInstance().getId("progress_rd_iv"));

        if (imgPaths == null || imgPaths.length == 0) {
            spaceshipImage.setVisibility(View.GONE);
        } else if (imgPaths.length == 1) {
            String path = ResManager.getInstance().getPath(Uri.parse(imgPaths[0]));
            Drawable d = LruCacheUtil.getInstance().getDrawable(path);
            if (d != null) {
                if (Build.VERSION.SDK_INT >= 16) {
                    spaceshipImage.setBackground(d);
                } else {
                    spaceshipImage.setBackgroundDrawable(d);
                }
            } else {
                spaceshipImage.setVisibility(View.GONE);
            }
        } else {
            final AnimationDrawable frameAnim = new AnimationDrawable();
            for (String img : imgPaths) {
                String path = ResManager.getInstance().getPath(Uri.parse(img));
                Drawable d = LruCacheUtil.getInstance().getDrawable(path);
                if (d != null) {
                    frameAnim.addFrame(d, 150);
                }
            }

            if (frameAnim.getNumberOfFrames() > 0) {
                frameAnim.setOneShot(false);
                if (Build.VERSION.SDK_INT >= 16) {
                    spaceshipImage.setBackground(frameAnim);
                } else {
                    spaceshipImage.setBackgroundDrawable(frameAnim);
                }
            }
            // dialog显示时，ImageView显示动画
            progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (frameAnim != null && !frameAnim.isRunning()) {
                        frameAnim.start();
                    }
                }
            });
            // dialog消失时，ImageView停止动画
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (frameAnim != null && !frameAnim.isRunning()) {
                        frameAnim.stop();
                    }
                }
            });
        }
        progressDialog.setContentView(v);
        return progressDialog;
    }

    private static void setProgressPublicData(View v, String bgFillColor, int alpha, String title, String titleSize, String titleColor, String detailTitle, String detailTitleSize, String detailTitleColor) {

        // 设置dialog的背景
        GradientDrawable gd = new GradientDrawable();
        int strokeWidth = 0; // 0边框
        int roundRadius = 8; // 圆角半径
        //int strokeColor = Color.parseColor("#2E3135");//边框颜色
        if(TextUtils.isEmpty(bgFillColor)){
            bgFillColor = "#000000";
        }
        int fillColor = Color.parseColor(bgFillColor);//内部填充颜色
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, RDResourceManager.getInstance().getColorId("white"));
        gd.setAlpha(alpha);
        v.setBackgroundDrawable(gd);

        // title文字
        TextView titleTv = (TextView) v.findViewById(RDResourceManager.getInstance().getId("titleTv"));
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
        }
        if (!TextUtils.isEmpty(titleSize)) {
            titleTv.setTextSize(Float.parseFloat(titleSize));
        }
        if (!TextUtils.isEmpty(titleColor)) {
            titleTv.setTextColor(Color.parseColor(titleColor));
        }

        // detailTitle文字
        TextView detailTitleTv = (TextView) v.findViewById(RDResourceManager.getInstance().getId("detailTitleTv"));
        if (!TextUtils.isEmpty(detailTitle)) {
            detailTitleTv.setText(detailTitle);
        }
        if (!TextUtils.isEmpty(detailTitleSize)) {
            detailTitleTv.setTextSize(Float.parseFloat(detailTitleSize));
        }
        if (!TextUtils.isEmpty(detailTitleColor)) {
            detailTitleTv.setTextColor(Color.parseColor(detailTitleColor));
        }


    }


}
