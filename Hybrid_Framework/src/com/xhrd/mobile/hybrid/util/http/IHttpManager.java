package com.xhrd.mobile.hybrid.util.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 网络接口
 */
public interface IHttpManager {
	int FILE_NAME_MAX_LENGTH = 50;
	String MIME = "application/vnd.android.package-archive";
	String ALLMIME = "*/*";
	String ACCEPT_ENCODING = "gzip";
	String CHARSET_ENCODING = "UTF-8";
	String CHARSET_LANGUAGE = "zh-CN,zh,q=0.8";
	String POST_METHOD = "POST";
	String GET_METHOD = "GET";
	String USER_AGENT = "Android";
	int CONNECTION_TIMEOUT = 20000;
	int READ_TIMEOUT = 30000;
	String BOUNDARY = "----7d4a6d158c9d7d6d8f9a97ds7";

    /**
     * 发起网络请求。
     * @param url 访问地址
     * @param params 访问参数
     * @param headers 访问头
     * @param files 附件
     * @param method 发送方法
     * @return 封装的HttpResult
     * @throws IOException 访问异常
     */
	HttpResult sendRequest(String url, Map<String, String> params, Map<String, String> headers,
						   List<UploadEntity> files, String method) throws IOException;

    /**
     * 设置连接超时时间
     * @param timeout
     */
    void setConnectionTimeout(int timeout);

    /**
     * 设置读取超时时间
     * @param timeout
     */
    void setReadTimeout(int timeout);
}
