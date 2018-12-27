package com.limon.core.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodInvocationHandler {

    private Method method;

    private List<ProxyHandler> proxyHandlerList;

    public MethodInvocationHandler(Method method,ProxyHandler proxyHandler){
        this.method=method;
        proxyHandlerList=new ArrayList<>();
        if(proxyHandler!=null)
            proxyHandlerList.add(proxyHandler);
    }

    public Object invoke(Object target,Object[] args) throws InvocationTargetException, IllegalAccessException {
        for(ProxyHandler proxyHandler:proxyHandlerList){
            proxyHandler.before();
        }
        Object rs =method.invoke(target,args);
        Collections.reverse(Arrays.asList(proxyHandlerList));
        for(ProxyHandler proxyHandler:proxyHandlerList){
            proxyHandler.after();
        }
        return rs;
    }
}
