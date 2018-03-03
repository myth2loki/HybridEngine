package com.xhrd.mobile.hybridframework.util.http;

/**
 * Created by wangqianyu on 15/2/10.
 */
public class HttpManagerFactory {

    public static IHttpManager getHttpManager() {
        return HttpUCManager.getInstance();
    }
}
