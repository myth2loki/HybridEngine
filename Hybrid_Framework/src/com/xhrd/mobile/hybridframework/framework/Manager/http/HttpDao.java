package com.xhrd.mobile.hybridframework.framework.Manager.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

 

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;


import com.xhrd.mobile.hybridframework.framework.Manager.http.util.Md5;

import org.json.JSONObject;


public class HttpDao{
	
	private static HttpDB helper;
	public HttpDao() {
		helper = new HttpDB();
	}

   //url varchar(), json varchar()
	public void insertData(HttpInfo bean) {
        //判断更新
        if (updateControlBean(bean)>0){
            return;
        }
		SQLiteDatabase db = null;		 
		try {
			db = helper.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put("url", Md5.getMD5(bean.getUrl()));
			values.put("json", bean.getJson());
            int currentTime=(int)System.currentTimeMillis();
            values.put("cachestarttime",currentTime);
            values.put("cachetime", bean.getCachetime());
			values.put("header",bean.getHeader());
            db.insert(HttpDB.table, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
		 
	}

	 
	public int deleteControlByID(String url) {
		SQLiteDatabase db = null;
		int delete = 0;
		try {
			db = helper.getReadableDatabase();
			delete = db.delete(HttpDB.table, "url=?", new String[]{Md5.getMD5(url)});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
		return delete;
	}

	 
	public int updateControlBean(HttpInfo bean) {
		SQLiteDatabase db = null;
		int delete = 0;
		try {
			db = helper.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put("url", Md5.getMD5(bean.getUrl()));
			values.put("json", bean.getJson());
            int currentTime=(int)System.currentTimeMillis();
            values.put("cachestarttime", currentTime);
            values.put("cachetime", bean.getCachetime());
			values.put("header",bean.getHeader());
			delete = db.update(HttpDB.table, values, "url=?", new String[]{bean.getUrl()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
		return delete;
	}

	 
	public HttpInfo queryControlById(String url,String offline) {
        //true代表获取离线缓存
        HttpInfo httpInfo=new HttpInfo();

        if(!offline.equals("true")){
            return httpInfo;
        }
		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = helper.getReadableDatabase();
			cursor = db.query(HttpDB.table, null, "url=?", new String[]{Md5.getMD5(url)}, null, null, null);
			while (cursor.moveToNext()) {
				String httpurl = cursor.getString(1);
				String json = cursor.getString(2);
                int cachestartTime=cursor.getInt(3);
                int cachetime=cursor.getInt(4);
				String header=cursor.getString(5);
				httpInfo.setHeader(new JSONObject(String.valueOf(header)));
				httpInfo.setUrl(Md5.getMD5(httpurl));
                //时间判断 如果时间不符  则放回的值为null
                int currentTime=(int)System.currentTimeMillis();
                if((currentTime-cachestartTime)<=cachetime){
                    httpInfo.setJson(json);
                }
                httpInfo.setCachestarttime(cachestartTime);
                httpInfo.setCachetime(cachetime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
			if (cursor != null && cursor.isClosed()) {
				cursor.close();
			}
		}
		return httpInfo;
	}
}
