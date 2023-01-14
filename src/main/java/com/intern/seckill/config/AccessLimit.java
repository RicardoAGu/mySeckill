package com.intern.seckill.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义拦截器，目标是通过计数器算法限制同一个用户过快地请求。
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    int second();

    int maxCount();

    boolean needLogin() default true;

}
