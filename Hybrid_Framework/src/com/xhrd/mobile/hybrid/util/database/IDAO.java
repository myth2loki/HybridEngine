package com.xhrd.mobile.hybrid.util.database;

import java.util.List;

/**
 * interface of DAO.
 * @author wangqianyu
 *
 * @param <T>
 */
public interface IDAO<T> {
	/**
	 * query for single result.
	 * @param whereSql where part of SQL
	 * @param selectionArgs values of place holders in where
	 * @param orderSql order of sort
	 * @return T the first item of result, or null if no result
	 */
	public T query(String whereSql, String[] selectionArgs, String orderSql);

	/**
	 * query for all.
	 * @return List<T> list of result, or empty list if no result
	 */
	public List<T> queryForAll();
	
	/**
	 * query for all.
	 * @param limit limit of records
	 * @return List<T> list of result, or empty list if no result
	 */
	public List<T> queryForAll(String limit);

	/**
	 * query for all by conditions.
	 * @param whereSql where part of SQL
	 * @param selectionArgs values of place holders in where
	 * @param orderSql order of sort
	 * @param limit limit of records
	 * @return List<T> list of result, or empty list if no result
	 */
	public List<T> queryForAll(String whereSql, String[] selectionArgs, String orderSql, String limit);
	
	/**
	 * query for all by conditions.
	 * @param whereSql where part of SQL
	 * @param selectionArgs values of place holders in where
	 * @param orderSql order of sort
	 * @return List<T> list of result, or empty list if no result
	 */
	public List<T> queryForAll(String whereSql, String[] selectionArgs, String orderSql);

	/**
	 * save data to table associated with T, database will be created if it is
	 * unavailable.
	 * 
	 * @param entity
	 */
	public void insert(T entity);

	/**
	 * save data to table associated with T, database will be created if it is
	 * unavailable.
	 * 
	 * @param entities
	 */
	public void insert(List<T> entities);

	/**
	 * delete all data.
	 */
	public void delete();

	/**
	 * delete data by condition.
	 * @param whereSql where string
	 * @param whereArgs where args
	 */
	public void delete(String whereSql, String[] whereArgs);

	/**
	 * update entity if found data by conditions, otherwise insert.
	 * 
	 * @param whereSql where string
	 * @param whereArgs where args
	 * @param entity value
	 */
	public void addOrUpdateData(String whereSql, String[] whereArgs, T entity);

	/**
	 * update entity by conditions.
	 * @param whereSql
	 * @param whereArgs
	 * @param entity
	 */
	public void update(String whereSql, String[] whereArgs, T entity);

	/**
	 * create table according to T.
	 */
	public void createTable();
	
	/**
	 * drop table according to T.
	 */
	public void dropTable();
	
	/**
	 * drop all tables related with {@link BaseDAOHelper}.
	 */
	public void dropAllTables();

	/**
	 * check if table relates to T exists.
	 * @return true if exists, otherwise false
	 */
	public boolean checkTableAvailable();

	/**
	 * get count of records relates to T;
	 * 
	 * @return number of record, -1 if table is unavailable
	 */
	public int getRecordCount();

	/**
	 * get version of database.
	 * @return
	 */
	public int getDBVersion();

	/**
	 * close the DAO.
	 */
	public void close();

}