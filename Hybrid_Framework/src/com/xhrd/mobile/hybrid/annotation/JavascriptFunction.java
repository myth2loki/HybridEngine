package com.xhrd.mobile.hybrid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明js方法
 * Created by wangqianyu on 15/6/3.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JavascriptFunction {
    /**
     * 方法名
     * @return
     */
    String name() default "";

    /**
     * 是否有返回
     * @return
     */
    boolean hasReturn() default false;

    /**
     * 是否转换返回的js
     * @return
     */
    boolean convertJS() default false;

    /**
     * js方法所需的权限
     * @return
     */
    String[] permissions() default {};

    /**
     * js方法所需的权限说明
     * @return
     */
    String[] permissionRationales() default {};

    /**
     * 是否doze
     * @return
     */
    boolean doze() default false;
}
