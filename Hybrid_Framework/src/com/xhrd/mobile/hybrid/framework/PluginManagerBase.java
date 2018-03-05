package com.xhrd.mobile.hybrid.framework;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.JsPromptResult;
import android.widget.Toast;

import com.xhrd.mobile.hybrid.Config;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.AbsHybridChromeClient;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.engine.HybridScript;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.engine.HybridResourceManager;
import com.xhrd.mobile.hybrid.util.ClassUtil;
import com.xhrd.mobile.hybrid.util.SystemUtil;
import com.xhrd.mobile.hybrid.util.log.LogUtils;
import com.xhrd.mobile.hybridframework.BuildConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by maxinliang on 15/6/11.
 */
public abstract class PluginManagerBase {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    protected boolean mGenerated;
    protected List<PluginData> mPluginDataList = new ArrayList<PluginData>();;
    protected Map<Class<?>, PluginBase> mGlobalPluginMap = new HashMap<Class<?>, PluginBase>();;

    public static final Object PERMISSION_IN_PROGRESS = new Object();
    public  boolean isRequestPermission = false;
    public  boolean isRequestDoze = false;
    private static final int REQUEST_CODE_PERMISSION = 0;
    private static final int REQUEST_CODE_DOZE = 0;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private static final int REQUEST_CODE_SYSTEM_ALERT_WINDOW = 1;
    private static final String WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS;
    private static final String SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;

    private CountDownLatch countDownLatch;

