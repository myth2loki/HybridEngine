package com.xhrd.mobile.hybrid.framework;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.JsPromptResult;

import com.xhrd.mobile.hybrid.engine.EngineEventListener;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.util.ClassUtil;
import com.xhrd.mobile.hybrid.util.log.LogUtils;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 查件管理类，主要负责管理第三方插件。
 */
public class PluginManager extends PluginManagerBase {

    public static String PLUGIN_NODE = "plugin";
    public static String METHOD_NODE = "method";
    public static String PERMISSION_NODE = "permission";
    public static String ITEM_NODE = "item";
    public static String PERMISSION_DEFINE_NODE = "permissionDefine";
    public static String PROPERTIES_NODE = "properties";
    public static String PROPERTY_ATTR = "property";
    public static String DOMAIN_ATTR = "domain";
    public static String CLASS_NAME_ATTR = "className";
    public static String NAME_ATTR = "name";
    public static String DOZE_ATTR = "isAddWhitelist";
    public static String RATIONALE_ATTR = "rationale";
    public static String VALUE_ATTR = "value";
    public static String STARTUP_ATTR = "startup";
    public static String SCOPE_ATTR = "scope";
    public static String VERSION_ATTR = "version";
    public static String RETURN_ATTR = "return";
    public static String CONVERT_RETURN_ATTR = "convertReturn";
    public static String PRE_PARAMS_ATTR = "preParams";
    public static String LIFE_CYCLE_CALLBACK_ATTR = "lifeCycleCallback";
    public static String IS_UI_PLUGIN = "isUi";

    public PluginManager(XmlPullParser plugins, LinkedList<EngineEventListener> mustInitObj, Context context) {
        super();
        if (plugins != null) {
            initPluginClass(plugins, mustInitObj, context);
        }
    }

