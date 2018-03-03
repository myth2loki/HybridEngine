package com.xhrd.mobile.hybridframework.util.http;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * 网络工具类
 * Created by wangqianyu on 15/2/9.
 */
class NetUtil {

    private static final String TAG = "NetUtil";
    private static SSLContext mSSLContext;

    /**
     * 获取Http连接。如果地址是https默认使用接受任何签名。
     * @param url
     * @return
     * @throws IOException
     */
    public static HttpURLConnection getHttpConnection(String url) throws IOException {
        URL u = new URL(url);
        if ("https".equals(u.getProtocol())) {
            if (mSSLContext == null) {
                try {
                    initTrustAllSSL();
                } catch (Exception e) {
                    Log.e(TAG, "init trust any SSL failed.", e);
                }
            }
            return getHttpsConn(url);
        } else {
            return getHttpConn(url);
        }
    }

    /**
     * 获取HttpUrlConnection实例。
     * @param url 请求地址
     * @return HttpUrlConnection实例
     * @throws IOException 网络出错
     */
    private static HttpURLConnection getHttpConn(String url) throws IOException {
        URL u = new URL(url);
        return (HttpURLConnection) u.openConnection();
    }

    /**
     * 获取HttpsUrlConnection实例。
     * @param url 请求地址
     * @return HttpsUrlConnecton实例
     * @throws IOException 网络出错
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    private static HttpsURLConnection getHttpsConn(String url) throws IOException {
        HttpsURLConnection httpsConn = (HttpsURLConnection) getHttpConn(url);
        httpsConn.setSSLSocketFactory(mSSLContext.getSocketFactory());
        httpsConn.setHostnameVerifier(new TrustAnyHostnameVerifer());
        return httpsConn;
    }

    /**
     * 通过证书初始化SSL。
     * @param ksInputStream 服务端信任的证书输入流
     * @param ksPwd 服务端信任的证书密码
     * @param tsInputStream 客户端信任的证书输入流
     * @param tsPwd 客户端信任的证书密码
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     * @throws IOException
     * @throws CertificateException
     */
    public static void initSSL(InputStream ksInputStream, InputStream tsInputStream, char[] ksPwd, char[] tsPwd) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException,
            KeyManagementException, IOException, CertificateException, NoSuchProviderException {
        // 服务器端需要验证的客户端证书
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(ksInputStream, ksPwd);

        // 客户端信任的服务器端证书
        KeyStore trustKeyStore = KeyStore.getInstance("bks");
        trustKeyStore.load(tsInputStream, ksPwd);

        KeyManagerFactory keyManager = KeyManagerFactory.getInstance("X509");
        TrustManagerFactory trustManager = TrustManagerFactory.getInstance("X509");
        keyManager.init(keyStore, tsPwd);
        trustManager.init(trustKeyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
        mSSLContext = sslContext;
    }

    public static void initTrustAllSSL() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = { new X509TrustAllManager() };
        sslContext.init(null, tm, null);
        mSSLContext = sslContext;
    }

    /**
     * 域名验证，信任所有域名。
     */
    private static class TrustAnyHostnameVerifer implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
