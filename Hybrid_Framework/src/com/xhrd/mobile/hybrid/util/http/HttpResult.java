package com.xhrd.mobile.hybrid.util.http;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * result of HTTP response.
 * 
 * @author myth2loki
 * 
 */
public class HttpResult implements Parcelable {

	private static String TAG = "downloader httpResult";

	HttpURLConnection conn;
	String responseMessage;
	InputStream inputStream, errorStream;
	OutputStream outputStream;
	String contentEncoding, contentType;
	public int responseCode;
	long contentLength;
	String sessionId;
	public String content;
    boolean fromCache;
	/**
	 * returned headers from server
	 */
	Map<String, List<String>> headers;

	/**
	 * release all resources.
	 */
	public void close() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.d(TAG, "", e);
			}
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				Log.d(TAG, "", e);
			}
		}
		if (conn != null) {
			conn.disconnect();
		}
	}

	/**
	 * get response code of HTTP.
	 * 
	 * @return
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * get input stream of HTTP.
	 * 
	 * @return input stream of HTTP, null if response code is 200.
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * get error stream of HTTP.
	 * 
	 * @return error stream of HTTP, null if response code is not 200.
	 */
	public InputStream getErrorStream() {
		return errorStream;
	}

	/**
	 * get output stream of HTTP.
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public long getContentLength() {
		return contentLength;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	

	/**
	 * get string of input stream of HTTP.
	 * 
	 * @return
	 */
	public String getContent() {
		if (content == null) {
			content = getContentFromStream();
		}
		return content;
	}
	
	private String getContentFromStream() {
		InputStream is = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			is = inputStream;
		} else {
			is = errorStream;
		}
		if (is == null) {
			throw new NullPointerException("there is no input stream.");
		}
		String line = null;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			try {
				reader = new BufferedReader(new InputStreamReader(is, IHttpManager.CHARSET_ENCODING));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
				if (is != null) {
					is.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

    /**
     * 获取header的第一个值
     * @param key header的键
     * @return header的值
     */
	public String getHeaderValue(String key) {
		List<String> values = getHeaderValues(key);
		if (values == null) {
			values = headers.get(key.toLowerCase());
		}
		if (values != null && !values.isEmpty()) {
			return values.get(0);
		} else {
			return null;
		}
	}

    /**
     * 获取header的值列表
     * @param key header的键
     * @return header的值
     */
    public List<String> getHeaderValues(String key) {
        return headers.get(key);
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(content);
		dest.writeString(contentEncoding);
		dest.writeString(contentType);
		dest.writeString(responseMessage);
		dest.writeString(sessionId);
		dest.writeInt(responseCode);
		dest.writeLong(contentLength);
	}

	public static final Creator<HttpResult> CREATOR = new Creator<HttpResult>() {
		
		@Override
		public HttpResult[] newArray(int size) {
			return new HttpResult[size];
		}
		
		@Override
		public HttpResult createFromParcel(Parcel source) {
			HttpResult result = new HttpResult();
			result.content = source.readString();
			result.contentEncoding = source.readString();
			result.contentType = source.readString();
			result.responseMessage = source.readString();
			result.sessionId = source.readString();
			result.responseCode = source.readInt();
			result.contentLength = source.readLong();
			return result;
		}
	};
}
