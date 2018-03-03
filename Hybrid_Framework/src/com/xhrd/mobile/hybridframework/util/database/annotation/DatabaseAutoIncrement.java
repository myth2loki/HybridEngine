package com.xhrd.mobile.hybridframework.util.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auto increment is explained to "PRIMARY KEY AUTOINCREMENT". It must be used
 * with data type "long" and must not be used with {@code DatabaseUnique},
 * {@code DatabasePrimaryKey} according to sqlite doc. Otherwise will be
 * ignored.
 * 
 * @author wangqianyu
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface DatabaseAutoIncrement {
}
