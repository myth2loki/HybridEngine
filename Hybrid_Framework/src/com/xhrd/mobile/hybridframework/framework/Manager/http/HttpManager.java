package com.xhrd.mobile.hybridframework.framework.Manager.http;

import android.text.TextUtils;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.engine.RDCloudView;
import com.xhrd.mobile.hybridframework.engine.HybridActivity;
import com.xhrd.mobile.hybridframework.framework.PluginBase;
import com.xhrd.mobile.hybridframework.framework.Manager.ResManager;
import com.xhrd.mobile.hybridframework.framework.Manager.http.callback.RequestCallBack;
import com.xhrd.mobile.hybridframework.framework.Manager.http.client.HttpRequest;
import com.xhrd.mobile.hybridframework.framework.Manager.http.exception.HttpException;
import com.xhrd.mobile.hybridframework.framework.Manager.http.util.JsonUtils;
import com.xhrd.mobile.hybridframework.framework.Manager.http.util.ParamEntity;
import com.xhrd.mobile.hybridframework.framework.PluginData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by lilong
 */
public class HttpManager extends PluginBase {

    @Override
    public String getDefaultDomain() {
        return "httpManager";
    }

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("sendRequest");
    }

    @JavascriptFunction
    public void sendRequest(RDCloudView cloudView, String[] params) throws JSONException {
        if (params != null && params.length > 2) {
            RequestInfo requestInfo = new RequestInfo();
            String content = params[0];
            String callbackSuccess = params[1];
            String callbackError = params[2];
            if (params.length > 3) {
                String callbackprogress = params[3];
                requestInfo.setCallbackprogress(callbackprogress);
            }
            requestInfo.setContent(content);
            requestInfo.setCallbackSuccess(callbackSuccess);
            requestInfo.setCallbackError(callbackError);
            requestInfo.setCloudView(cloudView);
            handleRequest(requestInfo);
        }
    }

    /*
    * 发送网络请求
    * **/
    private void doSend(RequestInfo requestInfo) {
        if (requestInfo == null) {
            return;
        }

        RequireInfo require = requestInfo.getRequireInfo();
        if (require == null) {
            return;
        }
        RequestParams requestParams = new RequestParams();
        requestParams.setBodyType(requestInfo.getBodyType());

        HttpUtils http = getHttpUtils(require);
        List<ParamInfo> headers = requestInfo.getHeaders();
        //添加头文件
        if (headers != null && headers.size() > 0) {
            for (int i = 0; i < headers.size(); i++) {
                requestParams.addHeader(headers.get(i).getKey(), headers.get(i).getValue());
            }
        }
        //添加请求参数
        List<ParamInfo> paramInfos = requestInfo.getParams();
        if (paramInfos != null && paramInfos.size() > 0) {
            for (int i = 0; i < paramInfos.size(); i++) {
                requestParams.addBodyParameter(paramInfos.get(i).getKey(), paramInfos.get(i).getValue());
            }
        }
        requestParams.setBodyContent(
                requestInfo.getBodyContent());
        requestInfo.setHttp(http);
        requestInfo.setRequestParams(requestParams);
        MyRequestCallBack myRequestCallBack = new MyRequestCallBack(requestInfo);
        //添加上传文件信息
        if (require.getMethod().equalsIgnoreCase("POST")) {
            upLoad(requestInfo);
        } else if (require.getMethod().equalsIgnoreCase("PUT")) {
            http.send(HttpRequest.HttpMethod.PUT, require.getUrl(), requestParams, myRequestCallBack);
        } else if (require.getMethod().equalsIgnoreCase("DELETE")) {
            http.send(HttpRequest.HttpMethod.DELETE, require.getUrl(), requestParams, myRequestCallBack);
        } else if (require.getMethod().equalsIgnoreCase("HEAD")) {
            http.send(HttpRequest.HttpMethod.HEAD, require.getUrl(), requestParams, myRequestCallBack);
        } else {
            http.send(HttpRequest.HttpMethod.GET, require.getUrl(), requestParams, myRequestCallBack);
        }
    }

    class MyRequestCallBack extends RequestCallBack {
        private RequestInfo requestInfo;
        private List<Fileinfo> fileUpInfos;

        public MyRequestCallBack(RequestInfo requestInfo) {
            this.requestInfo = requestInfo;
            fileUpInfos = requestInfo.getFileUpInfos();

        }

        @Override
        public void onSuccess(ResponseInfo responseInfo) {
            RequireInfo requireInfo = requestInfo.getRequireInfo();
            String result = (String) responseInfo.result;
            if (result.contains("'")) {
                result = result.replace("'", "\\'");
            }
            JSONObject rePheader = JsonUtils.getResponseHeaders(responseInfo.getAllHeaders());
            saveData(requireInfo, result, rePheader);
            if (requireInfo.getDataType().equals("json")) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    jsCallback(requestInfo.getCloudView(), true, requestInfo.getCallbackSuccess(), (rePheader==null)?"":rePheader, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                jsCallback(requestInfo.getCloudView(), true, requestInfo.getCallbackSuccess(), (rePheader == null) ? "" : rePheader, result);
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            jsCallback(requestInfo.getCloudView(), true, requestInfo.getCallbackError(), error.getExceptionCode(), "", msg);
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            if (isUploading) {
                loadCurrentLen(requestInfo, current, total);
            }
        }
    }


    /**
     * 解析json数据获取相关请求数据，headr,body,以及上传文件等
     **/
    private void handleRequest(RequestInfo requestInfo) {
        if (requestInfo != null) {
            String strResult = requestInfo.getContent();
            RequireInfo requireInfo = JsonUtils.getParam(strResult);
            if (requireInfo != null) {
                requestInfo.setRequireInfo(requireInfo);
                //获取请求体参数
                ParamEntity paramEntity = JsonUtils.getParamsEntity(requireInfo.getForm(), requireInfo.getBody());
                if (paramEntity != null) {
                    List<Fileinfo> fileUpInfos = paramEntity.getFileInfos();
                    List<ParamInfo> params = paramEntity.getParams();
                    //获取头部信息
                    List<ParamInfo> Headers = JsonUtils.getHeaders(requireInfo.getHTTPHeader());
                    //获取缓存数据
                    HttpInfo httpInfo = new HttpDao().queryControlById(requireInfo.getUrl(), requireInfo.getOffline());
                    String cacheData = httpInfo.getJson();
                    String header = httpInfo.getHeader();
                    JSONObject jsonObjectHeader = null;
                    try {
                        if (!TextUtils.isEmpty(header))
                            jsonObjectHeader = new JSONObject(header);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //如果缓存数据为空
                    requestInfo.setFileUpInfos(fileUpInfos);
                    requestInfo.setHeaders(Headers);
                    requestInfo.setParams(params);

                    //设置数据格式，如为"json"对请求的实体数据转为键值对，如为"text"保持原数据格式不变
                    requestInfo.setBodyType(requireInfo.getBodyType());
                    requestInfo.setBodyContent(paramEntity.getBody());
                    if (TextUtils.isEmpty(cacheData)) {
                        doSend(requestInfo);
                    } else {
                        if (requireInfo.getDataType().equals("json")) {
                            try {
                                JSONObject jsonObject = new JSONObject(String.valueOf(cacheData));
                                jsCallback(requestInfo.getCloudView(), true, requestInfo.getCallbackSuccess(), jsonObjectHeader, jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            jsCallback(requestInfo.getCloudView(), true, requestInfo.getCallbackSuccess(), jsonObjectHeader, cacheData);
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加缓存
     * true: 直接调用缓存数据，如果缓存数据不存在，执行ajax请求并离线缓存返回数据；
     * false: 直接请求ajax数据，并把请求到的数据离线缓存；
     * undefined: 直接请求ajax数据，不缓存请求到的数据；
     */
    public void saveData(RequireInfo requireInfo, String data, JSONObject rePheader) {
        HttpInfo httpInfo = new HttpInfo();
        httpInfo.setUrl(requireInfo.getUrl());
        httpInfo.setCachetime(requireInfo.getExpires());
        httpInfo.setJson(data);
        httpInfo.setHeader(rePheader);
        if (requireInfo.getOffline().equals("true") || requireInfo.getOffline().equals("false"))
            new HttpDao().insertData(httpInfo);
    }

    /**
     * 证书请求，有证书添加，无证书不添加。
     */
    private HttpUtils getHttpUtils(RequireInfo requireInfo) {
        HttpUtils http = null;
        String path = requireInfo.getCertificatePath();
        InputStream inputStream = null;
        try {
            if (path != null) {
                if (path.startsWith("/")) {
                    //绝对路径
                    inputStream = new FileInputStream(new File(path));
                } else {
                    inputStream = HybridActivity.getInstance().getAssets().open(path);
                }
                http = new HttpUtils(requireInfo.getTimeout(), null, inputStream
                        , requireInfo.getCertificatePassword().toCharArray());

            } else {
                http = new HttpUtils(requireInfo.getTimeout(), null, null
                        , null);
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return http;

    }

    /**
     * 上传文件
     **/
    private synchronized void upLoad(RequestInfo requestInfo) {
        if (requestInfo == null) {
            return;
        }
        RequireInfo requireInfo = requestInfo.getRequireInfo();
        if (requireInfo == null || !requireInfo.getMethod().equalsIgnoreCase("POST"))
            return;
        HttpUtils http = requestInfo.getHttp();
        if (http != null) {
            RequestParams requestParams = requestInfo.getRequestParams();
            List<Fileinfo> fileUpInfos = requestInfo.getFileUpInfos();
            MyRequestCallBack myRequestCallBack = new MyRequestCallBack(requestInfo);
            if (fileUpInfos != null) {
                for (Fileinfo fileinfo : fileUpInfos) {
                    requestParams.addBodyParameter(fileinfo.getName(), new File(ResManager.getInstance().getPath(fileinfo.getPath())), fileinfo.getFileName(), fileinfo.getMimeType(), null);
                }
            }
            //String key, File file, String fileName, String mimeType, String charst
            http.send(HttpRequest.HttpMethod.POST, requireInfo.getUrl(), requestParams, myRequestCallBack);
        }
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }

    /**
     * 文件正在上传的进度
     **/
    private void loadCurrentLen(RequestInfo requestInfo, long current, long total) {
        requestInfo.setLoadingCurrent(total);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(2);
        long loadLen = requestInfo.getLoadLen();
        long loadTotal = requestInfo.getLoadTotal();

        double dLoadLen = 0.0D;
        double dLoadTotal = 0.0D;
        if (requestInfo.getLoadTotal() != 0) {
            //TODO 整型除法怎么保证输出小数？
            dLoadLen = (double) loadLen;
            dLoadTotal = (double) loadTotal;

        } else {
            dLoadLen = (double) current;
            dLoadTotal = (double) total;

        }
        double percent = Double.parseDouble(nf.format(dLoadLen / dLoadTotal));
        if (requestInfo.getCallbackprogress() != null) {
            jsCallback(requestInfo.getCloudView(), true, requestInfo.getCallbackprogress(), percent);
        }

    }
}
