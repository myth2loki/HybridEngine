package com.xhrd.mobile.hybrid.util.jsbuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxinliang on 16/1/7.
 */
public class JsObjectBuilder {

    private final StringBuffer js = new StringBuffer();
    private final String funJs = "%s:function(){" +
                                    "%s" +
                                    "var ret = exec('RDCloud://%s/%s/'+this.hostId, ['%s',this.id], %s, %s);" +
                                    "%s" +
                                "},";

    private Map<String, Object> properties = new HashMap<String, Object>();
    private Map<String, JsFunctionProperty> functions = new HashMap<String, JsFunctionProperty>();

    /**
     * 构造
     * @param id
     * @param hostId
     */
    public JsObjectBuilder(int id, int hostId){
        addProperty("id", id);
        addProperty("hostId", hostId);
    }

    /**
     * 创建JsObjectBuilder
     * @param id
     * @param hostId
     * @return
     */
    public synchronized static final JsObjectBuilder create(int id, int hostId) {
        return new JsObjectBuilder(id, hostId);
    }

    /**
     * 添加属性，如果属性已添加则覆盖原值（key作为是否添加的依据）
     * @param key
     * @param value
     */
    public void addProperty(String key, Number value) {
        properties.put(key, value);
    }

    /**
     * 添加属性，如果属性已添加则覆盖原值（key作为是否添加的依据）
     * @param key
     * @param value
     */
    public void addProperty(String key, String value) {
        addProperty(key, value, false);
    }

    /**
     * 添加属性，如果属性已添加则覆盖原值（key作为是否添加的依据）
     * @param key
     * @param value
     * @param jsObject 是否为js对象
     */
    public void addProperty(String key, String value, boolean jsObject) {
        if (jsObject){
            properties.put(key, value);
        }else {
            properties.put(key, "'" + value + "'");
        }
    }


    /**
     * 添加方法，如果方法已添加则覆盖原值（name作为是否添加的依据）
     * @param name
     * @param superFunctionName
     * @param className
     * @param hasReturn
     * @param convertJS
     * @param execPre
     * @param execPost
     */
    public void addFunction(String name, String superFunctionName,String className, boolean hasReturn, boolean convertJS, String execPre, String execPost) {
        JsFunctionProperty functionProperty = new JsFunctionProperty(name, superFunctionName, className, hasReturn, convertJS, execPre, execPost);
        functions.put(name, functionProperty);
    }

    /**
     * 添加方法，如果方法已添加则覆盖原值（name作为是否添加的依据）
     * @param name
     * @param superFunctionName
     * @param className
     */
    public void addFunction(String name, String superFunctionName,String className) {
        addFunction(name, superFunctionName, className, false, false, "", "");
    }

    /**
     * 添加方法，如果方法已添加则覆盖原值（name作为是否添加的依据）
     * @param name
     * @param superFunctionName
     * @param className
     * @param execPre js方法执行前的代码块
     */
    public void addFunction(String name, String superFunctionName,String className, String execPre) {
        addFunction(name, superFunctionName, className, false, false, execPre,"");
    }

    /**
     * 添加方法，如果方法已添加则覆盖原值（name作为是否添加的依据）
     * @param name
     * @param superFunctionName
     * @param className
     * @param execPre js方法执行前的代码块
     * @param execPost js方法执行后的代码块，返回值对象为ret
     */
    public void addFunctionWithReturn(String name, String superFunctionName,String className, String execPre, String execPost) {
        addFunction(name, superFunctionName, className, true, false, execPre, execPost);
    }

    /**
     * 添加方法，如果方法已添加则覆盖原值（name作为是否添加的依据）
     * @param name
     * @param superFunctionName
     * @param className
     * @param execPre js方法执行前的代码块
     * @param execPost js方法执行后的代码块，返回值对象为ret
     */
    public void addFunctionWithConvertReturn(String name, String superFunctionName,String className, String execPre, String execPost) {
        addFunction(name, superFunctionName, className, true, true, execPre, execPost);
    }

    /**
     * 根据已添加的属性与方法，生成相应的js对象
     * @return
     */
    public String build() {
        js.append("{");

        // 生成属性
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            js.append(entry.getKey()).append(":").append(entry.getValue().toString()).append(",");
        }

        // 生成方法
        for (Map.Entry<String, JsFunctionProperty> entry : functions.entrySet()){
            JsFunctionProperty jfp = entry.getValue();
            String fun = String.format(funJs, jfp.name, jfp.execPre,jfp.className, jfp.superFunctionName, jfp.name, jfp.hasReturn, jfp.convertJS, jfp.execPost);
            js.append(fun);
        }

        // 去除末尾的逗号
        if (js.length() > 0 && js.charAt(js.length() - 1) == ',') {
            js.deleteCharAt(js.length() - 1);
        }

        js.append("}");
        return js.toString();
    }

}
