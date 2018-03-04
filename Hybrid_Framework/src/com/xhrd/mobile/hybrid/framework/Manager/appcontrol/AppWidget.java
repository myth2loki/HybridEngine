package com.xhrd.mobile.hybrid.framework.Manager.appcontrol;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.RDCloudView;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.JSFunction;
import com.xhrd.mobile.hybrid.framework.Manager.ResManagerFactory;
import com.xhrd.mobile.hybrid.framework.Manager.appcontrol.info.AppAuthorInfo;
import com.xhrd.mobile.hybrid.framework.Manager.appcontrol.info.AppInfo;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.RDApplicationInfo;
import com.xhrd.mobile.hybrid.util.JSObjConvertor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * rd.App
 */
public class AppWidget extends PluginBase {

    private static final int REQUEST_CODE_FOR_OPEN_APP = 1;

    private Map<RDCloudView, String> mCallbackMap = new HashMap<RDCloudView, String>();
    private String appPath = "";

    public static final int STATUS_SUCESS = 1;
    public static final int STATUS_FAILED = 0;

    @Override
    public void addMethodProp(PluginData data) {

        data.addProperty("STATUS_SUCESS",STATUS_SUCESS);
        data.addProperty("STATUS_FAILED",STATUS_FAILED);

        data.addProperty("platformName", "Android");//系统名称
        data.addProperty("platformVersion", Build.VERSION.RELEASE);//系统版本
        data.addProperty("deviceModel", Build.MODEL);//设备模型名称
        data.addProperty("deviceName", Build.DEVICE);//设备名称

        data.addMethodWithReturn("getEngineVersion");// 引擎

//        data.addMethod("installApp", new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, new String[]{});
        data.addMethod("installApp");
        data.addMethod("openApp");
        data.addMethod("isAppInstalled");
        data.addMethodWithConvertReturn("isFullScreen");//是否全屏
//        data.addMethod("setStatusBarStyle");//设置状态栏背景色，Android不支持
        data.addMethod("setStatusBarBackgroundColor");//设置状态栏背景色
        data.addMethod("cleanCache");//清楚缓存
        data.addMethodWithConvertReturn("getApplicationInfo");

        data.addMethod("downloadFile");//系统浏览器下载文件
        data.addMethod("exit");//退出
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.App;
    }

    @Override
    public String getDefaultDomain() {
        return "App";
    }

