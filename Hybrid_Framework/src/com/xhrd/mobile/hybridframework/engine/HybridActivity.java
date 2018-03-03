package com.xhrd.mobile.hybridframework.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xhrd.mobile.hybridframework.BuildConfig;
import com.xhrd.mobile.hybridframework.framework.HybridEnv;
import com.xhrd.mobile.hybridframework.framework.RDApplicationInfo;

import java.util.LinkedList;

/**
 * 入口类, 负责创建最初的可视窗口骨架。
 * Created by wangqianyu on 15/4/10.
 */
public class HybridActivity extends Activity {//FragmentActivity
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = HybridActivity.class.getSimpleName();

    private static final int EXIT_MESSAGE = 0;

    private static HybridActivity mActivity;
    private RDApplicationInfo mAppInfo;
    private Handler mExitHandler = new Handler();

    private LinkedList<RDCloudWindow> mWindowList = new LinkedList<>();
    private FrameLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        mActivity = this;
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 初始化主界面。
     *
     */
    private void initView() {
        mRootLayout = new FrameLayout(this);
        setContentView(mRootLayout);
    }



    /**
     * 打开新窗口
     *
     * @param windowName
     * @param type       0  html url; 1: html text
     * @param data       url or text
     */
    public void openWindow(final String windowName, final int type, final String data) {
        if (DEBUG) {
            Log.e(TAG, "run: window name = " + windowName);
        }

        if (TextUtils.isEmpty(windowName)) {
            return;
        }

        RDCloudWindow window = new RDCloudWindow(this, windowName, type, data);
        if (mWindowList.size() > 0) {
            final RDCloudWindow lastWindow = mWindowList.peekLast();
            lastWindow.onBackground();
            window.startAnimation(AnimationUtils.loadAnimation(window.getContext(), RDResourceManager.getInstance().getAnimId("enter_in")));
            lastWindow.startAnimation(AnimationUtils.loadAnimation(lastWindow.getContext(), RDResourceManager.getInstance().getAnimId("enter_out")));
        }
        mWindowList.add(window);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRootLayout.addView(window, lp);
    }

    /**
     * 获取入口类实例。
     *
     * @return 入口类实例
     */
    public static HybridActivity getInstance() {
        return mActivity;
    }

    /**
     * Take care of calling onBackPressed() for pre-Eclair platforms.
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (needExit()) {
                finish();
            } else {
                back();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back() {
        if (!canBack()) {
            return;
        }
        RDCloudWindow window = mWindowList.peekLast();
        window.back();
    }

    private boolean canBack() {
        return !mWindowList.isEmpty();
    }

    /**
     * 是否能直接退出。
     *
     * @return
     */
    private boolean needExit() {
        return mWindowList.isEmpty();
    }

    @Override
    public void finish() {
        if (mExitHandler.hasMessages(EXIT_MESSAGE)) {
            super.finish();
        } else {
            Toast.makeText(this, getString(RDResourceManager.getInstance().getStringId("press_again_exit")), Toast.LENGTH_LONG).show();
            mExitHandler.sendEmptyMessageDelayed(EXIT_MESSAGE, 3000);
        }
    }

    /**
     * 直接退出app
     */
    public final void exit() {
        super.finish();
    }

    public RDApplicationInfo getRDCloudAppInfo() {
        return mAppInfo;
    }

    public void backToWindow(String windowName) {
        RDCloudWindow window = findWindowByName(windowName);
        if (DEBUG) {
            Log.d(TAG, "backToWindow: window = " + window);
        }
        if (window == null) {
            return;
        }

        while (!mWindowList.isEmpty()) {
            RDCloudWindow tempWindow = mWindowList.pollLast();
            if (tempWindow == window) {
                break;
            }

            mRootLayout.removeView(tempWindow);
        }
    }

    int findWindowIndexByName(String windowName) {
        RDCloudWindow window;
        for (int i = 0; i < mWindowList.size(); i++) {
            window = mWindowList.get(i);
            if (window.getName().equals(windowName)) {
                return i;
            }
        }
        return -1;
    }

    public void closeWindow(String windowName) {
        RDCloudWindow window = findWindowByName(windowName);
        if (window == null) {
            if (DEBUG) {
                Log.d(TAG, "closeWindow: windowName = " + windowName);
            }
            return;
        }

        mRootLayout.removeView(window);
        mWindowList.remove(window);
    }

    RDCloudWindow findWindowByName(String windowName) {
        for (RDCloudWindow window : mWindowList) {
            if (window.getName().equals(windowName)) {
                return window;
            }
        }
        return null;
    }

    public void showSoftKeyboard() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    public void setWindowVisible(String windowName, boolean visible) {
        RDCloudWindow window = findWindowByName(windowName);
        if (window == null) {
            if (DEBUG) {
                Log.d(TAG, "closeWindow: windowName = " + windowName);
            }
            return;
        }

        window.setWindowVisible(visible);
    }

//    @Override
//    public void onConfigurationChanged(final Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        for (RDCloudWindow window : mWindowList) {
//            window.onWindowSizeChanged(newConfig);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mContainer.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void evaluateScript(String compName, String winName, String popName, String js) {
//        mContainer.evaluateScript(compName, winName, popName, js);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        mContainer.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    /**
     * 打开带返回参数的窗口
     *
     * @param intent
     * @param requestCode
     * @param window 窗口实例
     * @param pluginId 插件索引
     */
    public void startActivityForResult(Intent intent, int requestCode, RDCloudWindow window, int pluginId) {
        int windowIndex = findWindowIndexByName(window.getName());
        windowIndex = windowIndex << 24;
        pluginId = pluginId << 16;
        int realRequestCode = windowIndex & pluginId & requestCode;
        mActivity.startActivityForResult(intent, realRequestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        HybridEnv.getPluginManager().onActivityResult(requestCode, resultCode, data);

        int windowIndex = requestCode >> 24;
        int pluginIndex = requestCode << 8 >> 24;
        int realRequestCode = requestCode << 16;
        RDCloudWindow window = mWindowList.get(windowIndex);
        window.onActivityResult(pluginIndex, realRequestCode, resultCode, data);
    }

    //pathCode window|id|requestCode
    public void requestPermissions(String[] permissions, int requestCode, RDCloudWindow window, int pluginId) {
        int windowIndex = findWindowIndexByName(window.getName());
        windowIndex = windowIndex << 24;
        pluginId = pluginId << 16;
        int realRequestCode = windowIndex & pluginId & requestCode;
        ActivityCompat.requestPermissions(mActivity, permissions, realRequestCode);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int windowIndex = requestCode >> 24;
        int pluginId = requestCode << 8 >> 24;
        int realRequestCode = requestCode << 16;
        RDCloudWindow window = mWindowList.get(windowIndex);
        window.onRequestPermissionsResult(permissions, grantResults, realRequestCode, pluginId);
    }
}
