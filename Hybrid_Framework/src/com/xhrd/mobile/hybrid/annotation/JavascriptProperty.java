package com.xhrd.mobile.hybrid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注释js属性
 * <br/>Created by maxinliang on 15/6/12.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JavascriptProperty {

    /**
     * 属性名称
     * @return
     */
    String name();

    /**
     * 属性值(需是定值)
     * @return
     */
    String value() default "";
}
