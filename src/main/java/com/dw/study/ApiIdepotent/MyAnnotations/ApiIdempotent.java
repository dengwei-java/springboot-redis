package com.dw.study.ApiIdepotent.MyAnnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author dw
 * @ClassName ApiIdempotent
 * @Description 自定义注解实现接口的幂等性
 * @Date 2021/1/30 13:06
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIdempotent {

}
