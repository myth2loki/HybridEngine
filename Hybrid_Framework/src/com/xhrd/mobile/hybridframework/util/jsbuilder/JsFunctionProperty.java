package com.xhrd.mobile.hybridframework.util.jsbuilder;

/**
 * Created by maxinliang on 16/1/7.
 */
public class JsFunctionProperty {

    public String name;
    public String superFunctionName;
    public String className;
    public boolean hasReturn;
    public boolean convertJS;

    public String execPre;
    public String execPost;

    public JsFunctionProperty(String name, String superFunctionName, String className, boolean hasReturn, boolean convertJS, String execPre, String execPost) {
        this.name = name;
        this.superFunctionName = superFunctionName;
        this.className = className;
        this.hasReturn = hasReturn;
        this.convertJS = convertJS;
        this.execPre = execPre;
        this.execPost = execPost;
    }

    public String getName() {
        return name;
    }

    public String getSuperFunctionName() {
        return superFunctionName;
    }

    public String getClassName() {
        return className;
    }

    public boolean isHasReturn() {
        return hasReturn;
    }

    public boolean isConvertJS() {
        return convertJS;
    }

    public String getExecPre() {
        return execPre;
    }

    public String getExecPost() {
        return execPost;
    }
}
