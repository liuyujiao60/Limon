package com.limon.core.annotation;

import com.limon.core.proxy.ProxyHandler;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aop {

    Class value();
}
