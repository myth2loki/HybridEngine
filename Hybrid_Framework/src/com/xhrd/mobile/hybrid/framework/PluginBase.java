package com.xhrd.mobile.hybrid.framework;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ValueCallback;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.engine.HybridScript;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.engine.HybridWindow;
import com.xhrd.mobile.hybrid.engine.HybridResourceManager;
import com.xhrd.mobile.hybrid.util.SystemUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 供内部framework插件使用。
 * Created by wangqianyu on 15/4/13.
 */
public abstract class PluginBase {
    public static final String EXCEPTION_JSON = "new Error(%s)";
    private static final AtomicInteger mInteger = new AtomicInteger();

    private List<HybridView> mRDViewList = new ArrayList<HybridView>();
    private Handler mHandler;
    private PluginData mPluginData;
    private int mId = mInteger.getAndIncrement();

    private Map<Integer, WeakReference<HybridView>> mPermRDCloudViewMap = new HashMap<>();

    public PluginBase() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取默认域名，FrameworkManager专用，复写用于设置域名。
     * @return
     */
    public String getDefaultDomain() {
        return getClass().getSimpleName();
    }

    public PluginData getPluginData() {
        return mPluginData;
    }

    public void setPluginData(PluginData pluginData) {
        this.mPluginData = pluginData;
    }

    /**
     * js方法的参数长度错误
     * @param view
     */
    public void jsErrCallbackParamsLengthError(HybridView view){
        jsErrCallback(view, EXCEPTION_JSON, view.getContext().getString(HybridResourceManager.getInstance().getStringId("params_length_error")));
    }

    /**
     *
     * @param func
     * @return 0: func, 1: key, 2: subKey
     */
    public static List<String> getKeyAndFunc(String func) {
        String[] temp = func.split("_");
        String key = null;
        String subKey = null;
        String f = null;
        if (temp.length > 0) {
            f = temp[0];
        }
        if (temp.length > 1) {
            key = temp[1];
        }
        if (temp.length > 2) {
            subKey = temp[2];
        }
        return Arrays.asList(f, key, subKey);
    }

    public static void addRemoveAfterCall(StringBuffer js, List<String> keyAndFunc) {
        js.append("RD.internal.nativeCallbacks.removeOne(").append(keyAndFunc.get(1)).append(")");
    }

