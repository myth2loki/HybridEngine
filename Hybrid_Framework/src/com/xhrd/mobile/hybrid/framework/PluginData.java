package com.xhrd.mobile.hybrid.framework;

import android.text.TextUtils;
import android.util.Pair;

import com.xhrd.mobile.hybrid.annotation.JavascriptConfig;
import com.xhrd.mobile.hybrid.annotation.JavascriptProperty;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.engine.RDCloudScript;
import com.xhrd.mobile.hybridframework.BuildConfig;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件实体类，负责拼装插件相关js代码。
 */
public class PluginData {
    public static final String JS_OBJECT_BEGIN_FRAMEWORK = RDCloudScript.RD_FRAMEWORK + ".internal.";
    public static final String JS_OBJECT_BEGIN_PLUGIN = RDCloudScript.RD_PLUGIN + ".internal.";

    private static final String js_arg_low = "ja2sa(arguments)";
    private static final String js_staves = "_.";
    private static final String js_point = ".";
    private static final String js_function_begin = ":function(){";
    private static final String js_symbol = "=";
    private static final String js_l_brackets = "(";
    private static final String js_function_end = ");}";
    private static final String js_property_end = ",";
    private static final String js_l_braces = "{";
    //    private static final String js_object_end = "};";
    private static final String js_object_end = "}";

    private String mJSObjectBegin;
    /**
     * javascript调用域名。
     */
    public String mDomain;
    private StringBuffer mJavascript;
    /**
     * 插件对应类的全限定名
     */
    public Class<?> mClass;
    public Constructor<?> mConsturctor;
    public Scope mScope;
    public boolean isAnnotatable;// 是否符合注解模式
    public boolean isUiPlugin;// 是否为UI插件
    public String version;

    protected boolean mGenerated;
    /**
     * 判断是否是内部功能
     */
    private boolean isFramework;

    private List<Pair<String, String>> mPropList = new ArrayList<Pair<String, String>>();
    private List<Pair<String, PluginMethodData>> mMethodList = new ArrayList<Pair<String, PluginMethodData>>();

