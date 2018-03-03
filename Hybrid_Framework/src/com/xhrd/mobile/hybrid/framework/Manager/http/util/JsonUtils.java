package com.xhrd.mobile.hybrid.framework.Manager.http.util;

import android.text.TextUtils;

import com.xhrd.mobile.hybrid.framework.Manager.http.Fileinfo;
import com.xhrd.mobile.hybrid.framework.Manager.http.ParamInfo;
import com.xhrd.mobile.hybrid.framework.Manager.http.RequireInfo;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lilong on 15/7/11.
 */
public class JsonUtils {
    /**
     *
     * 需要在getHttpsParam方法后调用 要不然值为空
     * */
    //public static List<Fileinfo> fileUpInfos;

    /**
     * 获取所有的请求参数
     */
    public static RequireInfo getParam(String json) {
        RequireInfo require = new RequireInfo();
        JSONObject jsonObj = null;
        try {
            String method = "";//可选
            String url = "";//必选
            String dataType = "";//必选
            String timeout = "";//可选
            String offline = "";//可选
            String expires = "";//可选
            String HTTPHeader = "";//可选
            String certificate = "";//可选
            String form = "";//可选
            String body = "";//可选

            jsonObj = new JSONObject(json);
            method = jsonObj.has("method") ? jsonObj.optString("method") : "get";
            timeout = jsonObj.has("timeout") ? jsonObj.optString("timeout") : "0";
            offline = jsonObj.has("offline") ? jsonObj.optString("offline") : "undefined";
            expires = jsonObj.has("expires") ? jsonObj.optString("expires") : "0";
            HTTPHeader = jsonObj.has("HTTPHeader") ? jsonObj.optString("HTTPHeader") : "";
            certificate = jsonObj.has("certificate") ? jsonObj.optString("certificate") : "";
            form = jsonObj.has("form") ? jsonObj.optString("form") : "";
            body = jsonObj.has("body") ? jsonObj.optString("body") : "";
            url = jsonObj.optString("url");//必选
            dataType = jsonObj.optString("dataType");//必选

            require.setUrl(url);
            require.setTimeout(Integer.valueOf(timeout));
            require.setOffline(offline);
            require.setMethod(method);
            require.setExpires(Integer.valueOf(expires));
            require.setBody(body);
            require.setCertificate(certificate);
            require.setForm(form);
            require.setDataType(dataType);
            require.setHTTPHeader(HTTPHeader);

            require.setBodyType(jsonObj.optString("bodyType"));

            if (!TextUtils.isEmpty(certificate)) {
                JSONObject jsonCertificate = new JSONObject(certificate);
                require.setCertificatePath(jsonCertificate.optString("path"));
                require.setCertificatePassword(jsonCertificate.optString("password"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return require;
    }

    /**
     * 获取请求参数和上传文件信息
     */
    public static List<ParamInfo> getForms(String json) {

//        form:{values:{foo:'bar',wang:'jun’},
//            files:[{name:'image',path:'http://img.8794.cn/2015/0520/20150520030121299.jpg',fileName:'hehe',mimeType:'image/jpeg'},{name:'image',path:'http://img.8794.cn/2015/0520/20150520030121299.jpg'}]
//        }};
        List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();
        if (TextUtils.isEmpty(json))
            return paramInfoList;


        JSONObject jsonObj = null;

        try {
            jsonObj = new JSONObject(json);
            //获取请求参数
            String values = jsonObj.optString("values");
            if (!TextUtils.isEmpty(values)) {
                JSONObject jsonValues = new JSONObject(values);
                Iterator keys = jsonValues.keys();

                while (keys.hasNext()) {
                    ParamInfo paramInfo = new ParamInfo();
                    String key = keys.next() + "";
                    paramInfo.setKey(key);
                    paramInfo.setValue(jsonValues.optString(key));
                    paramInfoList.add(paramInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paramInfoList;
    }

    /**
     * 获取请求参数和上传文件信息
     */
    public static List<Fileinfo> getFileInfos(String json) {
        List<Fileinfo> fileInfos = new ArrayList<Fileinfo>();
        if (TextUtils.isEmpty(json))
            return fileInfos;
        try {
            JSONObject jsonObj = new JSONObject(json);

            JSONArray array = null;
            array = jsonObj.optJSONArray("files");
            //获取上传文件信息
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObjfile = ((JSONObject) array.opt(i));
                    String name = null;
                    String path = null;
                    String filename = null;
                    String mimeType = "image/jpeg";
                    if (!jsonObjfile.isNull("fileName")) {
                        filename = jsonObjfile.optString("fileName");
                    }
                    if (!jsonObjfile.isNull("mimeType")) {
                        if (!TextUtils.isEmpty(jsonObjfile.optString("mimeType")))
                            mimeType = jsonObjfile.optString("mimeType");
                    }
                    if (!jsonObjfile.isNull("name")) {
                        name = jsonObjfile.optString("name");
                        path = jsonObjfile.optString("path");
                        Fileinfo info = new Fileinfo();
                        info.setFileName(filename);
                        info.setMimeType(mimeType);
                        info.setName(name);
                        info.setPath(path);
                        fileInfos.add(info);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fileInfos;
    }


    /**
     * 获取body信息
     */
    public static List<ParamInfo> getBodys(String json) {
        List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();
        if (TextUtils.isEmpty(json))
            return paramInfoList;
        //判断是不是json

        JSONObject jsonBody;
        try {
            jsonBody = new JSONObject(json);
            //获取请求参数
            Iterator keys = jsonBody.keys();
            while (keys.hasNext()) {
                ParamInfo paramInfo = new ParamInfo();
                String key = keys.next() + "";
                paramInfo.setKey(key);
                paramInfo.setValue(jsonBody.optString(key));
                paramInfoList.add(paramInfo);
            }
        } catch (JSONException e) {
            //不是json格式
            ParamInfo paramInfo = new ParamInfo();
            paramInfo.setKey("body");
            paramInfo.setValue(json);
            paramInfoList.add(paramInfo);

            e.printStackTrace();
        }
        return paramInfoList;
    }

    /**
     * 获取头信息
     */
    public static List<ParamInfo> getHeaders(String header) {
        List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();
        if (TextUtils.isEmpty(header))
            return paramInfoList;
        //判断是不是json

        JSONObject jsonBody;
        try {
            jsonBody = new JSONObject(header);
            //获取请求参数
            Iterator keys = jsonBody.keys();
            while (keys.hasNext()) {
                ParamInfo paramInfo = new ParamInfo();
                String key = keys.next() + "";
                paramInfo.setKey(key);
                paramInfo.setValue(jsonBody.optString(key));
                paramInfoList.add(paramInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return paramInfoList;
    }

    /**
     * 获取请求信息，包括文件信息
     *
     * @param form
     * @param body
     * @return
     */
    public static ParamEntity getParamsEntity(String form, String body) {
        ParamEntity paramEntity = null;

        List<ParamInfo> paramInfos = null;
        List<Fileinfo> fileinfos = null;

        List<ParamInfo> bodyInfos = getBodys(body);


        if (bodyInfos != null && bodyInfos.size() > 0) {
            paramInfos = bodyInfos;
        } else if (getForms(form).size() > 0 || getFileInfos(form).size() > 0) {
            paramInfos = getForms(form);
            fileinfos = getFileInfos(form);
        }
        paramEntity = new ParamEntity();
        paramEntity.setParams(paramInfos);
        paramEntity.setFileInfos(fileinfos);
        paramEntity.setForm(form);
        paramEntity.setBody(body);
        return paramEntity;
    }

    public static JSONObject getResponseHeaders(Header[] headers) {
        JSONObject jsonObject = null;
        try {
            if (headers != null && headers.length > 0) {
                jsonObject = new JSONObject();
                for (Header header : headers) {
                    jsonObject.put(header.getName(), header.getValue());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
