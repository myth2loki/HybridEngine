package com.xhrd.mobile.hybrid.util.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.xhrd.mobile.hybrid.util.database.annotation.DatabaseTable;


/**
 * DAO helper for creating database.
 * @author wangqianyu
 *
 * @param <T>
 */
public abstract class BaseDAOHelper<T> extends SQLiteOpenHelper {
	protected Class<T> mClazz;
	
	public BaseDAOHelper(Context context, String name, int version, Class<T> clazz) {
		super(context, name, null, version);
		mClazz = clazz;
	}

	public BaseDAOHelper(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler, Class<T> clazz) {
		super(context, name, factory, version, errorHandler);
		mClazz = clazz;
	}

	public BaseDAOHelper(Context context, String name, CursorFactory factory, int version, Class<T> clazz) {
		super(context, name, factory, version);
		mClazz = clazz;
	}

	@Override
	public final void onCreate(SQLiteDatabase db) {
		//this method will be called in getWritableDatabase().
	}

	@Override
	public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//this method will be called in getWritableDatabase().
		db.execSQL("DROP TABLE IF EXISTS " + getTableName(mClazz));
	}
	
	@Override
	public final void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//this method will be called in getWritableDatabase().
		db.execSQL("DROP TABLE IF EXISTS " + getTableName(mClazz));
	}
	
	protected static String getTableName(Class<?> entityClass) {
		DatabaseTable dt = entityClass.getAnnotation(DatabaseTable.class);
		if (dt != null && dt.name() != null) {
			return dt.name();
		} else {
			return null;
		}
	}
	
	public Class<T> getEntityClass() {
		return mClazz;
	}
}
