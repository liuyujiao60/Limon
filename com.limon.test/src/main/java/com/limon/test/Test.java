package com.limon.test;

import com.limon.core.BeanRegister;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {

    public static void main(String[] args){
        BeanRegister beanRegister=new BeanRegister();

        try {
            beanRegister.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogSenderService logSenderService=beanRegister.getBean(LogSenderService.class);

        logSenderService.sendServerLog();
    }
}
