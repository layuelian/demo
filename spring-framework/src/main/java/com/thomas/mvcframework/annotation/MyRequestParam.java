package com.thomas.mvcframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})//可以在什么地方使用
@Retention(RetentionPolicy.RUNTIME)//生命周期
@Documented
public @interface MyRequestParam {
	String value() default "";
}
