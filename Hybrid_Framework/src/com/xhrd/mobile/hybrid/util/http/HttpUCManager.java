package com.xhrd.mobile.hybrid.util.http;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.xhrd.mobile.hybrid.Config;
import com.xhrd.mobile.hybrid.util.FileUtil;
import com.xhrd.mobile.hybrid.util.SystemUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * this is based on HttpUrlConnection.
 * 
 * @author myth2loki
 * 
 */
class HttpUCManager implements IHttpManager {
	
	public static final String TAG = HttpUCManager.class.getSimpleName();
	private int mConnTimeout = IHttpManager.CONNECTION_TIMEOUT;
	private int mReadTimeout = IHttpManager.READ_TIMEOUT;

	private static HttpUCManager mInstance = new HttpUCManager();

	public static HttpUCManager getInstance(){
		return mInstance;
	}

	protected HttpUCManager() {
		// Work around pre-Froyo bugs in HTTP connection reuse.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}



	private HttpURLConnection execute(String url, Map<String, String> params, Map<String, String> headers,
			List<UploadEntity> files, String method) throws IOException {
		StringBuffer encodedParams = new StringBuffer();
		HttpURLConnection conn = null;

		// params
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (encodedParams.length() > 0) {
					encodedParams.append("&");
				}
				encodedParams.append(URLEncoder.encode(entry.getKey(), CHARSET_ENCODING)).append("=")
						.append(URLEncoder.encode(entry.getValue(), CHARSET_ENCODING));
			}
		}

		// 转大写字母
		method = method.toUpperCase();

