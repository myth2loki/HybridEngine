package com.xhrd.mobile.hybrid.framework.manager.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.xhrd.mobile.hybrid.annotation.JavascriptConfig;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.manager.ResManagerFactory;
import com.xhrd.mobile.hybrid.framework.PluginData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Camera模块管理设备的摄像头，可用于拍照操作
 * */

@JavascriptConfig(domain = "camera", scope = PluginData.Scope.App)
public class Camera extends PluginBase {
    private static final int CAPTURE_IMAGE = 50000;

    private String mImageSucFunc, mImageErrFunc;
    private File mCapFile;

    @JavascriptFunction(name = "captureImage", permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void captureImage(HybridView window, String[] params) {
        mImageSucFunc = params[0];
        mImageErrFunc = params[1];
        try {
            if (params.length == 3){
                String camOptsStr = params[2];
                JSONObject optsJO = new JSONObject(camOptsStr);
                String filename = optsJO.optString("filename", "");
                filename = ResManagerFactory.getResManager().getPath(filename);
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
        }
    }

}
