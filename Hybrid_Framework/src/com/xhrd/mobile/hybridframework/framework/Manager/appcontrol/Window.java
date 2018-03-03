package com.xhrd.mobile.hybridframework.framework.Manager.appcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.engine.HybridActivity;
import com.xhrd.mobile.hybridframework.engine.IAppCallback;
import com.xhrd.mobile.hybridframework.engine.PullToRefreshWebView;
import com.xhrd.mobile.hybridframework.engine.RDCloudView;
import com.xhrd.mobile.hybridframework.engine.RDCloudWindowInfo;
import com.xhrd.mobile.hybridframework.framework.PluginBase;
import com.xhrd.mobile.hybridframework.framework.PluginData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Window extends PluginBase implements MyCallInterface, IAppCallback {

    private static final String CALLBACK_ON_LOAD = "onLoad";
    Dialog showDialog;
    String ok_method;
    String cancle_method;

    private int mResolutionHeight;
    private int mResolutionWidth;

    Map<String, Integer> screenOrientation = new HashMap<String, Integer>();

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("openWindow");
        //data.addMethod("openWindowByComponent");
        data.addMethod("closeWindow");
        data.addMethod("closeSelf");
        data.addMethod("alert");
        data.addMethod("confirm");
        data.addMethod("prompt");
        data.addMethod("evaluateScript");
        data.addMethod("toast");
        data.addMethod("setWindowVisible");
        data.addMethod("backToWindow");
        data.addMethod("lockRotate");
        data.addMethod("setAttr");
        data.addMethod("addHeaderRefreshing");
        data.addMethod("addFooterRefreshing");
        data.addMethod("removeHeaderRefreshing");
        data.addMethod("removeFooterRefreshing");
        data.addMethod("endFooterRefresh");
        data.addMethod("endHeaderRefresh");
        data.addMethodWithConvertReturn("getWidth");
        data.addMethodWithConvertReturn("getHeight");


        data.addMethod("statusBarStyle");
        data.addMethod("downloadFile");



        data.addProperty("WEBVIEW_LOCAL_RELATIVE_PATH", "0");//加载本地html
        data.addProperty("WEBVIEW_LOCAL_STRING", "1");//加载html字符串
        data.addProperty("WEBVIEW_REMOTE_URL", "2");//加载url
        data.addProperty("ANIMATION_TYPE_FADE", "10");//淡入淡出
        data.addProperty("ANIMATION_TYPE_PUSH", "11");//推入
        data.addProperty("ANIMATION_TYPE_REVEAL", "12");//显露
        data.addProperty("ANIMATION_TYPE_MOVE_IN", "13");//切入
        data.addProperty("ANIMATION_TYPE_CUBE", "14");//立方体翻转
        data.addProperty("ANIMATION_TYPE_PAGE_CURL", "15");//上翻页
        data.addProperty("ANIMATION_TYPE_PAGE_UNCURL","16");//下翻页
        data.addProperty("ANIMATION_TYPE_SUCK_EFFECT","17");//收缩
        data.addProperty("ANIMATION_TYPE_RIPPLE_EFFECT","18");//水滴
        data.addProperty("ANIMATION_TYPE_FLIP","19");//翻转
        data.addProperty("ANIMATION_TYPE_ROTATE","20");//旋转
        data.addProperty("ANIMATION_TYPE_CAMERA_IRISHOLLOW_OPEN","21");//照相机打开
        data.addProperty("ANIMATION_TYPE_CAMERA_IRISHOLLOW_CLOSE","22");//照相机关闭
        data.addProperty("ANIMATION_SUBTYPE_FROM_LEFT","40");//从左往右
        data.addProperty("ANIMATION_SUBTYPE_FROM_RIGHT","41");//从右往左
        data.addProperty("ANIMATION_SUBTYPE_FROM_TOP","42");//从上往下
        data.addProperty("ANIMATION_SUBTYPE_FROM_BOTTOM","43");//从下往上
        data.addProperty("ANIMATION_CURVE_LINEAR","50");//线性
        data.addProperty("ANIMATION_CURVE_EASEIN","51");//从慢到快
        data.addProperty("ANIMATION_CURVE_EASEOUT", "52");//从快到慢
        data.addProperty("ANIMATION_CURVE_EASEINEASEOUT", "53");//从慢到快到慢
    }
    
    private String getJsonString(JSONObject pJson,String pKey) {
        return pJson.has(pKey)?pJson.optString(pKey,""):null;
    }
    
    /**
     * 在当前componen中打开窗口
     *
     * @param rdView
     * @param params
     */
    @JavascriptFunction
    public void openWindow(RDCloudView rdView, String[] params) {
        RDCloudWindowInfo ri = new RDCloudWindowInfo();
        ri.windowName = params[0];
        if (TextUtils.isEmpty(ri.windowName)) {
            return;
        }
        ri.type = Integer.valueOf(params[1]);
        ri.data = params[2];
        if (params.length > 3) {
            //TODO 动画 {direct:xx, time:300, curve:xx}
        }
        HybridActivity.getInstance().openWindow(ri.windowName, ri.type, ri.data);
    }

    /**
     * @param rdView
     * @param params
     */
    @JavascriptFunction
    public void closeWindow(RDCloudView rdView, String[] params) {
        if (params.length > 1) {
            //TODO 动画 {direct:xx, time:300, curve:xx}
        }
        if (rdView == null) {
            return;
        }
        HybridActivity.getInstance().closeWindow(rdView.getName());
    }

    /**
     * @param rdView
     * @param params
     */
    @JavascriptFunction
    public void closeSelf(RDCloudView rdView, String[] params) {
        String windowName = rdView.getWindowName();
        closeWindow(rdView, new String[]{windowName});
    }

    @JavascriptFunction
    public void back(RDCloudView rdView, String[] params) {
        HybridActivity.getInstance().back();

    }

    /**
     * 设定窗口大小
     *
     * @param rdView
     * @param params
     */
    @JavascriptFunction
    public void setWindowFrame(RDCloudView rdView, String[] params) {
        int x = Integer.parseInt(params[2]);
        int y = Integer.parseInt(params[3]);
        int width = Integer.parseInt(params[4]);
        int height = Integer.parseInt(params[5]);
        rdView.getRDCloudWindow().setWindowFrame(x, y, width, height);
    }

    @JavascriptFunction
    public void showSoftKeyboard(RDCloudView rdView, String[] params) {
        HybridActivity.getInstance().showSoftKeyboard();
    }

    @JavascriptFunction
    public void alert(RDCloudView rdView, String[] params) {

        Dialog confirmDialog = new ShowDialog().createDialog(rdView, params, ShowDialog.F_ALERT, this);
        confirmDialog.show();
    }

    @JavascriptFunction
    public void confirm(RDCloudView rdView, String[] params) {
        Dialog confirmDialog = new ShowDialog().createDialog(rdView, params, ShowDialog.F_CONFIRM, this);
        confirmDialog.show();

    }

    /**
     * 带输入文本的弹框提醒
     * @param rdView
     * @param params params[0] json对象  {
                                            title:"标题",
                                            msg:"内容",
                                            text:"文本内容",
                                            type:"text",// 文本类型 如text、password
                                            buttons:{"确认","取消"}//显示的是按钮
                                            }
                     params[1] 回调函数
     */
    @JavascriptFunction
    public void prompt(RDCloudView rdView, String[] params) {
        Dialog promptDialog = new ShowDialog().createDialog(rdView, params, ShowDialog.F_PROMPT, this);
        promptDialog.show();
    }

    private String getWindowName(RDCloudView rdCloudView){
        return rdCloudView.getWindowName();
    }

    @JavascriptFunction
    public void toast(RDCloudView rdView, String[] params) {
        Toast.makeText(HybridActivity.getInstance().getApplication(), params[0], Toast.LENGTH_SHORT).show();

    }

    /**
     * 改变状态栏的样式，目前android不做处理
     * @param rdView
     * @param params
     */
    @JavascriptFunction
    public void statusBarStyle(RDCloudView rdView, String[] params){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && params.length > 0) {
            String cStr = params[0];
            int color;
            if (cStr.startsWith("#")) {
                color = Color.parseColor(cStr);
            } else {
                color = Integer.parseInt(cStr);
                switch(color) {
                    case 0:
                        color = Color.BLACK;
                        break;
                    case 1:
                        color = Color.WHITE;
                        break;
                }
            }
            HybridActivity.getInstance().getWindow().setStatusBarColor(color);
        }
    }

    @JavascriptFunction
    public void downloadFile(RDCloudView rdView, String[] params){
        String url = params[0];
        rdView.loadUrl(url);
    }

    @Override
    public void ok() {
        jsCallback(ok_method, "ok");
        showDialog.dismiss();

    }

    @Override
    public void cancle() {
        jsCallback(cancle_method, "cancle");
        showDialog.dismiss();


    }


    @Override
    public void onBackground() {

    }

    @Override
    public void onForeground() {

    }

    @Override
    public void onLoad() {
        System.out.println("=======RDWindow onLoad========>");
        jsCallback(CALLBACK_ON_LOAD);
    }

    /**
     * @param rdView
     * @param params
     */
    @JavascriptFunction
    public void backToWindow(RDCloudView rdView, String[] params) {
        String newWinName = params[0];
        HybridActivity.getInstance().backToWindow(newWinName);
    }

    @JavascriptFunction
    public void setWindowVisible(RDCloudView rdView, String[] params) {
        int visible = -1;
        try {
            visible = Integer.parseInt(params[0]);
            String winName = params[1];
            if (TextUtils.isEmpty(winName)) {
                winName = rdView.isPopover() ? rdView.getPopName() : rdView.getWindowName();
            }
            HybridActivity.getInstance().setWindowVisible(winName, visible == 1);
        } catch (NumberFormatException e) {
            Log.w(getClass().getSimpleName(), "params is invalid, param: " + params[0]);
        }
    }

    @JavascriptFunction
    public void lockRotate(RDCloudView rdView, String[] params) {
        boolean lock = Boolean.parseBoolean(params[0]);
        Activity act = HybridActivity.getInstance();

        if (lock) {
            if(isPad())
            {
                if(mResolutionWidth>mResolutionHeight)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    }else {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }else {
                    act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }else {
                if(mResolutionWidth>mResolutionHeight)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    }else {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }else {
                    act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        } else {
//            recoverScreenOrientation(windowName);
            if (act != null) {
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
    }



    @Override
    public void onDeregistered(RDCloudView view) {
        super.onDeregistered(view);
        //recoverScreenOrientation(view.getWindowName());
    }


    /**
         bgColor: (String 类型 ) 背景颜色，如果字段为空，颜色为透明
         bgAlpha: (String 类型 ) 背景颜色，如果字段为空，颜色为透明 api level 11
         vScrollBarEnabled: (Number 类型 ) 是否显示水平滚动条
         hScrollBarEnabled: (Number 类型 ) 是否显示垂直滚动条
     * @param rdView
     * @param params
     */
    @SuppressLint("NewApi")
	@JavascriptFunction
    public void setAttr(RDCloudView rdView, String[] params) {
        try {
            JSONObject jo = new JSONObject(params[0]);
            String bgColorStr = jo.optString("bgColor");
            if (!TextUtils.isEmpty(bgColorStr)) {
                int bgColor = Color.parseColor(bgColorStr);
                rdView.getRefresableParent().setBackgroundColor(bgColor);
            }
            int vScrollBarEnabled = jo.optInt("vScrollBarEnabled", -1);
            if (vScrollBarEnabled > -1) {
                rdView.setVerticalScrollBarEnabled(vScrollBarEnabled == 1);
            }
            int hScrollBarEnabled = jo.optInt("hScrollBarEnabled", -1);
            if (hScrollBarEnabled > -1) {
                rdView.setHorizontalFadingEdgeEnabled(hScrollBarEnabled == 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rdView
     * @param params
     * 添加头刷新事件
     */
    @JavascriptFunction
    public void addHeaderRefreshing(RDCloudView rdView, String[] params) {
        String function = params[0];//js方法
        PullToRefreshWebView refreshWebView = rdView.getRefresableParent();
        refreshWebView.setPullRefreshEnabled(true);
        if (params.length > 1) {
            String param = params[1];//头部的设置参数
            refreshWebView.setHeadParams(param);
        }
        refreshWebView.getRefreshableView().setOnPullDownCallBack(function);
    }
    /**
     * @param rdView
     * @param params
     * 添加底部刷新事件
     */
    @JavascriptFunction
    public void addFooterRefreshing(RDCloudView rdView, String[] params) {
        String funciton = params[0];//js方法
        PullToRefreshWebView refreshWebView = rdView.getRefresableParent();
        refreshWebView.setPullLoadEnabled(true);
        if (params.length > 1) {
            String param = params[1];//底部的设置参数
            refreshWebView.setFootParams(param);
        }
        refreshWebView.getRefreshableView().setOnPullUpCallBack(funciton);
    }
    /**
     * @param rdView
     * @param params
     * 移除头部刷新事件
     */
    @JavascriptFunction
    public void removeHeaderRefreshing(RDCloudView rdView, String[] params) {
        PullToRefreshWebView refreshWebView = rdView.getRefresableParent();
        refreshWebView.setPullRefreshEnabled(false);
    }

    /**
     * @param rdView
     * @param params
     * 移除底部刷新事件
     */
    @JavascriptFunction
    public void removeFooterRefreshing(RDCloudView rdView, String[] params) {
        PullToRefreshWebView refreshWebView = rdView.getRefresableParent();
        refreshWebView.setPullLoadEnabled(false);
    }

    @JavascriptFunction
    public void endFooterRefresh(RDCloudView rdView, String[] params) {
        PullToRefreshWebView refreshWebView = rdView.getRefresableParent();
        refreshWebView.onPullUpRefreshComplete();
    }

    @JavascriptFunction
    public void endHeaderRefresh(RDCloudView rdView, String[] params) {
        PullToRefreshWebView refreshWebView = rdView.getRefresableParent();
        refreshWebView.onPullDownRefreshComplete();
    }

    @JavascriptFunction
    public int getWidth(RDCloudView rdView, String[] params) {
        return rdView.getWidth();
    }
    @JavascriptFunction
    public int getHeight(RDCloudView rdView, String[] params) {
        return rdView.getHeight();
    }

    @Override
    public String getDefaultDomain() {
        return "window";
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }

    /**
     * 设置屏幕显示方向
     * @param orientation
     */
    private void setActivityScreenOrientation(int orientation) {

        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                break;
            default:
                HybridActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }

    }

    /**
     * 复原方向
     * @param rdView
     */
    private void recoverScreenOrientation(RDCloudView rdView){
        // 获取屏幕方向，并移除key
        Integer orientation = screenOrientation.remove(rdView.isPopover() ? rdView.getPopName() : rdView.getWindowName());
        if(orientation != null){
            setActivityScreenOrientation(orientation);
        }
    }
    /**
     * 判断是否为平板
     *
     * @return
     */
    private boolean isPad() {
        WindowManager wm = (WindowManager) HybridActivity.getInstance().getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        this.mResolutionHeight = dm.heightPixels;
        this.mResolutionWidth = dm.widthPixels;
        double x = Math.pow(dm.widthPixels , 2);
        double y = Math.pow(dm.heightPixels , 2);
        // 屏幕尺寸
        //double screenInches = Math.sqrt(x + y);
        double screenInches = Math.sqrt(x + y) / (160 * dm.density);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.0) {
            return true;
        }
        return false;
    }

}