    @JavascriptFunction
    public String getEngineVersion(){
        try {
            ApplicationInfo ai = HybridActivity.getInstance().getPackageManager().getApplicationInfo(HybridActivity.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData.getString("ENGINE_VERSION");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("getMetaDataBundle", e.getMessage(), e);
        }
        return "";
    }

    @JavascriptFunction
    public void installApp(RDCloudView rdCloudView, String[] params) {
        if(params != null && params.length > 0){
            appPath = ResManagerFactory.getResManager().getPath(params[0]);
            File apkfile = new File(appPath);
            if (!apkfile.exists()) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            rdCloudView.getContext().startActivity(intent);
        }
    }

    @JavascriptFunction
    public void openApp(RDCloudView rdCloudView, String[] params) {
        if (params.length > 1) {
            try {
                JSONObject jo = new JSONObject(params[0]);
                JSONArray paramsArr = null;
                if(!jo.isNull("params")){
                    paramsArr = jo.optJSONArray("params");
                }

                String action = null;
                if (!jo.isNull("action")){
                    action = jo.getString("action");
                }
                String mime = null;
                if (!jo.isNull("mime")){
                    mime = jo.getString("mime");
                }
                String uri = null;
                if (!jo.isNull("uri")){
                    uri = jo.getString("uri");
                }

                Intent itt = new Intent();

                if (action != null){
                    itt.setAction(action);
                }
                if (mime != null) {
                    itt.setType(mime);
                }
                if (uri != null) {
                    itt.setData(Uri.parse(uri));
                }
                if (paramsArr != null) {
                    for (int i = 0; i < paramsArr.length(); i++) {
                        JSONObject o = paramsArr.getJSONObject(i);
                        itt.putExtra(o.getString("key"), o.getString("value"));
                    }
                }
                //put callback to map
                if (params[1] != null) {
                    mCallbackMap.put(rdCloudView, params[1]);
                }
                // 启动第三方app
                startActivityForResult(itt, REQUEST_CODE_FOR_OPEN_APP);
            } catch (JSONException e) {
                Log.e(getClass().getSimpleName(), "json is invalid", e);
                if (params[1] != null) {
                    jsCallback(rdCloudView, true, params[1], -1);
                }else {
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_OPEN_APP) {
            if (resultCode == Activity.RESULT_OK) {
                String result = getResultJson(data);
                for (Map.Entry<RDCloudView, String> entry : mCallbackMap.entrySet()) {
                    JSFunction func = new JSFunction(entry.getValue(), true);
                    func.addParamNumber(STATUS_SUCESS);
                    func.addParamObject(result);
                    jsCallback(entry.getKey(), func);
                }
            } else {
                for (Map.Entry<RDCloudView, String> entry : mCallbackMap.entrySet()) {
                    jsCallback(entry.getKey(), true, entry.getValue(), STATUS_FAILED + "");
                }
            }
            mCallbackMap.clear();
        }
    }

    private String getResultJson(Intent intent){
        StringBuffer sb = new StringBuffer("{");
        sb.append("data").append(":");
        if (intent.getDataString() != null){
            sb.append("'").append(intent.getDataString()).append("',");
        }else {
            sb.append("null,");
        }

        sb.append("extras:{");
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            Set<String> keys = bundle.keySet();
            for (String key : keys){
                Object value = bundle.get(key);
                try {
                    if (value != null && isBaseDataType(value.getClass())) {
                        sb.append(key).append(":").append("'").append(value).append("',");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}}");
        return sb.toString();
    }
    /**
     * 判断一个类是否为基本数据类型。
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
    private static boolean isBaseDataType(Class clazz) throws Exception {
        return (
                clazz.equals(String.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Boolean.class) ||
                clazz.isPrimitive()
        );
    }

//                                clazz.equals(BigDecimal.class) ||
//                                clazz.equals(BigInteger.class) ||
//                                clazz.equals(Date.class) ||
//                                clazz.equals(DateTime.class) ||
    @JavascriptFunction
    public void isAppInstalled(final RDCloudView rdCloudView, String[] params) {
        if (params.length >= 2) {
            if (TextUtils.isEmpty(params[0])) {
                return;
            }
            if (TextUtils.isEmpty(params[1])) {
                return;
            }
            final String pkg = params[0];
            final String cb = params[1];
            new Thread("isAppInstalled_thread") {
                public void run() {
                    List<PackageInfo> infoList = HybridEnv.getApplicationContext().getPackageManager().getInstalledPackages(0);
                    for (PackageInfo info : infoList) {
                        if (info.packageName.equals(pkg)) {
                            jsCallback(rdCloudView, true, cb, true);
                            return;
                        }
                    }
                    jsCallback(rdCloudView, true, cb, false);
                }
            }.start();
        }
    }

    @JavascriptFunction
    public String getPlatformVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    @JavascriptFunction
    public boolean isFullScreen() {
        int isFullScreen = HybridActivity.getInstance().getWindow().getAttributes().flags;
        return (isFullScreen & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    @JavascriptFunction
    public void setStatusBarBackgroundColor(String[] params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(HybridActivity.getInstance());
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.parseColor(params[0]));
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        android.view.Window win = HybridActivity.getInstance().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @JavascriptFunction
    public String getApplicationInfo() {
        RDApplicationInfo rdAppInfo = HybridActivity.getInstance().getRDCloudAppInfo();
        AppAuthorInfo author = new AppAuthorInfo(rdAppInfo.author.name, rdAppInfo.author.email, rdAppInfo.author.tel, rdAppInfo.author.address);
        AppInfo appInfo = new AppInfo(rdAppInfo.id, rdAppInfo.name, rdAppInfo.description, author, rdAppInfo.version, rdAppInfo.bgcolor, rdAppInfo.entry, rdAppInfo.appkey);
        return JSObjConvertor.convertJS(appInfo);
    }

    @JavascriptFunction
    public void downloadFile(String[] params) {
        if (params.length > 0) {
            Uri uri = Uri.parse(params[0]);
            Intent itt = new Intent(Intent.ACTION_VIEW, uri);
            itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            HybridEnv.getApplicationContext().startActivity(itt);
        }
    }

    @JavascriptFunction
    public  void exit() {
        HybridActivity.getInstance().exit();
    }

    @JavascriptFunction
    public void cleanCache() {
            String cachePath =  ResManagerFactory.getResManager().getPath("cache://");
            File file = new File(cachePath);
            deleteAllFiles(file);
    }
    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

}
