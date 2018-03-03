package com.xhrd.mobile.hybrid.framework.Manager.appcontrol.info;

/**
 * Created by az on 15/12/16.
 */
public class AppInfo {
    public long id;
    public String name;
    public String description;
    public AppAuthorInfo author;
    public String version;
    public String bgcolor;
    public String entry;
    public String appkey;

    public AppInfo(long id, String name, String description, AppAuthorInfo author, String version, String bgcolor, String entry, String appkey) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
        this.version = version;
        this.bgcolor = bgcolor;
        this.entry = entry;
        this.appkey = appkey;
    }
}
