package com.xhrd.mobile.hybrid.annotation;

import com.xhrd.mobile.hybrid.framework.PluginData;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解plugin的scope，默认为PluginData.Scope.New
 * <br/>
 * 注解plugin是否为UiView
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
	PluginData.Scope scope() default PluginData.Scope.New;

    boolean lifeCycleCallback() default false;

//    boolean isUI() default false;

    /**
     * js属性
     * @return
     */
    JavascriptProperty[] properties() default {};
}
