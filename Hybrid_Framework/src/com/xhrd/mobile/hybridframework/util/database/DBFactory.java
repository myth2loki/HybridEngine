package com.xhrd.mobile.hybridframework.util.database;


/**
 * factory of DAO.
 * @author wangqianyu
 *
 */
public class DBFactory {
	
	/**
	 * get a new instance of DAO.
	 * @param dbHelper sqlite helper
	 * @return a instance of DAO
	 */
	public static <T> IDAO<T> newDAO(BaseDAOHelper<T> dbHelper) {
		return new BaseDAO<T>(dbHelper);
	}

}