    /**
     * 在当前RDCloudView回调JS脚本。
     * @param view RDCloudView
     * @param remove 执行后，是否删除(注意此参数)
     * @param func jsFunction
     * @param content 参数
     */
    public void jsCallback(final HybridView view, boolean remove, String func, Object content) {
        List<String> keyAndFunc = getKeyAndFunc(func);
        final StringBuffer sb = new StringBuffer("javascript:var f =").append(keyAndFunc.get(0)).append("; f(");
        if (content.getClass().isAssignableFrom(String.class)) {
            sb.append("'").append(content).append("'");
        } else {
            sb.append(content);
        }
        sb.append(");");
        if (remove) {
            addRemoveAfterCall(sb, keyAndFunc);
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(sb.toString());
            }
        });
    }

    /**
     * 在当前RDCloudView回调JS结果。
     * @param view RDCloudView
     * @param result JS结果
     */
    public void jsCallback(final HybridView view, final JSFuncResult result) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(result.getPreScript())) {
                    if (SystemUtil.isKitKat()) {
                        view.evaluateJavascript(result.getPreScript(), new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                resultBack(view, result);
                            }
                        });
                    } else {
                        view.loadUrl("javascript:" + result.getPreScript());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resultBack(view, result);
                    }
                } else {
                    resultBack(view, result);
                }
            }
        });
    }

    private void resultBack(HybridView rdCloudView, JSFuncResult funcResult) {
        Object ret = funcResult.getResult();
        if (ret != null) {
            if (ret instanceof IJSData) {
                IJSData data = (IJSData) ret;
                rdCloudView.loadUrl("javascript:" + data.getScript());
            } else {
                rdCloudView.loadUrl("javascript:" + ret);
            }
        }
    }

    /**
     * 在当前RDCloudView回调JS函数。
     * @param view RDCloudView
     * @param function JS函数类
     */
    public void jsCallback(final HybridView view, final JSFunction function) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String script = function.getScript();
                if (!TextUtils.isEmpty(script)) {
                    view.loadUrl(script);
                }
            }
        });
    }

    /**
     * 在当前RDCloudView回调JS数据。
     * @param view RDCloudView
     * @param script JS数据类
     */
    public void jsCallback(final HybridView view, final JSScript script) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String script1 = script.getScript();
                if (!TextUtils.isEmpty(script1)) {
                    view.loadUrl("javascript:" + script1);
                }
            }
        });
    }

    /**
     * 在当前javascript正确回调。
     * @param func
     * @param content
     */
    public void jsCallback(String func, Object content) {
        jsCallback(getLastTargetView(), true, func, content);
    }

    /**
     * 带json格式的callback。
     * @param func
     * @param json
     */
    public void jsonCallBack(String func, String json) {
        jsonCallBack(getLastTargetView(), true, func, json);
    }

    /**
     * 带不定参数格式的callback,参数不适合传json
     * @param func
     * @param params
     */
    public void jsCallback(String func, Object... params) {
    	jsCallback(getLastTargetView(), true, func, params);
    }
    
    /**
     * 带不定参数格式的callback,参数不适合传json
     * @param func
     * @param params
     */
    public void jsCallback(final HybridView view, boolean remove, String func, Object... params) {
        List<String> keyAndFunc = getKeyAndFunc(func);
    	StringBuffer sb = new StringBuffer();
        sb.append("javascript: var f = ").append(keyAndFunc.get(0)).append("; f(");
    	for(int i = 0; i < params.length; i++){
    		Object content = params[i];
    		final Object s = content.getClass().isAssignableFrom(String.class) ?
                    "'"+ content +"'" : content ;
    		
    		sb.append(s).append(',');
    	}
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("); ");

        if (remove) {
            addRemoveAfterCall(sb, keyAndFunc);
        }
        final String f = sb.toString();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(f);
            }
        });
    }

    public void jsonCallBack(final HybridView view, boolean remove, String func, String json) {
        List<String> keyAndFunc = getKeyAndFunc(func);
        final StringBuffer sb = new StringBuffer("javascript:var f = ").append(keyAndFunc.get(0)).append("; f(").append(json).append(");");
        if (remove) {
            addRemoveAfterCall(sb, keyAndFunc);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(sb.toString());
            }
        });
    }

    /**
     * javascript错误回调。
     *
     * @param func
     * @param content
     */
    public void jsErrCallback(String func, final Object content) {
        jsErrCallback(getLastTargetView(), func, content);
    }

    /**
     * javascript错误回调。
     *
     * @param func
     * @param content
     */
    public void jsErrCallback(final HybridView view, String func, final Object content) {
        final String f = "var f = " + func + ";";
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (content.getClass().isAssignableFrom(String.class)) {
                    view.loadUrl("javascript: " + f + " try{throw '" + content + "';}catch(e){f(e);}");
                } else {
                    view.loadUrl("javascript: " + f + " try{throw " + content + ";}catch(e){f(e);}");
                }
            }
        });
    }

    private HybridView getLastTargetView() {
        if (mRDViewList.size() == 0) {
            return null;
        }
        return mRDViewList.get(mRDViewList.size() - 1);
    }

    public int getId() {
        return mId;
    }

    /**
     * 生成require方法片段。--------》转移到plugData中使用
     * function require(name)
     * ----------------------
     * if({name} = "name") {
     *  return exec('RDCloud://com.xx.test.Example/newInstance/-1', '', true, true);
     * }
     */
    @Deprecated
    public String genPartialRequireMethod() {
        String exec_template = "exec('%s://%s/%s/%d', '', true, true);";
        String simpleName = TextUtils.isEmpty(mPluginData.mDomain) ? getClass().getSimpleName() : mPluginData.mDomain;
        StringBuffer sb = new StringBuffer();
        sb.append("if(name=='").append(simpleName).append("'){");
        sb.append("     return ").append(String.format(exec_template, HybridScript.JS_SCHEMA, getClass().getName(), "newInstance", -1));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 获取第一个目标view<br/>
     * <font color='red'><b>已过期，建议使用 方法名(RDCloudView rdCloudView, String[] params)的回调方式</b></font>
     * @return
     */
    @Deprecated
    protected HybridView getTargetView() {
        if(mRDViewList.size()>0) {
            return mRDViewList.get(0);
        }
        return null;
    }

    /**
     * 获取scope为app的内部功能。
     * @param domain 域名
     * @return 返回插件实例，找不到为null
     */
    protected PluginBase getPlugin(String domain) {
        PluginManagerBase fm = HybridEnv.getPluginManager();
        return fm.getPlugin(domain);
    }

    /**
     * 获取plugin的scope
     * @return
     */
    public PluginData.Scope getScope() {
        return PluginData.Scope.New;
    }

    protected void startActivityForResult(Intent itt, int requestCode) {
        startActivityForResult(getTargetView(), itt, requestCode);
    }

    protected void startActivityForResult(HybridView view, Intent itt, int requestCode) {
        int pathCode = mId;
        HybridWindow window = view.getRDCloudWindow();
        if (getScope() == PluginData.Scope.App) {
            //全局插件无需走路径选择。
            HybridActivity.getInstance().startActivityForResult(itt, requestCode);
        } else {
            window.startActivityForResult(view, itt, requestCode, pathCode);
        }
    }

    /**
     * 处理ActivityResult。
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 是否需要弹出UI告诉用户为什么要申请权限
     * @param permission
     * @return
     */
    protected final boolean shouldShowRequestPermissionRationale(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(HybridActivity.getInstance(), permission);
    }

    /**
     * 检查当前是否拥有权限的授权
     * @param permission
     * @return
     */
    protected final int checkSelfPermission(String permission) {
        return ContextCompat.checkSelfPermission(HybridEnv.getApplicationContext(), permission);
    }

    protected final void requestPermissions(HybridView view, String[] permissions, int requestCode) {
        mPermRDCloudViewMap.put(requestCode, new WeakReference<HybridView>(view));
        int pathCode = mId;
        HybridWindow window = view.getRDCloudWindow();
        window.requestPermissions(view, permissions, requestCode, pathCode);
    }

    /**
     * 处理权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public final void onRequestPermissionsResultInner(int requestCode, String[] permissions, int[] grantResults) {
        WeakReference<HybridView> ref = mPermRDCloudViewMap.remove(requestCode);
        if (ref != null) {
            HybridView view = ref.get();
            if (view != null) {
                onRequestPermissionsResult(ref.get(), requestCode, permissions, grantResults);
            } else {
                Log.w(getClass().getSimpleName(), "RDCloudView in ref is recycled, do not call callback.");
            }
        }
    }

    /**
     * 处理权限
     * @param view
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(HybridView view, int requestCode, String[] permissions, int[] grantResults) {

    }

    /**
     * 构造时
     */
    public void onCreate(HybridView view) {

    }

    /**
     * 注册时调用。(在注册js时)
     * @param view
     */
    public void onRegistered(HybridView view) {
        if (!mRDViewList.contains(view)) {
            mRDViewList.add(view);
        }
    }

    /**
     * 反注册时调用。(再反注册js时)
     * @param view
     */
    public void onDeregistered(HybridView view) {
        mRDViewList.remove(view);
    }

    /**
     * 销毁时调用。
     */
    public void onDestroy() {
        mRDViewList.clear();
    }

    /**
     * 生成javascript代码。使用{@link PluginData}生成
     * @return
     */
    public final String genJavascript() {
        StringBuffer js = new StringBuffer();
        try {
            PluginData data = getPluginData();
            if (data == null) {
                Constructor<?> con = getClass().getConstructor();
                data = new PluginData(con, getClass(), PluginData.Scope.New, false);
            }
            addMethodProp(data);
            //父类增加公共方法需要都配置，参考getVersion
            getPluginData().addMethodWithReturn("getVersion");
            //data.genJavaScriptEnd();
            js.append(data.genJavascript());
        } catch (NoSuchMethodException e) {

        }
        return js.toString();
    }

    @JavascriptFunction(name = "getVersion", hasReturn = true)
    public final String getVersion() {
        if (this instanceof UIPluginBase) {
            return getPluginData() == null ? "" : getPluginData().version;
        } else {
            return getVersionInner();
        }
    }

    protected String getVersionInner() {
        return "0.0.0";
    }

    /**
     * 添加js函数和属性
     * @param data
     */
    protected void addMethodProp(PluginData data) {

    }

}
