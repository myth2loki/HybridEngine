package com.xhrd.mobile.hybrid.framework.manager.http.util;

import com.xhrd.mobile.hybrid.framework.manager.http.Fileinfo;
import com.xhrd.mobile.hybrid.framework.manager.http.ParamInfo;

import java.util.List;

/**
 * Created by lilong on 15/10/9.
 */
public class ParamEntity {
    private List<ParamInfo> params;
    private List<Fileinfo> fileInfos;
    private String body;
    private String form;

    public ParamEntity() {
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params;
    }

    public void setFileInfos(List<Fileinfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public List<ParamInfo> getParams() {
        return this.params;
    }

    public List<Fileinfo> getFileInfos() {
        return this.fileInfos;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getBody(){
        return body;
    }

    public String getForm(){
        return form;
    }
}