package com.xhrd.mobile.hybrid.util.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * X509信任管理器，信任所有证书。
 * Created by wangqianyu on 15/2/9.
 */
public class X509TrustAllManager implements X509TrustManager {
    public X509TrustAllManager() {
        super();
    }

    /*
     * Delegate to the default trust manager.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    /*
     * Delegate to the default trust manager.
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    /*
     * Merely pass this through.
     */
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
