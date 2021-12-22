package com.fsolsh.mining.annotation;

import java.lang.annotation.*;

/**
 * 无需Token注解
 * 添加此注解的请求处理方法，不会校验token，仅作用于方法，防止误加在类上引发灾难
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NoTokenRequired {

}
