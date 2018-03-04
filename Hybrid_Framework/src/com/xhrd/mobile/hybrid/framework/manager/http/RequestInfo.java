package com.xhrd.mobile.hybrid.framework.manager.http;

import com.xhrd.mobile.hybrid.engine.RDCloudView;

import java.util.List;

/**
 * Created by lang on 15/8/12.
 */
public class RequestInfo {
    private RequireInfo requireInfo;
    private RequestParams requestParams;
    /**
     * 正在上传的进度
     **/
    private long loadLen;
    /**
     * 上传的总进度
     **/
    private long loadTotal;
    /**
     * 正在上传文件的长度
     **/
    private long loadingCurrent;
    private HttpUtils http;
    private List<Fileinfo> fileUpInfos;
    private RDCloudView view;
    private String callbackError;
    private String callbackSuccess;
    private String callbackprogress;
    private String content;
    private List<ParamInfo> headers;
    private List<ParamInfo> params;
    private String bodyContent;
    /**
     * 前端传入的在json或text.
     * text: 保存原始数据
     * json: 对数据进行处，转化为键值对方式。
     */
    private String bodyType;

    public RequireInfo getRequireInfo() {
        return requireInfo;
    }

    public void setRequireInfo(RequireInfo requireInfo) {
        this.requireInfo = requireInfo;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
    }



    public long getLoadTotal() {
        return loadTotal;
    }



    public HttpUtils getHttp() {
        return http;
    }

    public void setHttp(HttpUtils http) {
        this.http = http;
    }

    public List<Fileinfo> getFileUpInfos() {
        return fileUpInfos;
    }

    public void setFileUpInfos(List<Fileinfo> fileUpInfos) {
        this.fileUpInfos = fileUpInfos;
    }

    public String getCallbackError() {
        return callbackError;
    }

    public void setCallbackError(String callbackError) {
        this.callbackError = callbackError;
    }

    public String getCallbackSuccess() {
        return callbackSuccess;
    }

    public void setCallbackSuccess(String callbackSuccess) {
        this.callbackSuccess = callbackSuccess;
    }


    public void setCallbackprogress(String callbackprogress) {
        this.callbackprogress = callbackprogress;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCallbackprogress() {
        return callbackprogress;
    }

    public String getContent() {
        return content;
    }

    public void setHeaders(List<ParamInfo> headers) {
        this.headers = headers;
    }

    public List<ParamInfo> getHeaders() {
        return headers;
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params;
    }

    public List<ParamInfo> getParams() {
        return params;
    }

    public long getLoadLen() {
        return loadLen;
    }

    public void setLoadLen(long loadLen) {
        this.loadLen = loadLen;
    }

    public void setLoadTotal(long loadTotal) {
        this.loadTotal = loadTotal;
    }

    public long getLoadingCurrent() {
        return loadingCurrent;
    }

    public void setLoadingCurrent(long loadingCurrent) {
        this.loadingCurrent = loadingCurrent;
    }

    public RDCloudView getCloudView() {
        return view;
    }

    public void setCloudView(RDCloudView view) {
        this.view = view;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public String getBodyType(){
        return this.bodyType;
    }

    public void setBodyType(String bodyType){
        this.bodyType = bodyType;
    }
}
