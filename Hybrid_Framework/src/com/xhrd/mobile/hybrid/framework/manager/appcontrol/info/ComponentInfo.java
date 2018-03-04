package com.xhrd.mobile.hybrid.framework.manager.appcontrol.info;

/**
 * Created by az on 15/12/17.
 */
public  class ComponentInfo {
    public String name;
    public String className;
    public String description;
    public String version;
    public String bgcolor;
    public String url;

    public ComponentInfo(String name, String className, String description, String version, String bgcolor, String url) {
        this.name = name;
        this.className = className;
        this.description = description;
        this.version = version;
        this.bgcolor = bgcolor;
        this.url = url;
    }
}
