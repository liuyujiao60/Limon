package com.limon.core.ioc;

import com.limon.core.BeanProvider;
import com.limon.core.annotation.RPCReference;
import com.limon.core.proxy.ClassInvocationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

@Slf4j
public class BeanGraftedFactory {

    private <T> void graftBean(Class<T> classType, BeanProvider beanProvider) throws IllegalAccessException {
        ClassInvocationHandler classInvocationHandler=(ClassInvocationHandler)Proxy
                .getInvocationHandler(beanProvider.getBean(classType));
        Field[] fields=classInvocationHandler.getTargetClass().getDeclaredFields();

        for(Field field:fields){
            if(field.getAnnotation(RPCReference.class)!=null){
                field.setAccessible(true);
                field.set(classInvocationHandler.getTarget(),beanProvider.getBean(field.getType()));
            }
        }
    }

    public void graftAllBean(BeanProvider beanProvider){
        beanProvider.getRPCServiceList().stream().forEach(clazz->{
            try {
                graftBean(clazz,beanProvider);
            } catch (IllegalAccessException e) {
                log.error("bean initilize failed!");
            }
        });
    }
}
