package com.xhrd.mobile.hybrid.framework.Manager.http;

import java.util.List;

/**
 * Created by lilong on 15/5/30.
 */
public class RequireInfo {


    String  method ;//(*String 类型* )可选 异步请求方法类型 包含GET、POST、PUT、DELETE、 HEAD方法，默认为GET请求。
    String  url;//(*String 类型* )必选 网络请求地址
    String  dataType;//(*String 类型* )必选 服务端的响应类型，包括json,text中的一种。
    int  timeout;//(*Number 类型* )可选 网络请求超时时间
    String  offline; //(*String 类型* )可选 是否直接调用离线数据，包括true,false,undefined。默认为undefined。
    int  expires;//(*String 类型* )可选 离线缓存过期时间
    String HTTPHeader;//请求头(*JSON 类型* )
    String certificate;//(*JSON 类型* )可选 添加证书信息 {path:value,password:value},其中path是证书(.p12证书)的地址,password是提取证书的密码。
    String form;//(*JSON 类型* )可选 表单方式提交数据。form字段与body字段不能同时使用。
    String body;//可选 请求体(以字符串形式提交数据)。body字段与form字段不能同时使用。 //如: body:{field1: "value1", field2: "value2"} (将json对像转为json字符串再赋值给body),或者 body:"value"。

    String certificatePath;//证书地址
    String certificatePassword;//证书密码

    private   List<Fileinfo> fileUpInfos;
    private String bodyType;

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getCertificatePassword() {
        return certificatePassword;
    }

    public void setCertificatePassword(String certificatePassword) {
        this.certificatePassword = certificatePassword;
    }
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getHTTPHeader() {
        return HTTPHeader;
    }

    public void setHTTPHeader(String HTTPHeader) {
        this.HTTPHeader = HTTPHeader;
    }

    public String getOffline() {
        return offline;
    }

    public void setOffline(String offline) {
        this.offline = offline;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<Fileinfo> getFileUpInfos() {
        return fileUpInfos;
    }

    public void setFileUpInfos(List<Fileinfo> fileUpInfos) {
        this.fileUpInfos = fileUpInfos;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getBodyType(){
        return bodyType;
    }
}
