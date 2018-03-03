package com.xhrd.mobile.hybridframework.framework.Manager.http;

/**
 * Created by lilong on 15/5/31.
 */
public class Fileinfo {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    String   name;
   String  path ;
   String  fileName;


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    String  mimeType;




}
