package com.xhrd.mobile.hybridframework.framework.Manager.http.client;


import com.xhrd.mobile.hybridframework.framework.Manager.http.util.HttpLU;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * trust all certs
 */
public class DefaultSSLSocketFactory extends SSLSocketFactory {

    private SSLContext sslContext = SSLContext.getInstance("TLS");

    private static KeyStore trustStore;

//    static {
//        try {
//            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//        } catch (Throwable e) {
//            HttpLU.e(e.getMessage(), e);
//        }
//    }

    private static DefaultSSLSocketFactory instance;

    public static DefaultSSLSocketFactory getSocketFactory(InputStream inputStream,char[] passWord) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        //TODO 以后优化
        //if(instance==null){}

        try {
            instance = new DefaultSSLSocketFactory();

        } catch (Throwable e) {
            HttpLU.e(e.getMessage(), e);
        }

        return instance;
    }


    private DefaultSSLSocketFactory()
            throws UnrecoverableKeyException,
            NoSuchAlgorithmException,
            KeyStoreException,
            KeyManagementException, IOException, CertificateException {
        super(trustStore);


        TrustManager trustAllCerts = new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
            }
        };
        sslContext.init(null, new TrustManager[]{trustAllCerts}, null);

        this.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}

