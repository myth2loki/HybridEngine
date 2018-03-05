package com.xhrd.mobile.hybrid.engine;

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

import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.RDApplicationInfo;
import com.xhrd.mobile.hybridframework.BuildConfig;

import java.util.LinkedList;

/**
 * 入口类, 负责创建最初的可视窗口骨架。
 */
public class HybridActivity extends Activity {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = HybridActivity.class.getSimpleName();

    private static final int EXIT_MESSAGE = 0;

    private static HybridActivity mActivity;
    private RDApplicationInfo mAppInfo;
    private Handler mExitHandler = new Handler();

    private LinkedList<HybridWindow> mWindowList = new LinkedList<>();
    private FrameLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        mActivity = this;
        initView();

        openWindow("internal_window", HybridView.DATA_TYPE_ASSET, "internal_plugin.html");
//        openWindow("index", RDCloudView.DATA_TYPE_ASSET, "index.html");
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
            Log.e(TAG, "run: Window name = " + windowName);
        }

        if (TextUtils.isEmpty(windowName)) {
            return;
        }

        HybridWindow window = new HybridWindow(this, windowName, type, data);
        if (mWindowList.size() > 0) {
            final HybridWindow lastWindow = mWindowList.peekLast();
            lastWindow.onBackground();
            window.startAnimation(AnimationUtils.loadAnimation(window.getContext(), HybridResourceManager.getInstance().getAnimId("enter_in")));
            lastWindow.startAnimation(AnimationUtils.loadAnimation(lastWindow.getContext(), HybridResourceManager.getInstance().getAnimId("enter_out")));
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
        HybridWindow window = mWindowList.peekLast();
        window.back();
    }

    private boolean canBack() {
        return mWindowList.size() > 0;
    }

    /**
     * 是否能直接退出。
     *
     * @return
     */
    private boolean needExit() {
        return mWindowList.size() == 1;
    }

    @Override
    public void finish() {
//        if (mExitHandler.hasMessages(EXIT_MESSAGE)) {
            super.finish();
//        } else {
//            Toast.makeText(this, getString(RDResourceManager.getInstance().getStringId("press_again_exit")), Toast.LENGTH_LONG).show();
//            mExitHandler.sendEmptyMessageDelayed(EXIT_MESSAGE, 3000);
//        }
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
        if (mWindowList.isEmpty()) {
            if (DEBUG) {
                Log.d(TAG, "backToWindow: windowList is empty");
            }
            return;
        }
        HybridWindow window = findWindowByName(windowName);
        if (DEBUG) {
            Log.d(TAG, "backToWindow: Window = " + window);
        }
        if (window == null) {
            return;
        }
        HybridWindow curWindow = mWindowList.peekLast();
        if (window == curWindow) {
            if (DEBUG) {
                Log.d(TAG, "backToWindow: ignore the same window");
            }
        }

        window.startAnimation(AnimationUtils.loadAnimation(this, HybridResourceManager.getInstance().getAnimId("exit_in")));
        curWindow.startAnimation(AnimationUtils.loadAnimation(this, HybridResourceManager.getInstance().getAnimId("exit_out")));

        while (!mWindowList.isEmpty()) {
            HybridWindow tempWindow = mWindowList.peekLast();
            if (tempWindow == window) {
                break;
            }
//            if (DEBUG) {
                Log.d(TAG, "backToWindow: windowName = " + tempWindow.getName());
//            }
            mWindowList.remove(tempWindow);
            mRootLayout.removeView(tempWindow);
        }

    }

    int findWindowIndexByName(String windowName) {
        HybridWindow window;
        for (int i = 0; i < mWindowList.size(); i++) {
            window = mWindowList.get(i);
            if (window.getName().equals(windowName)) {
                return i;
            }
        }
        return -1;
    }

    public void closeWindow(String windowName) {
        HybridWindow window = findWindowByName(windowName);
        if (window == null) {
            if (DEBUG) {
                Log.d(TAG, "closeWindow: windowName = " + windowName);
            }
            return;
        }

        int curIndex = mWindowList.indexOf(window);
        if (curIndex > 0) {
            int prevIndex = curIndex - 1;
            HybridWindow prevWindow = mWindowList.get(prevIndex);
            prevWindow.startAnimation(AnimationUtils.loadAnimation(this, HybridResourceManager.getInstance().getAnimId("exit_in")));
        }
        window.startAnimation(AnimationUtils.loadAnimation(this, HybridResourceManager.getInstance().getAnimId("exit_out")));

        mRootLayout.removeView(window);
        mWindowList.remove(window);
    }

    HybridWindow findWindowByName(String windowName) {
        for (HybridWindow window : mWindowList) {
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
        HybridWindow window = findWindowByName(windowName);
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
//        for (RDCloudWindow Window : mWindowList) {
//            Window.onWindowSizeChanged(newConfig);
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
    public void startActivityForResult(Intent intent, int requestCode, HybridWindow window, int pluginId) {
        int windowIndex = findWindowIndexByName(window.getName());
        windowIndex = windowIndex << 24;
        pluginId = pluginId << 16;
        int realRequestCode = windowIndex | pluginId | requestCode;
        mActivity.startActivityForResult(intent, realRequestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int windowIndex = requestCode >> 24;
        int pluginIndex = requestCode << 8 >> 24;
        int realRequestCode = requestCode & 0x000000FF;
        if (DEBUG) {
            Log.d(TAG, "onActivityResult: windowIndex = " + windowIndex);
            Log.d(TAG, "onActivityResult: pluginIndex = " + pluginIndex);
            Log.d(TAG, "onActivityResult: realRequestCode = " + realRequestCode);
        }
        HybridWindow window = mWindowList.get(windowIndex);
        window.onActivityResult(pluginIndex, realRequestCode, resultCode, data);
    }

    //pathCode Window|id|requestCode
    public void requestPermissions(String[] permissions, int requestCode, HybridWindow window, int pluginId) {
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
        HybridWindow window = mWindowList.get(windowIndex);
        window.onRequestPermissionsResult(permissions, grantResults, realRequestCode, pluginId);
    }
}
