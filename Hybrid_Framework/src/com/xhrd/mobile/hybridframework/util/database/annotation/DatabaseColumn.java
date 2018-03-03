package com.xhrd.mobile.hybridframework.util.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface DatabaseColumn {
	String name();
	boolean isNullable() default true;
	boolean isForeign() default false;
}