    PluginData(Constructor<?> constructor, Class<?> clazz, Scope scope, boolean isUi) {
        mConsturctor = constructor;
        mClass = clazz;
        mJSObjectBegin = "";
        mScope = scope;
        mJavascript = new StringBuffer();
        isUiPlugin = isUi;

        if(UIPluginBase.class.isAssignableFrom(clazz)){               //第三方插件
            if(!isUi){// 老版本适配
                try {
                    Object mBase = constructor.newInstance();
                    UIPluginBase base = (UIPluginBase) mBase;
                    isUiPlugin = base.isUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{                                                      //内部插件
            JavascriptConfig javascriptConfig = mClass.getAnnotation(JavascriptConfig.class);
            if (javascriptConfig == null) {
                isAnnotatable = false;
                try {// 老版本获取方式，获取相应属性
                    Object mBase = constructor.newInstance();
                    PluginBase base = (PluginBase) mBase;
                    mScope = base.getScope();
                    mDomain = base.getDefaultDomain();

                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            } else {//通过注解的方法，获取相应属性
                isAnnotatable = true;
                mScope = javascriptConfig.scope();
                mDomain = javascriptConfig.domain();
            }
        }

        addProperty("id", -1);
    }

    /**
     * 根据方法名字获取方法对象信息
     * @param methodName
     * @return
     */
    public PluginMethodData getPluginMethodDataByMethodName(String methodName) {
        if(mMethodList != null && methodName != null) {
            for (Pair<String, PluginMethodData> pair : mMethodList) {
                if (methodName.equals(pair.first)) {
                    return pair.second;
                }
            }
        }
        return null;
    }

    /**
     * 获取所有的方法
     * @return
     */
    public List<Pair<String, PluginMethodData>> getMethodList() {
        return mMethodList;
    }

    /**
     * 生成对象js开头代码。
     *
     * @param domain js对象名
     */
    void genJavaScriptBegin(String domain) {
        mJavascript.append(js_l_braces);
        this.mDomain = domain;
    }

    /**
     * 添加一个js函数。
     *
     * @param method 方法名
     */
    public void addMethod(String method) {
        addMethod(method, null, new String[]{});
    }

    /**
     * 添加一个js函数。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     */
    public void addMethod(String method, String[] permissions, String[] permissionRationales) {
        addMethod(method, permissions, permissionRationales, false);
    }


    /**
     * 添加一个js函数。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由的资源Id
     */
    public void addMethod(String method, String[] permissions, int[] permissionRationaleIds) {

        addMethod(method, permissions, getPermissionRationaleByIds(permissionRationaleIds), false);
    }

    /**
     * 解析请求权限拒绝后的提示信息
     * @param permissionRationaleIds 资源Id数组
     * @return
     */
    private String[] getPermissionRationaleByIds(int[] permissionRationaleIds) {
        String[] permissionRationales = null;
        if(permissionRationaleIds != null) {
            int legnth = permissionRationaleIds.length;
            permissionRationales = new String[legnth];
            if(legnth > 0) {
                for(int i=0; i<legnth; i++) {
                    permissionRationales[i] = HybridActivity.getInstance().getString(permissionRationaleIds[i]);
                }
            }
        }
        return permissionRationales;
    }

    /**
     * 添加一个js函数。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethod(String method, String[] permissions, String[] permissionRationales, boolean isAddWhitelist) {
        addMethod(method, null, false, false, permissions, permissionRationales, isAddWhitelist);
    }

    /**
     * 添加一个js函数。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由的资源Id
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethod(String method, String[] permissions, int[] permissionRationaleIds, boolean isAddWhitelist) {
        addMethod(method, null, false, false, permissions, getPermissionRationaleByIds(permissionRationaleIds), isAddWhitelist);
    }

    /**
     * 添加一个js函数，拥有返回值。
     *
     * @param method 方法名
     */
    public void addMethodWithReturn(String method) {
        addMethodWithReturn(method, null, new String[]{}, false);
    }

    /**
     * 添加一个js函数，拥有返回值。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     */
    public void addMethodWithReturn(String method, String[] permissions, String[] permissionRationales) {
        addMethod(method, null, true, false, permissions, permissionRationales, false);
    }


    /**
     * 添加一个js函数，拥有返回值。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限理由的资源Id
     */
    public void addMethodWithReturn(String method, String[] permissions, int[] permissionRationaleIds) {
        addMethod(method, null, true, false, permissions, getPermissionRationaleByIds(permissionRationaleIds), false);
    }

    /**
     * 添加一个js函数，拥有返回值。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethodWithReturn(String method, String[] permissions, String[] permissionRationales, boolean isAddWhitelist) {
        addMethod(method, null, true, false, permissions, permissionRationales, isAddWhitelist);
    }

    /**
     * 添加一个js函数，拥有返回值。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由的资源Id
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethodWithReturn(String method, String[] permissions, int[] permissionRationaleIds, boolean isAddWhitelist) {
        addMethod(method, null, true, false, permissions, getPermissionRationaleByIds(permissionRationaleIds), isAddWhitelist);
    }

    /**
     * 添加一个js函数，传递boolean参数是否请求加入白名单。
     *
     * @param method 方法名
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethodWithWhitelist (String method,boolean isAddWhitelist) {
        addMethod(method, null, true, false, null, new String[]{}, isAddWhitelist);
    }

    /**
     * 添加一个js函数，传递boolean参数是否请求加入白名单。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethodWithWhitelist(String method, String[] permissions, String[] permissionRationales, boolean isAddWhitelist) {
        addMethod(method, null, true, false, permissions, permissionRationales, isAddWhitelist);
    }

    /**
     * 添加一个js函数，传递boolean参数是否请求加入白名单。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethodWithWhitelist(String method, String[] permissions, int[] permissionRationaleIds, boolean isAddWhitelist) {
        addMethod(method, null, true, false, permissions, getPermissionRationaleByIds(permissionRationaleIds), isAddWhitelist);
    }
    /**
     * 添加一个js函数。支持返回js object。
     *
     * @param method 方法名
     */
    public void addMethodWithConvertReturn(String method) {
        addMethodWithConvertReturn(method, null, new String[]{});
    }

    /**
     * 添加一个js函数。支持返回js object。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     */
    public void addMethodWithConvertReturn(String method, String[] permissions, String[] permissionRationales) {
        addMethodWithConvertReturn(method, permissions, permissionRationales, false);
    }

    /**
     * 添加一个js函数。支持返回js object。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由的资源Id
     */
    public void addMethodWithConvertReturn(String method, String[] permissions, int[] permissionRationaleIds) {
        addMethodWithConvertReturn(method, permissions, getPermissionRationaleByIds(permissionRationaleIds), false);
    }

    /**
     * 添加一个js函数。支持返回js object。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethodWithConvertReturn(String method, String[] permissions, String[] permissionRationales, boolean isAddWhitelist) {
        addMethod(method, null, false, true, permissions, permissionRationales, isAddWhitelist);
    }

    /**
     * 添加一个js函数。支持返回js object。
     *
     * @param method 方法名
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethodWithConvertReturn(String method, String[] permissions, int[] permissionRationaleIds, boolean isAddWhitelist) {
        addMethod(method, null, false, true, permissions, getPermissionRationaleByIds(permissionRationaleIds), isAddWhitelist);
    }

    /**
     * 添加一个js函数，并支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param convertJS 是否转换返回的js。
     */
    public void addMethod(String method, boolean convertJS) {
        addMethod(method, convertJS, null, new String[]{}, false);
    }

    /**
     * 添加一个js函数，并支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param convertJS 是否转换返回的js
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethod(String method, boolean convertJS, String[] permissions, String[] permissionRationales, boolean isAddWhitelist) {
        addMethod(method, null, false, convertJS, permissions, permissionRationales, isAddWhitelist);
    }

    /**
     * 添加一个js函数，并支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param convertJS 是否转换返回的js
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由的资源Id
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethod(String method, boolean convertJS, String[] permissions, int[] permissionRationaleIds, boolean isAddWhitelist) {
        addMethod(method, null, false, convertJS, permissions, getPermissionRationaleByIds(permissionRationaleIds), isAddWhitelist);
    }

    /**
     * 添加一个带参数的js函数，并且支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param preParams 默认参数前缀
     * @param hasReturn 是否有返回
     * @param convertJS 是否转换返回的js。
     */
    public void addMethod(String method, String[] preParams, boolean hasReturn, boolean convertJS) {
        addMethod(method, preParams, hasReturn, convertJS, null, new String[]{});
    }

    /**
     * 添加一个带参数的js函数，并且支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param preParams 默认参数前缀
     * @param hasReturn 是否有返回
     * @param convertJS 是否转换返回的js
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     */
    public void addMethod(String method, String[] preParams, boolean hasReturn, boolean convertJS, String[] permissions, String[] permissionRationales) {
        addMethod(method, preParams, hasReturn, convertJS, permissions, permissionRationales,false);
    }

    /**
     * 添加一个带参数的js函数，并且支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param preParams 默认参数前缀
     * @param hasReturn 是否有返回
     * @param convertJS 是否转换返回的js
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由的资源Id
     */
    public void addMethod(String method, String[] preParams, boolean hasReturn, boolean convertJS, String[] permissions, int[] permissionRationaleIds) {
        addMethod(method, preParams, hasReturn, convertJS, permissions, getPermissionRationaleByIds(permissionRationaleIds),false);
    }

    /**
     * 添加一个带参数的js函数，并且支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param preParams 默认参数前缀
     * @param hasReturn 是否有返回
     * @param convertJS 是否转换返回的js
     * @param permissions 此方法所需的权限
     * @param permissionRationales 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethod(String method, String[] preParams, boolean hasReturn, boolean convertJS, String[] permissions, String[] permissionRationales, boolean isAddWhitelist) {
        PluginMethodData methodData = new PluginMethodData(method, getJsMethod(method, preParams, hasReturn, convertJS), permissions, permissionRationales, isAddWhitelist);
        mMethodList.add(new Pair<String, PluginMethodData>(method, methodData));
    }

    /**
     * 添加一个带参数的js函数，并且支持返回转换的js代码。
     *
     * @param method    js方法名
     * @param preParams 默认参数前缀
     * @param hasReturn 是否有返回
     * @param convertJS 是否转换返回的js
     * @param permissions 此方法所需的权限
     * @param permissionRationaleIds 此方法所需的权限的理由
     * @param isAddWhitelist 是否请求加入白名单
     */
    public void addMethod(String method, String[] preParams, boolean hasReturn, boolean convertJS, String[] permissions, int[] permissionRationaleIds, boolean isAddWhitelist) {
        PluginMethodData methodData = new PluginMethodData(method, getJsMethod(method, preParams, hasReturn, convertJS), permissions, getPermissionRationaleByIds(permissionRationaleIds), isAddWhitelist);
        mMethodList.add(new Pair<String, PluginMethodData>(method, methodData));
    }

    /**
     * 获取js方法
     * @param method
     * @param preParams
     * @param hasReturn
     * @param convertJS
     * @return
     */
    public final String getJsMethod(String method, String[] preParams, boolean hasReturn, boolean convertJS){
        StringBuilder mJavascript = new StringBuilder();
        mJavascript.append(method);
        mJavascript.append(":function(");
        if (preParams != null) {
            for (String p : preParams) {
                mJavascript.append(p).append(',');
            }
            if (preParams.length > 0) {
                mJavascript.deleteCharAt(mJavascript.length() - 1);
            }
        }
        mJavascript.append("){");
        if (convertJS || hasReturn) {
            mJavascript.append("return ");
        }
        mJavascript.append("exec('");
        mClass.getPackage().getName();
        mJavascript.append(getJSSchema()).append("://").append(mClass.getName()).append('/').append(method).append("/'+").append("this.id").append(",");
        mJavascript.append("arguments,");
        mJavascript.append(hasReturn).append(',');
        mJavascript.append(convertJS).append(js_function_end);
        return mJavascript.toString();
    }
    
    private String getJSSchema() {
        return isFramework ? RDCloudScript.JS_SCHEMA_0 : RDCloudScript.JS_SCHEMA_1;
    }

    /**
     * 添加一个属性
     *
     * @param property 属性名
     */
    public void addProperty(String property) {
        StringBuilder mJavascript = new StringBuilder();
        mJavascript.append(property).append(':').append("null");
        mJavascript.append(js_property_end);
        mPropList.add(new Pair<String, String>(property, mJavascript.toString()));
    }

    /**
     * 通过Object添加一个属性
     *
     * @param property 属性名
     * @param value    属性值
     */
    public void addProperty(String property, Object value) {
        if (TextUtils.isEmpty(property)) {
            throw new IllegalArgumentException("property is null/empty");
        }
        StringBuilder mJavascript = new StringBuilder();
        mJavascript.append(property).append(':');

        if (value == null) {
            mJavascript.append(value);
            mJavascript.append(js_property_end);
        } else {
            if (value.getClass() == String.class || value.getClass() == Character.class) {
                mJavascript.append("'");
                mJavascript.append(value);
                mJavascript.append("'");
            } else if (value.getClass().isPrimitive()) {
                mJavascript.append(value);
            } else {
                mJavascript.append(value);
            }
            mJavascript.append(js_property_end);
        }
        mPropList.add(new Pair<String, String>(property, mJavascript.toString()));
    }

    /**
     * 添加一个js对象（非字符串）。
     * @param property 属性名
     * @param object js对象
     */
    public void addObject(String property, String object) {
        if (TextUtils.isEmpty(property)) {
            throw new IllegalArgumentException("property is null/empty");
        }

        StringBuilder mJavascript = new StringBuilder();
        mJavascript.append(property).append(':');
        mJavascript.append(object);
        mJavascript.append(js_property_end);
        mPropList.add(new Pair<String, String>(property, mJavascript.toString()));
    }

    /**
     * 添加一个属性，可以通过‘’单引号来区分数字和字符串。
     * @param property 属性名
     * @param value 属性值
     */
    public void addPlainProperty(String property, String value) {
        if (TextUtils.isEmpty(property)) {
            throw new IllegalArgumentException("property is null/empty");
        }
        StringBuilder mJavascript = new StringBuilder();
        mJavascript.append(property).append(':');

        mJavascript.append(value);
        mJavascript.append(js_property_end);
        mPropList.add(new Pair<String, String>(property, mJavascript.toString()));
    }

    /**
     * 通过枚举添加一个属性
     *
     * @param property 属性名
     * @param e        枚举
     */
    public void addProperty(String property, Enum<?> e) {
        mJavascript.append(property).append(":{");
        for (Enum<?> e1 : e.getClass().getEnumConstants()) {
            mJavascript.append(e1.name());
            mJavascript.append(":");
            mJavascript.append(e1.ordinal());
            mJavascript.append(",");
        }
        if (mJavascript.charAt(mJavascript.length() - 1) == ',') {
            mJavascript.deleteCharAt(mJavascript.length() - 1);
        }
        mJavascript.append("}").append(js_property_end);
    }

    /**
     * 通过注解添加属性-->属性只能是基本数据类型、string类型的定值（枚举类型、需计算的不可以）--》TODO 需优化
     */
    public void addPropertyByAnnotation() {
        // 添加属性
        JavascriptConfig javascriptConfig = mClass.getAnnotation(JavascriptConfig.class);
        if (javascriptConfig != null) {
            JavascriptProperty[] properties = javascriptConfig.properties();
            for (JavascriptProperty property : properties) {
                if (TextUtils.isEmpty(property.value())) {
                    addProperty(property.name());
                } else {
                    StringBuilder mJavascript = new StringBuilder();
                    mJavascript.append(property.name()).append(':');

//                    mJavascript.append("'");
                    mJavascript.append(property.value());
//                    mJavascript.append("'");

                    mJavascript.append(js_property_end);

                    mPropList.add(new Pair<String, String>(property.name(), mJavascript.toString()));
                }
            }
        } else {
            //RDLogUtil.w("property is null/empty");
            //throw new IllegalArgumentException("property is null/empty");
        }

    }


    /**
     * 生成require方法片段。
     * function require(name)
     * ----------------------
     * if({name} = "name") {
     * return exec('RDCloud://com.xx.test.Example/newInstance/-1', '', true, true);
     * }
     */
    public String genPartialRequireMethod() {
        String exec_template = "exec('%s://%s/%s/%d', '', true, true);";
        String simpleName = TextUtils.isEmpty(mDomain) ? mClass.getSimpleName() : mDomain;
        StringBuffer sb = new StringBuffer();
        sb.append("if(name=='").append(simpleName).append("'){");
        if (BuildConfig.DEBUG) {
            sb.append("     console.log('1------->'+name);");
        }
        //使用newInstance表明new
        sb.append("     return ").append(String.format(exec_template, RDCloudScript.JS_SCHEMA, mClass.getName(), "newInstance", -1));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 生成对象js结尾代码，并追加到script
     */
    private void genJavaScriptEnd() {
        mJavascript = new StringBuffer();
        mJavascript.append(js_l_braces);

        for (Pair<String, String> pair : mPropList) {
            mJavascript.append(pair.second);
        }
        for (Pair<String, PluginMethodData> pair : mMethodList) {
            mJavascript.append(pair.second.getJsFun()).append(',');
        }
        if (mJavascript.length() > 0 && mJavascript.charAt(mJavascript.length() - 1) == ',') {
            mJavascript.deleteCharAt(mJavascript.length() - 1);
        }
        mJavascript.append(js_object_end);
    }

    String genJavascript() {
        genJavaScriptEnd();
        return mJavascript.toString();
    }

    public void setFrameworkCall(boolean isFramework) {
        this.isFramework = isFramework;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PluginData that = (PluginData) o;

        if (mClass != null ? !mClass.equals(that.mClass) : that.mClass != null)
            return false;
        if (mConsturctor != null ? !mConsturctor.equals(that.mConsturctor) : that.mConsturctor != null)
            return false;
        if (mDomain != null ? !mDomain.equals(that.mDomain) : that.mDomain != null)
            return false;
        if (mJSObjectBegin != null ? !mJSObjectBegin.equals(that.mJSObjectBegin) : that.mJSObjectBegin != null)
            return false;
        if (mJavascript != null ? !mJavascript.equals(that.mJavascript) : that.mJavascript != null)
            return false;
        if (mScope != that.mScope)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mJSObjectBegin != null ? mJSObjectBegin.hashCode() : 0;
        result = 31 * result + (mDomain != null ? mDomain.hashCode() : 0);
        result = 31 * result + (mJavascript != null ? mJavascript.hashCode() : 0);
        result = 31 * result + (mClass != null ? mClass.hashCode() : 0);
        result = 31 * result + (mConsturctor != null ? mConsturctor.hashCode() : 0);
        result = 31 * result + (mScope != null ? mScope.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    public enum Scope {
        App, Window, New
    }
}
