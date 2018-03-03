package com.xhrd.mobile.hybrid.framework.Manager.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.RDResourceManager;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.Manager.ResManagerFactory;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.RDCloudApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Camera模块管理设备的摄像头，可用于拍照、摄像操作，通过RD.framework.get("CameraManager")获取摄像头管理对象。
 * Created by wangqianyu on 15/4/22.
 */
public class camera extends PluginBase {
    public static final int CAPTURE_IMAGE = RDCloudApplication.getAtomicId();
    public static final int CAPTURE_VIDEO = RDCloudApplication.getAtomicId();

    private static final String CAMERA_JSON = "{" +
                "captureImage:function(){" +
                    "window.CameraManager_.call('captureImage', %d, ja2sa(params))" +
                "}," +
                "captureVideo:function(){" +
                    "window.CameraManager_.call('startVideoCapture', %d, ja2sa(params))" +
                "}" +
            "}";

    private String mImageSucFunc, mImageErrFunc;
    private String mVideoSucFunc, mVideoErrFunc;
    private File mCapFile;

    @JavascriptFunction
    public void captureImage(String windowName, String[] params) {
        mImageSucFunc = params[0];
        mImageErrFunc = params[1];
        try {
            if (params.length == 3){
                String camOptsStr = params[2];
                JSONObject optsJO = new JSONObject(camOptsStr);
                String filename = optsJO.optString("filename", "");
                filename = ResManagerFactory.getResManager().getPath(getTargetView(windowName),filename);
                File file = new File(filename);
                mCapFile = file;
                if (file.isDirectory()) {
                    File dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    mCapFile = new File(dcimFolder, new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + ".jpg");
                }
            } else if (params.length == 2){
                File dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                mCapFile = new File(dcimFolder, new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + ".jpg");
            }
        } catch (JSONException e) {
            jsCallback(mImageErrFunc, e.getMessage());
        }
        Intent itt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        itt.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCapFile));
        startActivityForResult(itt, CAPTURE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK && mCapFile != null && mCapFile.exists()) {
                jsCallback(mImageSucFunc, mCapFile.getAbsolutePath());
            } else {
                jsCallback(mImageErrFunc, "");
            }
        } else if (requestCode == CAPTURE_VIDEO) {
            if (resultCode == Activity.RESULT_OK && mCapFile != null && mCapFile.exists()) {
                jsCallback(mVideoSucFunc, mCapFile.getAbsolutePath());
            } else {
                jsCallback(mVideoErrFunc, "capture video failed.");
            }
        }
    }

    @JavascriptFunction
    public void captureVideo(String windowName, String[] params) {
        mVideoSucFunc = params[0];
        mVideoErrFunc = params[1];
        try {
            String camOptsStr = params[2];
            JSONObject optsJO = new JSONObject(camOptsStr);
            String filename = optsJO.optString("filename", "");
            filename = ResManagerFactory.getResManager().getPath(this.getTargetView(windowName),filename);
            File file = new File(filename);
            mCapFile = file;
            if (file.isDirectory()) {
                File dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                mCapFile = new File(dcimFolder, new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + ".mp4");
            }
        } catch (JSONException e) {
            jsCallback(mVideoErrFunc, e.getMessage());
        }
        Intent itt = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        itt.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCapFile));
        startActivityForResult(itt, CAPTURE_VIDEO);
    }

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("captureImage",new String[]{Manifest.permission.CAMERA}, new String[]{RDResourceManager.getInstance().getString("request_camera_permission_msg")});
        data.addMethod("captureVideo");
    }
    
    @Override
    public PluginData.Scope getScope() {
    	return PluginData.Scope.app;
    }

}
