package com.xhrd.mobile.hybridframework.util.database;

import android.database.sqlite.SQLiteDatabase;

public interface DBListener {
	/**
	 * called when database needs to be created.
	 * @param db
	 */
	void onDBCreate(SQLiteDatabase db);
	
	/**
	 * called when database needs to be upgraded.
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	void onDBUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	
	/**
	 * called when database needs to be downgraded.
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	void onDBDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
