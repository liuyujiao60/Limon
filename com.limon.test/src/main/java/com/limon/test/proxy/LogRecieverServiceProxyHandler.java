package com.limon.test.proxy;

import com.limon.core.proxy.ProxyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogRecieverServiceProxyHandler implements ProxyHandler {
    @Override
    public void before() {
        log.info("server log will write into disk!");
    }

    @Override
    public void after() {
        log.info("server log has been written into disk!");
    }
}
