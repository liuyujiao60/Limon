package com.limon.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanRegister {

    private volatile BeanProvider beanProvider;

    public void init() throws Exception {
        if(beanProvider==null){
            beanProvider=new BeanProvider("com.limon.test");
        }
    }

    public <T> T getBean(Class<T> tClass){
        return this.beanProvider.getBean(tClass);
    }
}
