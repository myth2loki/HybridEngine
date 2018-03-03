package com.xhrd.mobile.hybridframework.framework.Manager.http;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xhrd.mobile.hybridframework.engine.HybridActivity;

public class HttpDB extends SQLiteOpenHelper {


	private static final String DB_NAME = "http.db";
	

	public static final String table = "httpdata";
	

	private static final int DB_VERSION = 1;
	 
	
 
	public HttpDB() {
		super(HybridActivity.getInstance().getApplication(), DB_NAME, null, DB_VERSION);
	}

	 

	private static final String SQL_HTTP_TABLE = "CREATE TABLE "
			+  table
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,url varchar , json varchar,cachestarttime INTEGER,cachetime INTEGER,header varchar)";

	@Override
	public void onCreate(SQLiteDatabase db) {
		  
		db.execSQL(SQL_HTTP_TABLE); // 
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