    /**
     * 添加一个插件。
     *
     * @param clazz
     */
    public void addPlugin(Class<? extends PluginBase> clazz) {
        try {
            Constructor<?> con = clazz.getConstructor();
            PluginData data = new PluginData(con, clazz, null, false);
            data.setFrameworkCall(true);
            mPluginDataList.add(data);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个插件。
     *
     * @param data
     */
    void removePlugin(PluginData data) {
        mPluginDataList.remove(data);
        mGlobalPluginMap.remove(data.mClass);
    }

    public void registerPlugin(HybridView view) {
        for (PluginBase base : mGlobalPluginMap.values()) {
            view.registerPlugin(base);
        }
    }

    /**
     * 回调全局功能、插件
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DOZE && isRequestDoze){// 引擎发起的申请加入休眠白名单
            if (countDownLatch != null)
                countDownLatch.countDown();
            isRequestDoze = false;
        } else if(requestCode == REQUEST_CODE_SYSTEM_ALERT_WINDOW || requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        } else {
            for (PluginBase plugin : mGlobalPluginMap.values()) {
                plugin.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 清除插件
     */
    public void destroy() {
        for (PluginBase base : mGlobalPluginMap.values()) {
            base.onDestroy();
        }
        mGlobalPluginMap.clear();
        //mGlobalPluginJsonMap.clear();
        mPluginDataList.clear();
    }

    /**
     * 根据pluginDataList生成插件对应的javascript和get方法。（初始化时）
     */
    String genJavascript() {
        if (mGenerated) {
            throw new IllegalStateException("javascript has been generated.");
        }

        //符合注解模式所使用资源——scope=App
        List<PluginData> globalDataList = new ArrayList<PluginData>();
        //符合注解模式所使用资源--scope=Window、New
        List<PluginData> pluginDataList = new ArrayList<PluginData>();


        //不符合注解模式所使用资源——scope=App
        List<PluginBase> globalList = new ArrayList<PluginBase>();
        //不符合注解模式所使用资源——scope=Window、New
        List<PluginBase> pluginList = new ArrayList<PluginBase>();

        for (PluginData data : mPluginDataList) {
            if (data.isAnnotatable) {// 符合注解模式
                if (data.mScope != PluginData.Scope.New) {// App
                    // 添加js属性
                    data.addPropertyByAnnotation();

                    // 添加js方法
                    Method[] methods = data.mClass.getMethods();
                    for (Method method : methods) {
                        // 获取js方法的信息
                        JavascriptFunction javascriptFunction = method.getAnnotation(JavascriptFunction.class);
                        if (javascriptFunction != null) {// 不为null, 说明此方法是jsFunc
                            if (TextUtils.isEmpty(javascriptFunction.name())) {
                                Log.w(getClass().getSimpleName(), method.getName() + "method has @JavascriptFunction but no name.");
                                continue;
                            }
                            // 获得jsUiFunc注释，判断此jsFunc是否为jsUiFunc------->内部插件不支持做成UI插件，但TabMark做成了UI，这里只能适配一下了
                            JavascriptUiFunction javascriptUiFunction = method.getAnnotation(JavascriptUiFunction.class);
                            if (javascriptUiFunction != null) {
                                if (data.isUiPlugin) {
                                    data.addMethod(javascriptFunction.name(), null, javascriptFunction.hasReturn(), javascriptFunction.convertJS(), javascriptFunction.permissions(), javascriptFunction.permissionRationales(), javascriptFunction.doze());
                                }
                            } else {
                                data.addMethod(javascriptFunction.name(), null, javascriptFunction.hasReturn(), javascriptFunction.convertJS(), javascriptFunction.permissions(), javascriptFunction.permissionRationales(), javascriptFunction.doze());
                            }
                        }
                    }
                    // plugin的property和jsMethod都已添加到了plugin的data中，后续不许再添加了
                    data.mGenerated = true;
                    globalDataList.add(data);
                } else {// Window, New
                    pluginDataList.add(data);
                }

            } else {                       //不符合注解模式

                try {
                    Object plugin = data.mConsturctor.newInstance();
                    if (plugin instanceof PluginBase) {
                        PluginBase base = (PluginBase) plugin;
                        base.setPluginData(data);

                        if (this instanceof FrameworkManager) {
                            if (base.getScope() != PluginData.Scope.New) {
                                globalList.add(base);
                            } else {
                                pluginList.add(base);
                            }
                        } else {
                            if (data.mScope != PluginData.Scope.New) {
                                globalList.add(base);
                            } else {
                                pluginList.add(base);
                            }
                        }
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();

                }

            }
        }

        StringBuilder globalSB = new StringBuilder();
        //生成rd.xxx全局--->新方法，注解中添加“方法信息”
        for (PluginData pluginData : globalDataList) {
            String domain = TextUtils.isEmpty(pluginData.mDomain) ? pluginData.mClass.getSimpleName() : pluginData.mDomain;
            if (this instanceof FrameworkManager) {
                globalSB.append(PluginData.JS_OBJECT_BEGIN_FRAMEWORK).append(domain).append('=').append(pluginData.genJavascript()).append(';');
                globalSB.append(HybridScript.RD).append('.').append(domain).append('=').append(PluginData.JS_OBJECT_BEGIN_FRAMEWORK).append(domain).append(';');
            } else {
                globalSB.append(PluginData.JS_OBJECT_BEGIN_PLUGIN).append(domain).append('=').append(pluginData.genJavascript()).append(';');
                globalSB.append(HybridScript.RD).append('.').append(domain).append('=').append(PluginData.JS_OBJECT_BEGIN_PLUGIN).append(domain).append(';');
            }
        }

        //生成rd.xxx全局--->旧方法
        for (PluginBase base : globalList) {
            String domain = TextUtils.isEmpty(base.getPluginData().mDomain) ? base.getClass().getSimpleName() : base.getPluginData().mDomain;
            if (this instanceof FrameworkManager) {
                globalSB.append(PluginData.JS_OBJECT_BEGIN_FRAMEWORK).append(domain).append('=').append(base.genJavascript()).append(';');
                globalSB.append(HybridScript.RD).append('.').append(domain).append('=').append(PluginData.JS_OBJECT_BEGIN_FRAMEWORK).append(domain).append(';');
            } else {
                globalSB.append(PluginData.JS_OBJECT_BEGIN_PLUGIN).append(domain).append('=').append(base.genJavascript()).append(';');
                globalSB.append(HybridScript.RD).append('.').append(domain).append('=').append(PluginData.JS_OBJECT_BEGIN_PLUGIN).append(domain).append(';');
            }
            // plugin的property和jsMethod都已添加到了plugin的data中，后续不许再添加了
            base.getPluginData().mGenerated = true;
        }

        HybridScript.RDScript += globalSB.toString();


        StringBuffer sb = new StringBuffer();
        //生成rd.require局部--->新方法，注解中添加“方法信息”
        for (PluginData pluginData : pluginDataList) {
            sb.append(pluginData.genPartialRequireMethod());
        }
        //生成rd.require局部--->旧方法
        for (PluginBase base : pluginList) {
            sb.append(base.genPartialRequireMethod());
        }

        return sb.toString();
    }

    /**
     * 获取js代码（执行js时）
     * @param base
     * @return
     */
    public String getJsByPlugin(PluginBase base){
        String ret = null;
        PluginData pluginData = base.getPluginData();
        if(pluginData.mGenerated){
            ret = pluginData.genJavascript().replace("id:-1", "id:" + base.getId());
            return ret;
        }

        if (pluginData.isAnnotatable) {
            // 添加js属性
            pluginData.addPropertyByAnnotation();

            // 添加js方法
            Method[] methods = pluginData.mClass.getMethods();
            for (Method method : methods) {
                // 获取js方法的信息
                JavascriptFunction javascriptFunction = method.getAnnotation(JavascriptFunction.class);
                if (javascriptFunction != null) {// 不为null, 说明此方法是jsFunc
                    if (TextUtils.isEmpty(javascriptFunction.name())) {
                        Log.w(getClass().getSimpleName(), method.getName() + "has @JavascriptFunction but no name.");
                        continue;
                    }
                    // 获得jsUiFunc注释，判断此jsFunc是否为jsUiFunc------->内部插件不支持做成UI插件，但TabMark做成了UI，这里只能适配一下了
                    JavascriptUiFunction javascriptUiFunction = method.getAnnotation(JavascriptUiFunction.class);
                    if (javascriptUiFunction != null) {
                        if (pluginData.isUiPlugin) {
                            pluginData.addMethod(javascriptFunction.name(), null, javascriptFunction.hasReturn(), javascriptFunction.convertJS(), javascriptFunction.permissions(), javascriptFunction.permissionRationales(), javascriptFunction.doze());
                        }
                    } else {
                        pluginData.addMethod(javascriptFunction.name(), null, javascriptFunction.hasReturn(), javascriptFunction.convertJS(), javascriptFunction.permissions(), javascriptFunction.permissionRationales(), javascriptFunction.doze());
                    }
                }
            }
            // plugin的property和jsMethod都已添加到了plugin的data中，后续不许再添加了
            pluginData.mGenerated = true;
            ret = pluginData.genJavascript().replace("id:-1", "id:" + base.getId());
        } else {
            ret = base.genJavascript().replace("id:-1", "id:" + base.getId());
            // plugin的property和jsMethod都已添加到了plugin的data中，后续不许再添加了
            pluginData.mGenerated = true;
        }

        return ret;
    }

    /**
     * 执行内部插件方法
     * @param view
     * @param message RDCloud://com.xx.test.Example/method/index
     * @param params  参数列表
     * @param jsPromptResult 返回值处理类，6.0系统以上使用
     * @return
     */
    public Object exec(HybridView view, String message, List<String> params, final JsPromptResult jsPromptResult) {
        if (Config.DEBUG) {
            Log.d("FrameworkManager", "exec -- message: " + message + ",   defaultValue: " + params);
        }
        Object result = null;
        if (!TextUtils.isEmpty(message)) {
            Uri uri = Uri.parse(message);
            if (PluginManager.isJavaScript(uri.getScheme())) {
                Object r = null;
                String className = uri.getHost();
                String method = uri.getPathSegments().get(0);
                int id = -1;
                if (uri.getPathSegments().size() >= 2) {
                    id = Integer.parseInt(uri.getPathSegments().get(1));
                }
                try {
                    r = execOnView(view, className, method, id, params.toArray(new String[0]), jsPromptResult);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                result = r;
            }
        }
        return result;
    }

    /**
     * 执行javascript对应的本地代码。
     *
     * @param view
     * @param className  class全限定名
     * @param methodName 执行的方法
     * @param params     执行的参数
     * @param jsPromptResult 返回值处理类
     * @return 返回的值
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    protected final Object execOnView(final HybridView view, String className, final String methodName, int id, final String[] params, final JsPromptResult jsPromptResult) throws
            InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        if (view.getRDCloudWindow() == null) {
//            LogManager log = (LogManager) getPlugin("log");
//            log.e("Window or popover", new String[]{"js calls happened after a Window or popover has been removed."});
            Log.i(getClass().getSimpleName(), "js calls happened after a Window or popover has been removed.");
            return null;
        }
        Class<?> clazz = ClassUtil.loadClass(view.getContext(), className);
        PluginData pluginData = null;
        for (PluginData data : mPluginDataList) {
            if (data.mClass.getName().equals(className)) {
                pluginData = data;
                break;
            }
        }
        //判断是否是framework的
        if (pluginData == null) {
            return null;
        }

        Object ret = null;
        //创建新的实例
        if ("newInstance".equals(methodName)) {
            Class<?>[] p = new Class<?>[]{Class.class};
            try {
                if (pluginData.mScope == PluginData.Scope.App) {
                    PluginBase base = mGlobalPluginMap.get(pluginData.mClass);
                    if (base == null) {
                        Method method = clazz.getMethod(methodName, p);
                        Pair<String, PluginBase> retPair = (Pair<String, PluginBase>) method.invoke(null, clazz);
                        retPair.second.onCreate(view);
                        retPair.second.onRegistered(view);
                        base = retPair.second;
                        mGlobalPluginMap.put(base.getClass(), base);
                    }
                    //PluginData在读取plugins.xml时创建
                    //ret = base.getPluginData().genJavascript();
                    ret = getJsByPlugin(base);
                } else if (pluginData.mScope == PluginData.Scope.Window) {
                    Map<Class<?>, PluginBase> map = getWindowInjectedJSObj(view);
                    PluginBase base = map.get(pluginData.mClass);
                    if (base == null) {
                        Method method = clazz.getMethod(methodName, p);
                        Pair<String, PluginBase> retPair = (Pair<String, PluginBase>) method.invoke(null, clazz);
                        retPair.second.onCreate(view);
                        retPair.second.onRegistered(view);
                        base = retPair.second;
                        base.setPluginData(pluginData);
                        map.put(pluginData.mClass, base);
                    }
                    ret = getJsByPlugin(base);
                } else {
                    //存储createNew范围的本地功能插件。
                    PluginBase base = (PluginBase) clazz.newInstance();
                    base.setPluginData(pluginData);
                    base.onCreate(view);
                    view.registerPlugin(base);
                    ret = getJsByPlugin(base);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            //调用插件方法
            PluginBase base = null;
            switch (pluginData.mScope) {
                case App:
                    base = mGlobalPluginMap.get(pluginData.mClass);
                    if (base == null) {
                        base = (PluginBase) pluginData.mConsturctor.newInstance();
                        base.setPluginData(pluginData);
                        base.onCreate(view);
                        mGlobalPluginMap.put(pluginData.mClass, base);
                    }
                    view.registerPlugin(base);
                    break;
                case Window:
                    Map<Class<?>, PluginBase> map = getWindowInjectedJSObj(view);
                    base = map.get(pluginData.mClass);
                    if (base == null) {
                        base = (PluginBase) pluginData.mConsturctor.newInstance();
                        base.setPluginData(pluginData);
                        base.onCreate(view);
                        map.put(pluginData.mClass, base);
                    }
                    view.registerPlugin(base);
                    break;
                case New:
                    Map<Integer, PluginBase> map1 = getViewInjectedJSObj(view);
                    base = map1.get(id);
                    break;
            }
            if (base == null) {
                return null;
            }

           ret = invokePluginMethodByName(view, base, pluginData, methodName, params, jsPromptResult);
        }
        return ret;
    }

    /**
     * 权限处理
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public boolean onRequestPermissionsResult(int id, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION && isRequestPermission){// 引擎发起的申请权限回调

            if (countDownLatch != null && grantResults.length == permissions.length) {
                countDownLatch.countDown();
                isRequestPermission = false;
            }
            return true;
        }else {
            for (PluginBase plugin : mGlobalPluginMap.values()) {
                if (id == plugin.getId()) {
                    plugin.onRequestPermissionsResultInner(requestCode, permissions, grantResults);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 检查是否授权
     * @param base InternalPluginBase对象
     * @param permissions 请求的权限数组
     * @return
     */
    private boolean existsPermissions(PluginBase base,String[] permissions) {
        for(String permission : permissions) {
            if (android.content.pm.PackageManager.PERMISSION_GRANTED != base.checkSelfPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 首先使用名字查找，如果找不到循环再次查找。
     * @param view
     * @param base
     * @param methodName
     * @param params
     * @return
     */
    protected final Object invokePluginMethodByNameInner6(final HybridView view, final PluginBase base, final PluginData pluginData, final String methodName, final Object[] params, final JsPromptResult jsPromptResult) {
        //如果是6.0及以上版本执行权限检查
        PluginMethodData pluginMethodData = pluginData.getPluginMethodDataByMethodName(methodName);

        final String[] permissions = pluginMethodData.getPermissions();
        // pluginMethod是否需申请权限，检查权限是否已获取
        if (pluginMethodData == null){
            return invokePluginMethodByNameInner(view, base, methodName, params);
        }

        final PluginBase internalPluginBase = base;
        final String[] denyInfo = pluginMethodData.getPermissionRationales();
        //是否请求加入白名单
        if(pluginMethodData.isAddWhitelist()) {
            final String packageName = view.getContext().getPackageName();
            final PowerManager pm = (PowerManager)view.getContext().getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                // 卡住
                countDownLatch = new CountDownLatch(1);
                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                HybridActivity.getInstance().startActivityForResult(intent, REQUEST_CODE_DOZE);
                new Thread("doze_request_thread") {
                    @Override
                    public void run() {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        invokePluginMethodByNameInner6(view, base, pluginData, methodName, params, jsPromptResult, permissions, denyInfo);
                    }
                }.start();
                isRequestDoze = true;
                return PERMISSION_IN_PROGRESS;
            }
            return invokePluginMethodByNameInner6(view, base, pluginData, methodName, params, jsPromptResult, permissions, denyInfo);
        }else {
            return invokePluginMethodByNameInner6(view, base, pluginData, methodName, params, jsPromptResult, permissions, denyInfo);
        }
    }

    /**
     * 首先使用名字查找，如果找不到循环再次查找。
     * @param view
     * @param base
     * @param methodName
     * @param params
     * @param permissions
     * @return
     */
    private Object invokePluginMethodByNameInner6(final HybridView view, final PluginBase base, final PluginData pluginData,
                                                  final String methodName, final Object[] params, final JsPromptResult jsPromptResult,
                                                  final String[] permissions, final String[] denyInfo) {
        if(permissions == null || permissions.length == 0 || existsPermissions(base, permissions)) {
            return invokePluginMethodByNameInner(view, base, methodName, params);
        }

        final AbsHybridChromeClient chromeClient = view.getChromeClient();

        int permissionLength = permissions.length;
        int generalPermissionLength = permissionLength;
        ArrayList<String> specialPermissionList = new ArrayList<>();
        final HashMap<String,String> specialDenyInfoMap = new HashMap<>();
        ArrayList<String> requestPermissionList = new ArrayList<>();
        int denyInfoLength = denyInfo != null ? denyInfo.length : 0;

        for(int i=0;i<permissionLength;i++) {
            String permission = permissions[i];
            if(permission.equals(WRITE_SETTINGS) || permission.equals(SYSTEM_ALERT_WINDOW)) {
                generalPermissionLength --;
                specialPermissionList.add(permission);

                if(denyInfoLength==permissionLength) {
                    specialDenyInfoMap.put(permission, denyInfo[i]);
                }
            }else {
                requestPermissionList.add(permission);
            }
        }

        int specialPermissionLength = specialPermissionList.size();

        String[] tempPermissions = null;

        boolean tempIsExistsWriteSetting = false;
        boolean tempIsExistsAlert = false;
        if(specialPermissionLength > 0) {
            // 卡住
            countDownLatch = new CountDownLatch(specialPermissionLength);
            String packageName = view.getContext().getPackageName();

            for(String permission : specialPermissionList) {
                String uri = "";
                int requestCode = 0;
                if(permission.equals(WRITE_SETTINGS)) {
                    uri = Settings.ACTION_MANAGE_WRITE_SETTINGS;
                    requestCode = REQUEST_CODE_WRITE_SETTINGS;
                    tempIsExistsWriteSetting = true;
                } else if(permission.equals(SYSTEM_ALERT_WINDOW)) {
                    uri = Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
                    requestCode = REQUEST_CODE_SYSTEM_ALERT_WINDOW;
                    tempIsExistsAlert = true;
                }
                Intent intent = new Intent(uri);
                intent.setData(Uri.parse("package:" + packageName));
                HybridActivity.getInstance().startActivityForResult(intent, requestCode);
            }

            if(generalPermissionLength > 0) {
                tempPermissions = requestPermissionList.toArray(new String[generalPermissionLength]);
            }
        }else {
            // 卡住
            countDownLatch = new CountDownLatch(1);
            tempPermissions = permissions;
            // 请求权限
            base.requestPermissions(view, tempPermissions, REQUEST_CODE_PERMISSION);
        }
        final boolean isExistsWriteSetting = tempIsExistsWriteSetting;
        final boolean isExistsAlert = tempIsExistsAlert;
        final String[] requestPermissions = tempPermissions;

        new Thread("permission_request_thread") {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isExistsAlert || isExistsWriteSetting) {
                    if(SystemUtil.isMarshmallow()) {
                        boolean isWriteSettingIgree = true;
                        boolean isAlertIgree = true;
                        if(isExistsAlert) {
                            isAlertIgree = Settings.canDrawOverlays(HybridActivity.getInstance());
                        }
                        if(isExistsWriteSetting) {
                            isWriteSettingIgree = Settings.System.canWrite(HybridActivity.getInstance());
                        }
                        if(isAlertIgree && isWriteSettingIgree) {
                            if(requestPermissions != null) {
                                countDownLatch = new CountDownLatch(1);
                                base.requestPermissions(view, requestPermissions, REQUEST_CODE_PERMISSION);
                                try {
                                    countDownLatch.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else {
                            String msg = "";
                            if(!isAlertIgree) {
                                msg = specialDenyInfoMap.get(SYSTEM_ALERT_WINDOW)+"，";
                            }
                            if(!isWriteSettingIgree) {
                                msg += specialDenyInfoMap.get(WRITE_SETTINGS);
                            }else {
                                if(!TextUtils.isEmpty(msg)) {
                                    msg = msg.substring(0,msg.length()-1);
                                }else {
                                    msg = HybridResourceManager.getInstance().getString("request_special_permission_msg");
                                }
                            }
                            denyToast(chromeClient,view,jsPromptResult,msg);
                            return;
                        }
                    }
                }
                if (requestPermissions == null || existsPermissions(base, requestPermissions)) {
                    // 权限申请成功，执行对应方法，需在ui线程中执行，不然可能会报错
                    view.getRDCloudWindow().post(new Runnable() {
                        @Override
                        public void run() {
                            Object ret = invokePluginMethodByNameInner(view, base, methodName, params);
                            chromeClient.preResultBack(view, jsPromptResult, ret);
                        }
                    });
                } else {
                    final String requestPermissionError;// = RDResourceManager.getInstance().getString("request_permission_error");
                    if (shouldShowRequestPermissionsRationale(base, requestPermissions) == -1) {
                        requestPermissionError = getPermissionMsg(denyInfo);
                    } else {
                        requestPermissionError = HybridResourceManager.getInstance().getString("request_permission_error");
                    }
                    denyToast(chromeClient,view,jsPromptResult,requestPermissionError);
                }
            }
        }.start();
        isRequestPermission = true;
        return PERMISSION_IN_PROGRESS;
    }

    /**
     * 拒绝后弹框提醒
     * @param chromeClient
     * @param view
     * @param jsPromptResult
     * @param denyInfo 提醒内容
     */
    private void denyToast(AbsHybridChromeClient chromeClient, final HybridView view, final JsPromptResult jsPromptResult, final String denyInfo) {
        // 权限申请失败
        LogUtils.e4defualtTag(denyInfo);
        chromeClient.preResultBack(view, jsPromptResult, denyInfo);
        view.getRDCloudWindow().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(view.getContext(), denyInfo, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 解析权限提醒消息
     * @param permissionMsg
     * @return
     */
    private String getPermissionMsg(String[] permissionMsg) {
        int denyLen = permissionMsg != null ? permissionMsg.length : 0;
        String msg = "";
        for(int i=0; i<denyLen; i++) {
            msg += permissionMsg[i] + "、";
        }
        if(TextUtils.isEmpty(msg)) {
            msg = HybridResourceManager.getInstance().getString("request_permission_msg");
        }else {
            msg = msg.substring(0,msg.length()-1);
        }
        return msg;
    }

    /**
     * 检查拒绝权限是否勾选“不在提示”
     * @param base
     * @param permissions
     * @return
     */
    private int shouldShowRequestPermissionsRationale(PluginBase base,String[] permissions) {
        int length = permissions.length;
        for(int i=0; i<length; i++) {
            if(base.shouldShowRequestPermissionRationale(permissions[i])) {
               return i;
            }
        }
        return -1;
    }

    /**
     * 首先使用名字查找，如果找不到循环再次查找。
     * @param view
     * @param base
     * @param methodName
     * @param params
     * @return
     */
    protected final Object invokePluginMethodByNameInner(final HybridView view, final PluginBase base, final String methodName, final Object[] params) {
        Object ret = null;
        int paramType = 0;
        Class<?>[] p = new Class<?>[]{String.class, String[].class};
        try {
            Method method = null;
            try {
                method = base.getClass().getMethod(methodName, p);
                paramType = 1;
            } catch (NoSuchMethodException e) {
                try {
                    p = new Class<?>[]{String.class};
                    method = base.getClass().getMethod(methodName, p);
                    paramType = 2;
                } catch (NoSuchMethodException e1) {
                    try {
                        p = new Class<?>[]{HybridView.class};
                        method = base.getClass().getMethod(methodName, p);
                        paramType = 3;
                    } catch (NoSuchMethodException e3) {
                        try {
                            p = new Class<?>[]{String[].class};
                            method = base.getClass().getMethod(methodName, p);
                            paramType = 4;
                        } catch (NoSuchMethodException e4) {
                            try {
                                p = new Class<?>[]{HybridView.class, String[].class};
                                method = base.getClass().getMethod(methodName, p);
                                paramType = 5;
                            } catch (NoSuchMethodException e5) {
                                method = base.getClass().getMethod(methodName);
                                paramType = 6;
                            }
                        }
                    }
                }
            }

            if (method.isAnnotationPresent(JavascriptFunction.class)) {
                switch (paramType) {
                    case 3:
                        ret = method.invoke(base, view);// RDCloudView
                        break;
                    case 4:
                        ret = method.invoke(base, new Object[]{params});// params[]
                        break;
                    case 5:
                        ret = method.invoke(base, view, params);// RDCloudView, params[]
                        break;
                    case 6:
                        ret = method.invoke(base);// 无参
                        break;
                }
            }
//            else {
//                //如果方法找错了，循环查找
//                List<Method> methodList = new ArrayList<Method>();
//                Class<?> clazz = base.getClass();
//                methodList.addAll(Arrays.asList(clazz.getMethods()));
//                for (Method m : methodList) {
//                    if (m.isAnnotationPresent(JavascriptFunction.class) && m.getName().equals(methodName)) {
//                        Class<?>[] paramTypes = m.getParameterTypes();
//                        try {
//                            switch (paramTypes.length) {
//                                case 0:
//                                    ret = m.invoke(base);
//                                    break;
//                                case 1:
//                                    if (paramTypes[0] == RDCloudView.class) {
//                                        ret = m.invoke(base, view);
//                                    } else if (paramTypes[0] == String.class) {
//                                        ret = m.invoke(base, windowName);
//                                    } else if (paramTypes[0] == String[].class) {
//                                        ret = m.invoke(base, params);
//                                    }
//                                    break;
//                                case 2:
//                                    if (paramTypes[0] == RDCloudView.class) {
//                                        ret = m.invoke(base, view, params);
//                                    } else if (paramTypes[0] == String.class) {
//                                        ret = m.invoke(base, windowName, params);
//                                    }
//                                    break;
//                                default:
////                                    Log.w(getClass().getSimpleName(), "can not find out method " + m.getName() + " with " + paramTypes.length + " parameter types, default to call it without parameter.");
////                                    ret = m.invoke(base);
//                                    Log.w(getClass().getSimpleName(), "mismatched with " + m.getName() + " which has " + paramTypes.length + " parameters.");
//                                    break;
//                            }
//                        } catch (Exception e1) {
//                            Log.e(getClass().getSimpleName(), "invoke plugin's method(" + m.getName() + ") failed.", e1);
//                            break;
//                        }
//                    }
//                }
//            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(getClass().getSimpleName(), "invoke plugin's method(" + methodName + ") failed.", e);
            }
        }
        return ret;
    }

    /**
     * 获取scope为app的内部功能。
     * @param domain 域名
     * @return 返回插件实例，找不到为null
     */
    public PluginBase getPlugin(String domain) {
        PluginBase base = null;
        for (Map.Entry<Class<?>, PluginBase> entry : mGlobalPluginMap.entrySet()) {
            if (entry.getValue().getDefaultDomain().equals(domain)) {
                base = entry.getValue();
            }
        }
        if (base == null) {
            for (PluginData data : mPluginDataList) {
                if (data.mScope == PluginData.Scope.App && data.mDomain.equals(domain)) {
                    try {
                        base = (PluginBase) data.mConsturctor.newInstance();
                        base.setPluginData(data);
                        base.onCreate(null);
                        mGlobalPluginMap.put(data.mClass, base);
                        break;
                    } catch (Exception e) {
                        Log.e(getClass().getSimpleName(), "initialize plugin in method getPlugin failed.", e);
                        break;
                    }
                }
            }
        }
        return base;
    }

    /**
     * 判断是否符合JS调用
     * @param message
     * @return
     */
    public static boolean isJavaScript(String message) {
        return message.startsWith(HybridScript.JS_SCHEMA);
    }

    /**
     * 判断是否是内部功能调用
     * @param message
     * @return
     */
    public static boolean isFrameworkCall(String message) {
        return message.startsWith(HybridScript.JS_SCHEMA_0);
    }

    protected abstract Map<Class<?>, PluginBase> getWindowInjectedJSObj(HybridView view);
    protected abstract Map<Integer, PluginBase> getViewInjectedJSObj(HybridView view);

    /**
     * 执行插件方法
     * @param view
     * @param base
     * @param pluginData
     * @param methodName
     * @param params
     * @param jsPromptResult
     * @return
     */
    protected abstract Object invokePluginMethodByName(final HybridView view, final PluginBase base, PluginData pluginData, final String methodName, final Object[] params, final JsPromptResult jsPromptResult);

    /**
     * Created by maxinliang on 15/6/12.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface JavascriptUiFunction {
    }

}