package com.xhrd.mobile.hybridframework.annotation;

import com.xhrd.mobile.hybridframework.framework.PluginData;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解plugin的scope，默认为PluginData.Scope.createNew
 * <br/>
 * 注解plugin是否为UiView
 * <br/>Created by maxinliang on 15/6/10.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JavascriptConfig {
    /**
     * 域名设置，默认为空字符串.
     * @return
     */
    String domain() default "";
    /**
     * scope
     * @return
     */
	PluginData.Scope scope() default PluginData.Scope.createNew;

    boolean lifeCycleCallback() default false;

//    boolean isUI() default false;

    /**
     * js属性
     * @return
     */
    JavascriptProperty[] properties() default {};
}