    /**
     * 初始化插件，并生成js代码
     *
     * @param pluginParser
     * @param listenerQueue
     * @param context
     */
    private void initPluginClass(XmlPullParser pluginParser, LinkedList<EngineEventListener> listenerQueue, Context context) {
        int eventType = -1;
        String domain = "", javaName = "";
        String version = "";
        PluginData scriptObj = null;

        // 存储 permissionDefine 特殊定义的节点，每一个plugin节点读取完后，进行合并
        Map<String, PluginMethodData> methodDatas = new HashMap<>();

        try {
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG) {
                    String strNode = pluginParser.getName();
                    if (strNode.equals(PLUGIN_NODE)) {
                        domain = pluginParser.getAttributeValue(null, DOMAIN_ATTR);
                        javaName = pluginParser.getAttributeValue(null, CLASS_NAME_ATTR);

                        boolean startup = "true".equals(pluginParser.getAttributeValue(null, STARTUP_ATTR));
                        version = pluginParser.getAttributeValue(null, VERSION_ATTR);
                        String scopeStr = pluginParser.getAttributeValue(null, SCOPE_ATTR);
                        PluginData.Scope scope = PluginData.Scope.New;
                        if (!TextUtils.isEmpty(scopeStr)) {
                            scope = PluginData.Scope.valueOf(scopeStr);
                        }
                        boolean isUi = "true".equals(pluginParser.getAttributeValue(null, IS_UI_PLUGIN));

                        if (!TextUtils.isEmpty(javaName)) {
                            Constructor<?> constructor = null;
                            Class<?> clazz = ClassUtil.loadClass(context, javaName);
                            if (clazz != null) {
                                constructor = clazz.getConstructor();
                                if (constructor != null) {
                                    scriptObj = new PluginData(constructor, clazz, scope, isUi);
                                    scriptObj.genJavaScriptBegin(domain);
                                    scriptObj.mConsturctor = constructor;
                                    scriptObj.mDomain = domain;
                                    scriptObj.mClass = clazz;
                                    scriptObj.version = version;
                                    scriptObj.mScope = scope;
                                }
                            }
                        }
                    } else if (strNode.equals(METHOD_NODE)) {// method node
                        String methodValue = pluginParser.getAttributeValue(null, NAME_ATTR);
                        boolean isAddWhitelist = "true".equals(pluginParser.getAttributeValue(null, DOZE_ATTR));
                        String preParamStr = pluginParser.getAttributeValue(null, PRE_PARAMS_ATTR);
                        boolean convertReturn = "true".equals(pluginParser.getAttributeValue(null, CONVERT_RETURN_ATTR));
                        boolean hasReturn = "true".equals(pluginParser.getAttributeValue(null, RETURN_ATTR));

                        // 解析permission
                        List<String> permissionList = new ArrayList<>();
                        List<String> permissionRationaleList = new ArrayList<>();
                        while (eventType != XmlResourceParser.END_DOCUMENT) {
                            eventType = pluginParser.next();
                            strNode = pluginParser.getName();
                            if (eventType == XmlResourceParser.START_TAG) {
                                if (strNode.equals(PERMISSION_NODE)) {
                                    String permission = pluginParser.getAttributeValue(null, NAME_ATTR);
                                    permissionList.add(permission);
                                    String permissionRationale = pluginParser.getAttributeValue(null, RATIONALE_ATTR);
                                    permissionRationaleList.add(permissionRationale);
                                }
                            } else if (eventType == XmlResourceParser.END_TAG) {
                                if (strNode.equals(METHOD_NODE)) {
                                    break;
                                }
                            }
                        }

                        if (null != scriptObj) {
                            //前置参数解析
                            String[] preParams = null;
                            if (!TextUtils.isEmpty(preParamStr)) {
                                preParams = preParamStr.split(",");
                            }

                            String[] permissions = null;
                            if (!permissionList.isEmpty()) {
                                permissions = permissionList.toArray(new String[permissionList.size()]);
                            }
                            String[] permissionRationales = null;
                            if (!permissionRationaleList.isEmpty()){
                                permissionRationales = permissionRationaleList.toArray(new String[permissionRationaleList.size()]);
                            }

                            if (convertReturn) {
                                scriptObj.addMethod(methodValue, preParams, true, true, permissions, permissionRationales, isAddWhitelist);
                            } else if (hasReturn) {
                                scriptObj.addMethod(methodValue, preParams, true, false, permissions, permissionRationales, isAddWhitelist);
                            } else {
                                scriptObj.addMethod(methodValue, preParams, false, false, permissions, permissionRationales, isAddWhitelist);
                            }
                        }
                    }else if(strNode.equals(PERMISSION_DEFINE_NODE)){// permissionDefine node

                        List<String> methods = new ArrayList<String>();
                        List<String> permissionList = new ArrayList<>();
                        List<String> permissionRationaleList = new ArrayList<>();
                        while (eventType != XmlResourceParser.END_DOCUMENT) {
                            eventType = pluginParser.next();
                            strNode = pluginParser.getName();
                            if (eventType == XmlResourceParser.START_TAG) {
                                if (strNode.equals(ITEM_NODE)) {// permission item
                                    String permission = pluginParser.getAttributeValue(null, NAME_ATTR);
                                    permissionList.add(permission);
                                    String permissionRationale = pluginParser.getAttributeValue(null, RATIONALE_ATTR);
                                    permissionRationaleList.add(permissionRationale);
                                }else if(strNode.equals(METHOD_NODE)){// method
                                    String methodName = pluginParser.getAttributeValue(null, NAME_ATTR);
                                    methods.add(methodName);
                                }
                            }else if (eventType == XmlResourceParser.END_TAG) {
                                if (strNode.equals(PERMISSION_DEFINE_NODE)) {
                                    for (String method : methods) {
                                        PluginMethodData data = new PluginMethodData(method, permissionList.toArray(new String[permissionList.size()]), permissionRationaleList.toArray(new String[permissionRationaleList.size()]));
                                        methodDatas.put(method, data);
                                    }
                                    break;
                                }
                            }
                        }
                    }else if (strNode.equals(PROPERTIES_NODE)) {
                        // property node开始
                    } else if (strNode.equals(PROPERTY_ATTR)) {
                        String propertyName = pluginParser.getAttributeValue(null, NAME_ATTR);
                        String propertyValue = pluginParser.getAttributeValue(null, VALUE_ATTR);
                        if (null != scriptObj) {
                            if (!TextUtils.isEmpty(propertyName)) {
                                if (!TextUtils.isEmpty(propertyValue)) {
                                    //scriptObj.addProperty(propertyName, propertyValue);
                                    scriptObj.addPlainProperty(propertyName, propertyValue);
                                } else {
                                    scriptObj.addProperty(propertyName);
                                }
                            }
                        }
                    }
                } else if (eventType == XmlResourceParser.END_TAG) {
                    String strNode = pluginParser.getName();
                    if (strNode.equals(PLUGIN_NODE) && null != scriptObj) {

                        if(!methodDatas.isEmpty()){
                            // 处理 permissionDefine 特殊定义的节点
                            for (Pair<String, PluginMethodData> pair : scriptObj.getMethodList()) {
                                if(pair.second.getPermissions() == null){
                                    PluginMethodData data = methodDatas.get(pair.first);
                                    if (data != null){
                                        pair.second.setPermissions(data.getPermissions());
                                        pair.second.setPermissionRationales(data.getPermissionRationales());
                                    }
                                }
                            }
                        }

                        LogUtils.d4defualtTag(scriptObj.getMethodList().toString());

                        mPluginDataList.add(scriptObj);
                        scriptObj = null;
                        methodDatas.clear();
                    }
                }
                eventType = pluginParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("plugin对象加载发生异常,请检查hybrid/plugin/plugins.xml文件! " + e.getMessage());
        }
    }

    @Override
    protected Object invokePluginMethodByName(HybridView view, PluginBase base, PluginData pluginData, String methodName, Object[] params, JsPromptResult jsPromptResult) {
        return invokePluginMethodByNameInner(view, base, methodName, params);
    }

    @Override
    protected Map<Class<?>, PluginBase> getWindowInjectedJSObj(HybridView view) {
        return view.getRDCloudWindow().getInjectedPluginJSObj();
    }

    @Override
    protected Map<Integer, PluginBase> getViewInjectedJSObj(HybridView view) {
        return view.getInjectedPluginJSObj();
    }

}
