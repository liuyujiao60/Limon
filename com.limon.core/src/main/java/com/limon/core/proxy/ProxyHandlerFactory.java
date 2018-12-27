package com.limon.core.proxy;


import com.limon.core.annotation.Aop;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Slf4j
public class ProxyHandlerFactory {

    public static ClassInvocationHandler createClassInvocationHandler(Class clazz) throws IllegalAccessException, InstantiationException {
        Aop annotation= (Aop) clazz.getAnnotation(Aop.class);
        if (annotation!=null) {
            return new ClassInvocationHandler(clazz.newInstance(), (ProxyHandler) annotation.value().newInstance());
        }
        return new ClassInvocationHandler(clazz.newInstance());
    }

    public static MethodInvocationHandler createMethodInvocationHandler(Method method) throws IllegalAccessException, InstantiationException {
        Aop annotation= (Aop) method.getAnnotation(Aop.class);
        if (annotation!=null) {
            return new MethodInvocationHandler(method, (ProxyHandler) annotation.value().newInstance());
        }
        return new MethodInvocationHandler(method, null);
    }
}
