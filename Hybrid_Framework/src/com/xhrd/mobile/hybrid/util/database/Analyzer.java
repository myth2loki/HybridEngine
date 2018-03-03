package com.xhrd.mobile.hybrid.util.database;

import android.database.Cursor;

/**
 * analyzer for DAO's storing and restoring.
 * @author wangqianyu
 *
 * @param <T> data type of field
 * @param <V> value for storing
 */
public interface Analyzer<T, V> {
	/**
	 * get data type of column which relates the class.
	 * 
	 * @param fieldType data type of field
	 * @return data type eg. INTEGER, TEXT...
	 */
	String getColumnType(Class<? extends T> fieldType);

	/**
	 * get value of this column for DB storing.
	 * 
	 * @param object
	 *            original value
	 * @return concrete value
	 */
	V getValueForDB(T object);

	/**
	 * get value of this column which relates the field from DB.
	 * 
	 * @param fieldType
	 *            data type
	 * @param cursor
	 *            cursor of DB
	 * @param index
	 *            index of cursor
	 * @return concrete value
	 */
	T getValueFromDB(Class<? extends T> fieldType, Cursor cursor, int index);
}