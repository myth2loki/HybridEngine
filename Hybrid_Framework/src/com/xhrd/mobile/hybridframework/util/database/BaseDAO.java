package com.xhrd.mobile.hybridframework.util.database;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xhrd.mobile.hybridframework.util.database.annotation.DatabaseAutoIncrement;
import com.xhrd.mobile.hybridframework.util.database.annotation.DatabaseColumn;
import com.xhrd.mobile.hybridframework.util.database.annotation.DatabaseIndex;
import com.xhrd.mobile.hybridframework.util.database.annotation.DatabaseOneToMany;
import com.xhrd.mobile.hybridframework.util.database.annotation.DatabasePrimaryKey;
import com.xhrd.mobile.hybridframework.util.database.annotation.DatabaseTable;
import com.xhrd.mobile.hybridframework.util.database.annotation.DatabaseUnique;

/**
 * implementation of IDAO.
 * @author wangqianyu
 *
 * @param <T>
 */
public final class BaseDAO<T> implements IDAO<T> {
	
	public static final String TAG = "BaseDAO";
	
	private Class<T> mClazz;
	private SQLiteOpenHelper mDbHelper;
	private Map<Class<?>, Analyzer<?, ?>> analyzerMap;
	private String mTableName;
	private Map<String, Field> mColumnMap;
	private Field mAutoIncreField;

