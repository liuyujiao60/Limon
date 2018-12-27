package com.limon.core.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClassInvocationHandler implements InvocationHandler {

    private Class targetType;

    private Object target;

    private ProxyHandler proxyHandler;

    private Map<String,MethodInvocationHandler> methodInvocationHandlerMap=new HashMap<>();

    public ClassInvocationHandler(Object target) throws InstantiationException, IllegalAccessException {
        this.target=target;
        this.targetType=target.getClass();
        this.proxyHandler=new DefaultProxyHandler();
        for(Method method:this.targetType.getMethods()){
            methodInvocationHandlerMap.put(method.getName(),ProxyHandlerFactory.createMethodInvocationHandler(method));
        }
    }

    public ClassInvocationHandler(Object target, ProxyHandler proxyHandler) throws InstantiationException, IllegalAccessException {
        this.target=target;
        this.targetType=target.getClass();
        this.proxyHandler=proxyHandler;
        for(Method method:this.targetType.getDeclaredMethods()){
            methodInvocationHandlerMap.put(method.getName(),ProxyHandlerFactory.createMethodInvocationHandler(method));
        }
    }

    public void setProxyHandler(ProxyHandler proxyHandler){
        this.proxyHandler=proxyHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        proxyHandler.before();
        Object rs = methodInvocationHandlerMap.get(method.getName()).invoke(target,args);
        proxyHandler.after();
        return rs;
    }

    public Class getTargetClass(){
        return targetType;
    }

    public Object getTarget(){
        return target;
    }
}