		if (POST_METHOD.equals(method)) {
			conn = NetUtil.getHttpConnection(url);
			conn.addRequestProperty("Accept", "application/json");
			conn.addRequestProperty("Accept-Charset", CHARSET_ENCODING);
			conn.addRequestProperty("Accept-Encoding", ACCEPT_ENCODING);
			conn.addRequestProperty("Accept-Language", CHARSET_LANGUAGE);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setConnectTimeout(mConnTimeout);
			conn.setReadTimeout(mReadTimeout);
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setChunkedStreamingMode(0);

			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			if (files == null || files.size() == 0) {
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.connect();
				OutputStream os = null;
				try {
					os = conn.getOutputStream();
					os.write(encodedParams.toString().getBytes());
				} finally {
					if (os != null) {
						os.flush();
					}
				}
			} else {
				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
				conn.connect();
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(conn.getOutputStream());
					StringBuffer paramSb = new StringBuffer();
					for (Map.Entry<String, String> param : params.entrySet()) {
						paramSb.append("--").append(BOUNDARY).append("\r\n");
						paramSb.append(String.format("Content-Disposition: form-data; name=\"%s\"\r\n\r\n%s\r\n",
								param.getKey(), param.getValue()));
						paramSb.append("--").append(BOUNDARY).append("\r\n");
					}
					bos.write(paramSb.toString().getBytes());

					for (UploadEntity ue : files) {
						String name = ue.name;
						File file = ue.file;
						if (!file.exists()) {
							continue;
						}
						StringBuffer fileSb = new StringBuffer();
						String filename = file.getName();
//						if (filename.length() > FILE_NAME_MAX_LENGTH) {
//							StringBuffer temp = new StringBuffer(filename);
//							filename = temp.reverse().substring(0, 50);
//							filename = new StringBuffer(filename).reverse().toString();
//						}
						Log.d(TAG, String.format("file: uploading %s.", filename));
						fileSb.append("--").append(BOUNDARY).append("\r\n");
						fileSb.append(String.format(
								"Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n", name, filename));
						fileSb.append(String.format("Content-Type: %s \r\n", FileUtil.getMimeType(file)));
						fileSb.append("\r\n");
						bos.write(fileSb.toString().getBytes());
						BufferedInputStream bis = null;
						try {
							bis = new BufferedInputStream(new FileInputStream(file));
							byte[] buffer = new byte[8192];
							int count = 0;
							while ((count = bis.read(buffer)) > -1) {
								bos.write(buffer, 0, count);
							}
						} finally {
							if (bis != null) {
								bis.close();
							}
						}
						bos.write("\r\n".getBytes());
						Log.d(TAG, String.format("file: %s is uploaded.", filename));
					}
					bos.write(("--" + BOUNDARY + "--\r\n").getBytes());
					bos.write("\r\n".getBytes());
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				}
			}

		} else if (GET_METHOD.equals(method)) {
			URL u = null;
			if (SystemUtil.hasHoneycomb()) {
				StringBuilder newUrlSB = new StringBuilder();
				Uri uri = Uri.parse(url);
				//append scheme
				newUrlSB.append(uri.getScheme()).append("://");
				//append host
				newUrlSB.append(uri.getHost());
				//append port
				if (uri.getPort() > -1) {
					newUrlSB.append(':').append(uri.getPort());
				}
				//append all paths
				for (String frg : uri.getPathSegments()) {
					newUrlSB.append('/').append(URLEncoder.encode(frg, CHARSET_ENCODING));
				}
				//append all query parameters
				Set<String> qpNames = uri.getQueryParameterNames();
				if (qpNames.size() > 0) {
					newUrlSB.append('?');
				}
				for (String que : qpNames) {
					newUrlSB.append(URLEncoder.encode(que, CHARSET_ENCODING))
					.append('=').append(URLEncoder.encode(uri.getQueryParameter(que), CHARSET_ENCODING)).append('&');
				}
				//delete last "&" if it exists
				if (newUrlSB.charAt(newUrlSB.length() - 1) == '&') {
					newUrlSB.deleteCharAt(newUrlSB.length() - 1);
				}
				//append ? or & according to current case
				if (encodedParams.length() > 0) {
					if (newUrlSB.indexOf("?") > -1) {
						newUrlSB.append('&');
					} else {
						newUrlSB.append('?');
					}
					newUrlSB.append(encodedParams);
				}
				
				url = newUrlSB.toString();
			} else {
				String[] tempStrs = url.split("\\?");
				if (tempStrs.length > 1) {
					//must encode parameters of URL.
					url = tempStrs[1];
					String regx = "[&]?([^=&]+)=([^=&]+)";
					Pattern p = Pattern.compile(regx);
					Matcher m = p.matcher(tempStrs[1]);
					StringBuilder encodedParamsOnUrl = new StringBuilder();
					while (m.find()) {
						encodedParamsOnUrl.append(URLEncoder.encode(m.group(1), CHARSET_ENCODING)).append('=')
						.append(URLEncoder.encode(m.group(2), CHARSET_ENCODING)).append('&');
					}
					if (encodedParamsOnUrl.length() > 0) {
						encodedParamsOnUrl.deleteCharAt(encodedParamsOnUrl.length() - 1);
					}
					if (encodedParams.length() > 0) {
						url = tempStrs[0] + "?" + encodedParamsOnUrl.toString() + "&" + encodedParams.toString();
					} else {
						url = tempStrs[0] + "?" + encodedParamsOnUrl.toString();
					}
				} else if (encodedParams.length() > 0) {
					url = tempStrs[0] + "?" + encodedParams.toString();
				}
			}
			
			if (Config.DEBUG) {
				Log.d(TAG, "full url-------->" + url);
			}
			
			conn = NetUtil.getHttpConnection(url);
			conn.addRequestProperty("Accept", "application/json");
			conn.addRequestProperty("Accept-Charset", CHARSET_ENCODING);
			conn.addRequestProperty("Accept-Encoding", ACCEPT_ENCODING);
            conn.addRequestProperty("Accept-Language", CHARSET_LANGUAGE);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			conn.setConnectTimeout(mConnTimeout);
			conn.setReadTimeout(mReadTimeout);
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setChunkedStreamingMode(0);
			conn.connect();
		} else {
			throw new IllegalArgumentException(String.format("illegal arugment: %s", method));
		}
		return conn;
	}

	@Override
	public HttpResult sendRequest(String url, Map<String, String> params, Map<String, String> headers,
			List<UploadEntity> files, String method) throws IOException {
		HttpURLConnection conn = execute(url, params, headers, files, method);
		HttpResult result = new HttpResult();
		result.conn = conn;
		result.responseCode = conn.getResponseCode();
		result.responseMessage = conn.getResponseMessage();
		result.headers = conn.getHeaderFields();
		result.contentEncoding = ConnectionUtil.getHeaderField(conn, "Content-Encoding");
		result.contentType = ConnectionUtil.getHeaderField(conn, "Content-Type");
		try {
			result.contentLength = Long.parseLong(ConnectionUtil.getHeaderField(conn, "Content-Length"));
		} catch (Exception e) {
			Log.e(TAG,
					String.format("can not convert Content-Length(%s) to number.",
							ConnectionUtil.getHeaderField(conn, "Content-Length")));
			result.contentLength = -1;
		}
		if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
			Log.d(TAG, "response code-----------> " + result.getResponseCode());
			Log.d(TAG, "response content encoding-----------> " + result.contentEncoding);
			Log.d(TAG, "response content type-----------> " + result.contentType);
		} else {
			Log.e(TAG, "response code-----------> " + result.getResponseCode());
			Log.e(TAG, "response content encoding-----------> " + result.contentEncoding);
			Log.e(TAG, "response content type-----------> " + result.contentType);
		}
		if (result.responseCode == HttpURLConnection.HTTP_OK || result.responseCode == HttpURLConnection.HTTP_PARTIAL) {
			if (result.contentEncoding != null && ACCEPT_ENCODING.equals(result.contentEncoding.toLowerCase())) {
				result.inputStream = new GZIPInputStream(conn.getInputStream());
			} else {
				result.inputStream = conn.getInputStream();
			}
		} else {
			if (result.contentEncoding != null && ACCEPT_ENCODING.equals(result.contentEncoding.toLowerCase())) {
				result.errorStream = new GZIPInputStream(conn.getErrorStream());
			} else {
				result.errorStream = conn.getErrorStream();
			}
		}
		return result;
	}

	@Override
    public void setConnectionTimeout(int timeout) {
		mConnTimeout = timeout;
	}
	
	@Override
    public void setReadTimeout(int timeout) {
		mReadTimeout = timeout;
	}
}