	BaseDAO(BaseDAOHelper<T> dbHelper) {
		initAnalyzer();
		mDbHelper = dbHelper;
		mClazz = dbHelper.getEntityClass();
		if (mClazz == null) {
			throw new IllegalArgumentException("entity class is null for DAO.");
		}
		DatabaseTable dt = mClazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("illegal class, not found DataBaseTable.");
		}
		mTableName = dt.name();
//		testSQL();
		if (!checkTableAvailable()) {
			createTable();
		}
		mColumnMap = getColumnMap();
	}

	private void initAnalyzer() {
		analyzerMap = new HashMap<Class<?>, Analyzer<?, ?>>();
		analyzerMap.put(Date.class, new DateAnalyzer());
		analyzerMap.put(String.class, new StringAnalyzer());
		analyzerMap.put(Enum.class, new EnumAnalyzer());
	}

	@Override
	public T query(String where, String[] selectionArgs, String order) {
		return query(where, selectionArgs, order, mClazz);
	}
	
	public <V> V query(String where, String[] selectionArgs, String order, Class<V> clazz) {
		List<V> items = queryForAll(where, selectionArgs, order, null, clazz);
		return items.size() == 0 ? null : items.get(0);
	}

	@Override
	public List<T> queryForAll() {
		return queryForAll(null, null, null);
	}
	
	@Override
	public List<T> queryForAll(String limit) {
		return queryForAll(null, null, null, limit);
	}
	
	/**
	 * query all data related to current DAO.
	 */
	@Override
	public List<T> queryForAll(String whereSql, String[] selectionArgs, String orderSql) {
		return queryForAll(whereSql, selectionArgs, orderSql, null, mClazz);
	}
	
	/**
	 * query all data related to current DAO.
	 */
	@Override
	public List<T> queryForAll(String whereSql, String[] selectionArgs, String orderSql, String limit) {
		return queryForAll(whereSql, selectionArgs, orderSql, limit, mClazz);
	}

	/**
	 * query all data for current table name.
	 * @param whereSql
	 * @param selectionArgs
	 * @param orderSql
	 * @param clazz
	 * @return
	 */
	private <V> List<V> queryForAll(String whereSql, String[] selectionArgs, String orderSql, String limit, Class<V> clazz) {
		DatabaseTable dt = clazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("not found DatabaseTable in class. class: " + clazz);
		}
		Cursor cursor = mDbHelper.getReadableDatabase()
				.query(dt.name(), null, whereSql, selectionArgs, null, null, orderSql, limit);
		V bean = null;
		List<V> beans = new ArrayList<V>();
		try {
			while (cursor.moveToNext()) {
				try {
					bean = convertCursorToEntity(cursor, clazz);
					loadRefData(bean);
					beans.add(bean);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return beans;
	}

	@Override
	public void insert(T entity) {
		List<T> entities = new ArrayList<T>(1);
		entities.add(entity);
		insert(entities);
	}

	@Override
	public void insert(List<T> entities) {
		if (entities.size() > 0) {
			SQLiteDatabase sdb = mDbHelper.getWritableDatabase();
			sdb.beginTransaction();
			try {
				try {
					ContentValues cv = null;
					long rowId = -1;
					for (T entity : entities) {
						cv = getContentValues(mColumnMap, entity);
						rowId = sdb.insert(mTableName, null, cv);
						if (rowId == -1) {
							Log.e(TAG, String.format("save entity faield. entity: %s", entity));
						} else {
							if (mAutoIncreField != null) {
								mAutoIncreField.set(entity, rowId);
							}
							//save ref data
							saveRefData(sdb, entity);
//							for (Map.Entry<String, Field> entry : mColumnMap.entrySet()) {
//								saveRefData(sdb, entry.getValue().get(entity));
//							}
						}
					}
					sdb.setTransactionSuccessful();
				} catch (IllegalArgumentException e) {
					Log.e(TAG, "", e);
				} catch (SQLiteException e) {
					Log.e(TAG, "", e);
				} catch (SecurityException e) {
					Log.e(TAG, "", e);
				} catch (IllegalAccessException e) {
					Log.e(TAG, "", e);
				}
			} finally {
				sdb.endTransaction();
			}
		}
	}
	
	
	private void insertOther(SQLiteDatabase sdb, Object entity) {
		List<Object> entities = new ArrayList<Object>(1);
		entities.add(entity);
		insertOther(sdb, entities);
	}
	
	private void insertOther(SQLiteDatabase sdb, List<Object> entities) {
		if (entities.size() > 0) {
			Object bean = entities.get(0);
			String tableName = getTableName(bean.getClass());
			try {
				ContentValues cv = null;
				long rowId = -1;
				for (Object entity : entities) {
					cv = getContentValues(mColumnMap, entity);
					rowId = sdb.insert(tableName, null, cv);
					if (rowId == -1) {
						Log.e(TAG, String.format("save entity faield. entity: %s", entity));
					} else {
						// save ref data
						saveRefData(sdb, entity);
					}
				}
				sdb.setTransactionSuccessful();
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "", e);
			} catch (SQLiteException e) {
				Log.e(TAG, "", e);
			} catch (SecurityException e) {
				Log.e(TAG, "", e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "", e);
			}
		}
	}

	@Override
	public void delete() {
		DatabaseTable dt = mClazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("illegal class to get data.");
		}
		SQLiteDatabase sdb = mDbHelper.getWritableDatabase();
		try {
			sdb.delete(mTableName, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(String whereSql, String[] whereArgs) {
		DatabaseTable dt = mClazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("illegal class to get data.");
		}
		SQLiteDatabase sdb = mDbHelper.getWritableDatabase();
		sdb.delete(mTableName, whereSql, whereArgs);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addOrUpdateData(String whereSql, String[] whereArgs, T entity) {
		if (getRecordCount() > 0 && queryForAll(whereSql, whereArgs, null).size() > 0) {
			update(whereSql, whereArgs, entity);
		} else {
			insert(Arrays.asList(entity));
		}
	}

	@Override
	public void update(String whereSql, String[] whereArgs, T entity) {
		if (entity != null) {
			Class<?> clazz = entity.getClass();
			DatabaseTable dt = clazz.getAnnotation(DatabaseTable.class);
			if (dt == null) {
				throw new IllegalArgumentException("illegal class to get data.");
			}
			SQLiteDatabase sdb = mDbHelper.getWritableDatabase();
			try {
				ContentValues cv = null;
				long rowId = -1;
				cv = getContentValues(mColumnMap, entity);
				rowId = sdb.update(mTableName, cv, whereSql, whereArgs);
				if (rowId == -1) {
					Log.e(TAG, String.format("update entity faield. entity: %s", entity));
				}
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "", e);
			} catch (SQLiteException e) {
				Log.e(TAG, "", e);
			} catch (SecurityException e) {
				Log.e(TAG, "", e);
			}
		}
	}

	@Override
	public void dropTable() {
		DatabaseTable dt = mClazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("illegal class to get data.");
		}
		SQLiteDatabase sdb = mDbHelper.getWritableDatabase();
		sdb.execSQL(String.format("DROP TABLE IF EXISTS %s", mTableName));
	}
	
	@Override
	public void dropAllTables() {
		SQLiteDatabase sdb = mDbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = sdb.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name != 'sqlite_sequence'", null);
			String tableName = null;
			while (cursor.moveToNext()) {
				tableName = cursor.getString(0);
				sdb.execSQL(String.format("DROP TABLE %s", tableName));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean checkTableAvailable(String tableName) {
		SQLiteDatabase sdb = mDbHelper.getWritableDatabase();
		String sql = String.format("SELECT * FROM sqlite_master WHERE type = 'table' AND name = '%s'", tableName);
		Cursor cursor = null;
		try {
			cursor = sdb.rawQuery(sql, null);
			return cursor.getCount() > 0;
		} catch (SQLiteException e) {
			// error means Table is unavailable, do nothing but return false.
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return false;
	}

	@Override
	public boolean checkTableAvailable() {
		DatabaseTable dt = mClazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("illegal class to get data.");
		}
		String tableName = dt.name();
		return checkTableAvailable(tableName);
	}

	@Override
	public int getRecordCount() {
		DatabaseTable dt = mClazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("illegal class to get data.");
		}
		SQLiteDatabase sdb = mDbHelper.getReadableDatabase();
		String sql = String.format("SELECT COUNT(*) FROM %s;", mTableName);
		Cursor cursor = null;
		int count = -1;
		try {
			cursor = sdb.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
		} catch (SQLiteException e) {
			// error means Table is unavailable, do nothing but return false.
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return count;
	}

	public void createTable() {
		createTable(mClazz);
	}
	
	private void createTable(Class<?> clazz) {
		createTable(clazz, null, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createTable(Class<?> clazz, String refTableName, String refColumnName) {
		List<Field> fields = getFields(clazz);
		List<Field> pkColumns = new ArrayList<Field>();
		StringBuffer pkColumnNames = new StringBuffer();
		StringBuffer columnsInSql = new StringBuffer();
		StringBuilder refColumnsInSql = new StringBuilder();
		List<String> indexList = new ArrayList<String>();
		for (Field f : fields) {
			Class<?> fieldType = f.getType();
			DatabaseColumn dc = f.getAnnotation(DatabaseColumn.class);
			if (dc != null) {
				columnsInSql.append(dc.name()).append(" ");
				if (fieldType.isPrimitive()) {
					if (int.class.equals(fieldType) || long.class.equals(fieldType) || float.class.equals(fieldType)
							|| double.class.equals(fieldType) || short.class.equals(fieldType)
							|| char.class.equals(fieldType) || boolean.class.equals(fieldType)) {
						columnsInSql.append("INTEGER");
					} else {
						throw new IllegalArgumentException(String.format("no matched field %s to create table %s",
								fieldType, mTableName));
					}
				} else if (byte[].class.equals(fieldType)) {
					columnsInSql.append("BLOB");
				} else {
					Analyzer analyzer = fieldType.isEnum() ? analyzerMap.get(Enum.class) : analyzerMap.get(fieldType);
					if (analyzer == null) {
						throw new IllegalArgumentException(String.format("no matched field %s to create table %s",
								fieldType, mTableName));
					}
					columnsInSql.append(analyzer.getColumnType(fieldType));
				}
				
				//check auto increment
				if (f.isAnnotationPresent(DatabaseAutoIncrement.class)
						&& (int.class.equals(fieldType) || long.class.equals(fieldType))) {
					columnsInSql.append(" PRIMARY KEY AUTOINCREMENT");
				} else if (dc.isNullable()) {
					//check null-able.
					columnsInSql.append(" NULL");
				} else {
					columnsInSql.append(" NOT NULL");
				}
				
				//unique can not be used with auto increment
				if (f.isAnnotationPresent(DatabaseUnique.class) && !f.isAnnotationPresent(DatabaseAutoIncrement.class)) {
					columnsInSql.append(" UNIQUE");
				}
				//primary key can not be used with auto increment
				if (f.isAnnotationPresent(DatabasePrimaryKey.class) && !f.isAnnotationPresent(DatabaseAutoIncrement.class)) {
					pkColumns.add(f);
					pkColumnNames.append(dc.name()).append(',');
				}
				if (f.isAnnotationPresent(DatabaseIndex.class)) {
					indexList.add(dc.name());
				}
				columnsInSql.append(',');
				
				if (dc.isForeign()) {
					//foreign key(id) references outTable(id) on delete cascade on update cascade
					String tempStr = " FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE ON UPDATE CASCADE";
					String refSql = String.format(tempStr, dc.name(), refTableName, refColumnName);
					if (refColumnsInSql.length() > 0) {
						refColumnsInSql.append(',');
					}
					refColumnsInSql.append(refSql);
				}
			}
			
			//check if it creates relationship
			DatabaseOneToMany dotm = f.getAnnotation(DatabaseOneToMany.class);
			DatabaseTable dt = f.getAnnotation(DatabaseTable.class);
			if (dotm != null) {
				createRefTable(dt.name(), pkColumns.get(0), f);
			}
		}
		if (pkColumnNames.length() > 0) {
			pkColumnNames.deleteCharAt(pkColumnNames.length() - 1);
		}
		if (columnsInSql.length() > 0) {
			columnsInSql.deleteCharAt(columnsInSql.length() - 1);
		}

		String sql = null;
		if (pkColumnNames.length() > 0) {
			sql = String.format("CREATE TABLE %s (%s, PRIMARY KEY(%s));", mTableName, columnsInSql.toString(),
					pkColumnNames.toString());
		} else {
			sql = String.format("CREATE TABLE %s (%s);", mTableName, columnsInSql.toString());
		}

		SQLiteDatabase sdb = mDbHelper.getWritableDatabase();
		try {
			sdb.beginTransaction();
			sdb.execSQL(sql);

			// create index
			for (String indexName : indexList) {
				String indexSql = String.format("CREATE INDEX %s_%s_index on %s(%s);", mTableName, indexName, mTableName,
						indexName);
				sdb.execSQL(indexSql);
			}
			sdb.setTransactionSuccessful();
		} finally {
			sdb.endTransaction();
		}
	
	}

	/**
	 * onUpgrade or onDowngrade will be called after calling the method if need
	 * to update database. It is used to update database structure. Please call
	 * the method before any operation of database.
	 */
	public void updateDatabseIfNeedUpdate() {
		mDbHelper.getWritableDatabase();
	}

	public void beginTransaction() {
		mDbHelper.getWritableDatabase().beginTransaction();
	}

	public void endTransaction() {
		mDbHelper.getWritableDatabase().endTransaction();
	}

	public void setTransactionSuccessful() {
		mDbHelper.getWritableDatabase().setTransactionSuccessful();
	}

	@Override
	public int getDBVersion() {
		return mDbHelper.getWritableDatabase().getVersion();
	}

	@Override
	public void close() {
		mDbHelper.close();
	}

	private static List<Field> getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		do {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		} while ((clazz = clazz.getSuperclass()) != null);
		return fields;
	}

	/**
	 * add analyzer to parse data for the class.
	 * @param clazz class which is analyzed
	 * @param analyzer analyzer
	 */
	public void addAnalyzer(Class<?> clazz, Analyzer<?, ?> analyzer) {
		analyzerMap.put(clazz, analyzer);
	}

	/**
	 * analyzer for {@link Date}
	 * @author wangqianyu
	 *
	 */
	private static class DateAnalyzer implements Analyzer<Date, Long> {

		@Override
		public String getColumnType(Class<? extends Date> fieldType) {
			return "INTEGER";
		}

		@Override
		public Long getValueForDB(Date object) {
			if (object == null) {
				return null;
			}
			return object.getTime();
		}

		@Override
		public Date getValueFromDB(Class<? extends Date> fieldType, Cursor cursor, int index) {
			return new Date(cursor.getLong(index));
		}

	}

	/**
	 * analyzer for {@link String}
	 * @author wangqianyu
	 *
	 */
	private static class StringAnalyzer implements Analyzer<String, String> {

		@Override
		public String getColumnType(Class<? extends String> fieldType) {
			return "TEXT";
		}

		@Override
		public String getValueForDB(String object) {
			return object;
		}

		@Override
		public String getValueFromDB(Class<? extends String> fieldType, Cursor cursor, int index) {
			return cursor.getString(index);
		}

	}

	/**
	 * analyzer for {@link Enum}
	 * @author wangqianyu
	 *
	 */
	private static class EnumAnalyzer implements Analyzer<Enum<?>, Integer> {

		@Override
		public String getColumnType(Class<? extends Enum<?>> fieldType) {
			return "INTEGER";
		}

		@Override
		public Integer getValueForDB(Enum<?> object) {
			if (object == null) {
				return null;
			}
			return object.ordinal();
		}

		@Override
		public Enum<?> getValueFromDB(Class<? extends Enum<?>> fieldType, Cursor cursor, int index) {
			try {
				return fieldType.getEnumConstants()[cursor.getInt(index)];
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ContentValues getContentValues(Map<String, Field> columnMap, Object entity) {
		ContentValues cv = new ContentValues();
		Field f = null;
		String column = null;
		Class<?> fieldType = null;
		Object value = null;
		for (Map.Entry<String, Field> entry : columnMap.entrySet()) {
			column = entry.getKey();
			f = entry.getValue();
			fieldType = f.getType();
			
			//get original value
			try {
				value = f.get(entity);
			} catch (Exception e) {
				Log.e(TAG, "get value failed.", e);
				value = null;
			}
			
			Analyzer analyzer = fieldType.isEnum() ? analyzerMap.get(Enum.class) : analyzerMap.get(fieldType);
			if (analyzer != null) {
				value = analyzer.getValueForDB(value);
			}
			if (value == null) {
				continue;
			}
			if (fieldType.isPrimitive()) {
				if (boolean.class.isAssignableFrom(fieldType)) {
					cv.put(column, (Boolean) value);
				} else if (byte.class.isAssignableFrom(fieldType)) {
					cv.put(column, (Byte) value);
				} else if (double.class.isAssignableFrom(fieldType)) {
					cv.put(column, (Double) value);
				} else if (float.class.isAssignableFrom(fieldType)) {
					cv.put(column, (Float) value);
				} else if (int.class.isAssignableFrom(fieldType)) {
					cv.put(column, (Integer) value);
				} else if (long.class.isAssignableFrom(fieldType)) {
					Long v = (Long) value;
					//ignore if the field is auto-increment and value is default(0).
					if (f.isAnnotationPresent(DatabaseAutoIncrement.class) && v == 0) {
						continue;
					}
					cv.put(column, (Long) value);
				} else if (short.class.isAssignableFrom(fieldType)) {
					cv.put(column, (Short) value);
				}
			} else {
				if (byte[].class.isAssignableFrom(fieldType)) {
					cv.put(column, (byte[]) value);
				} else {
					cv.put(column, String.valueOf(value));
				}
			}
		}
		return cv;
	}
	
	private Map<String, Field> getColumnMap() {
		return getColumnMap(mClazz);
	}
	
	private Map<String, Field> getColumnMap(Class<?> clazz) {
		Map<String, Field> map = new HashMap<String, Field>();

		for (Field f : getFields(clazz)) {
			DatabaseColumn dc = f.getAnnotation(DatabaseColumn.class);
			DatabaseAutoIncrement dai = f.getAnnotation(DatabaseAutoIncrement.class);
			if (dai != null) {
				f.setAccessible(true);
				mAutoIncreField = f;
			}
			if (dc != null) {
				f.setAccessible(true);
				map.put(dc.name(), f);
			}
		}
		return map;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <V> V convertCursorToEntity(Cursor cursor, Class<V> clazz) throws IllegalArgumentException, 
												IllegalAccessException, InstantiationException {
		V bean = clazz.newInstance();
		for (Entry<String, Field> entry : mColumnMap.entrySet()) {
			Field f = entry.getValue();
			Class<?> fieldType = f.getType();
			if (fieldType.isPrimitive()) {
				if (String.class.equals(fieldType)) {
					f.set(bean, cursor.getString(cursor.getColumnIndex(entry.getKey())));
				} else if (int.class.equals(fieldType)) {
					f.setInt(bean, cursor.getInt(cursor.getColumnIndex(entry.getKey())));
				} else if (long.class.equals(fieldType)) {
					f.setLong(bean, cursor.getLong(cursor.getColumnIndex(entry.getKey())));
				} else if (float.class.equals(fieldType)) {
					f.setFloat(bean, cursor.getFloat(cursor.getColumnIndex(entry.getKey())));
				} else if (double.class.equals(fieldType)) {
					f.setDouble(bean, cursor.getDouble(cursor.getColumnIndex(entry.getKey())));
				} else if (short.class.equals(fieldType)) {
					f.setShort(bean, cursor.getShort(cursor.getColumnIndex(entry.getKey())));
				} else if (char.class.equals(fieldType)) {
					f.setChar(bean, (char) cursor.getShort(cursor.getColumnIndex(entry.getKey())));
				} else if (boolean.class.equals(fieldType)) {
					f.setBoolean(bean, cursor.getInt(cursor.getColumnIndex(entry.getKey())) == 1);
				} else {
					throw new IllegalArgumentException(String.format("no matched field %s in table %s", fieldType,
							mTableName));
				}
			} else if (byte[].class.equals(fieldType)) {
				f.set(bean, cursor.getBlob(cursor.getColumnIndex(entry.getKey())));
			} else {
				Analyzer analyzer = fieldType.isEnum() ? analyzerMap.get(Enum.class) : analyzerMap.get(fieldType);
				if (analyzer == null) {
					throw new IllegalArgumentException(String.format("no matched field %s in table %s", fieldType,
							mTableName));
				}
				Object result = analyzer.getValueFromDB(fieldType, cursor, cursor.getColumnIndex(entry.getKey()));
				f.set(bean, result);
			}
		}
		return bean;
	}
	
	private void createRefTable(String tableName, Field pkField, Field refField) {
		Class<?> refType = refField.getType();
		if (List.class.isAssignableFrom(refType)) {
			Type fc = refField.getGenericType();
			if (fc instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) fc;
				Class<?> genClazz = (Class<?>) pt.getActualTypeArguments()[0];
				createTable(genClazz, tableName, pkField.getName());
			}
		}
	}
	
	private String getTableName(Class<?> clazz) {
		DatabaseTable dt = clazz.getAnnotation(DatabaseTable.class);
		if (dt == null) {
			throw new IllegalArgumentException("not found DatabaseTable from " + clazz);
		}
		return dt.name();
	}
	
	/**
	 * save ref-data
	 * @param sdb
	 * @param entity parent entity
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void saveRefData(SQLiteDatabase sdb, Object entity) throws IllegalArgumentException, IllegalAccessException {
		Map<Field, Class<?>> pClassMap = getRefClassese(entity.getClass());
		for (Field field : pClassMap.keySet()) {
			if (List.class.isAssignableFrom(field.getType())) {
				List<?> listValue = (List<?>) field.get(entity);
				insertOther(sdb, listValue);
			} else {
				Object value = field.get(entity);
				insertOther(sdb, value);
			}
		}
	}
	
	/**
	 * get parameterized type.
	 * @param f
	 * @return
	 */
	private static Type getParameterizedType(Field f) {
		Type fc = f.getGenericType();
		if (fc instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) fc;
			return pt.getActualTypeArguments()[0];
		}
		return null;
	}
	
	/**
	 * get all classes that has ref-data.
	 * @param clazz
	 * @return
	 */
	private static Map<Field, Class<?>> getRefClassese(Class<?> clazz) {
		Map<Field, Class<?>> pClassMap = new HashMap<Field, Class<?>>();
		for (Field f : getFields(clazz)) {
			if (f.isAnnotationPresent(DatabaseOneToMany.class)
					&& List.class.isAssignableFrom(f.getType())) {
				Type pt = getParameterizedType(f);
				Class<?> pClass = (Class<?>) pt;
				pClassMap.put(f, pClass);
			} else {
				//TODO the others
			}
		}
		return pClassMap;
	}
	
	/**
	 * get ref-data related to entity
	 * @param entity
	 */
	private void loadRefData(Object entity) {
		Map<Field, Class<?>> refClasseMap = getRefClassese(entity.getClass());
		
		Field field = null;
		Class<?> clazz = null;
		Object value = null;
		for (Map.Entry<Field, Class<?>> entry : refClasseMap.entrySet()) {
			field = entry.getKey();
			clazz = entry.getValue();
			if (List.class.isAssignableFrom(field.getType())) {
				value = queryForAll(null, null, null, null, clazz);
			} else {
				value = query(null, null, null, clazz);
			}
			try {
				field.set(entity, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * test for developing new DAO.
	 */
//	public void testSQL() {
//		SQLCreationBuilder builder = new SQLCreationBuilder(mClazz, analyzerMap);
//		Log.e("creation sql--------->", builder.build()+"");
//	}
}
