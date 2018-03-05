package com.xhrd.mobile.hybrid.framework;

import android.util.Pair;

import com.xhrd.mobile.hybrid.engine.HybridScript;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqianyu on 15/5/30.
 */
public class JavascriptGenerator {
    private int mId = -1;
    private String mClassName;
    private List<Pair<String, Object>> mPropList = new ArrayList<Pair<String, Object>>();
    private List<Pair<String, String>> mFuncList = new ArrayList<Pair<String, String>>();

    public JavascriptGenerator(int id, String className) {
        this.mId = id;
        addProperty("id", id);
        this.mClassName = className;
    }

    public void addProperty(String key, Object value) {
        mPropList.add(new Pair<String, Object>(key, value));
    }

    //"exec('RDCloud://com.xhrd.mobile.hybridframework.framework.Manager.audio.AudioManager/playerCall/'+this.id, 'play,'+this.id, false, false);"
    public void addMethod(String funcName, boolean hasReturn, boolean convertReturn) {
        String first = funcName;
        StringBuffer second = new StringBuffer();
        if (convertReturn) {
            second.append("try{var ret;");
            second.append(String.format("ret=exec('%s://%s/%s/'+this.id, %s, %s, %s);", HybridScript.JS_SCHEMA, mClassName, funcName, "[funcName," + mId + "]", hasReturn, convertReturn));
            second.append("return eval('('+ret+')');");
            second.append("}catch(e){").append("return null;").append("}");
        } else if (hasReturn) {
            second.append(String.format("return exec('%s://%s/%s/'+this.id, %s, %s, %s);", HybridScript.JS_SCHEMA, mClassName, funcName, "funcName," + mId, hasReturn, convertReturn));
        } else {
            second.append(String.format("exec('%s://%s/%s/'+this.id, %s, %s, %s);", HybridScript.JS_SCHEMA, mClassName, funcName, "funcName," + mId, hasReturn, convertReturn));
        }
        Pair<String, String> pair = new Pair<String, String>(first, second.toString());
        mFuncList.add(pair);
    }

    /**
     * 生成js obj代码。
     * @return
     */
    public String generate() {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        //property
        for (Pair<String, Object> prop : mPropList) {
            sb.append(prop.first).append(':');
            if (prop.second.getClass().isAssignableFrom(String.class)) {
                sb.append("'").append(String.valueOf(prop.second));
            } else {
                sb.append(String.valueOf(prop.second));
            }
            sb.append("',");
        }
        if (sb.charAt(sb.length() - 1) == ',' && mFuncList.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        //function
        for (Pair<String, String> func : mFuncList) {
            sb.append(func.first).append(":function(){");
            sb.append(func.second).append(';');
            sb.append("},");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append('}');
        return sb.toString();
    }
}
