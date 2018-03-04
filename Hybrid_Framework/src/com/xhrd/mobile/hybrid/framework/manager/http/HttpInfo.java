package com.xhrd.mobile.hybrid.framework.manager.http;

import org.json.JSONObject;

public class HttpInfo {
	public int pid;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String url;
	public String json;
    private int cachestarttime;
    private int cachetime;
	private String  header=null;

	public String getHeader() {
		return header;
	}

	public void setHeader(JSONObject header) {
		if(header!=null){
			this.header = header.toString();
		}

	}

	public int getCachetime() {
        return cachetime;
    }

    public void setCachetime(int cachetime) {
        this.cachetime = cachetime;
    }

    public int getCachestarttime() {
        return cachestarttime;
    }

    public void setCachestarttime(int cachestarttime) {
        this.cachestarttime = cachestarttime;
    }
}
