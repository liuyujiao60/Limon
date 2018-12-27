package com.limon.test.impl;

import com.alibaba.fastjson.JSONObject;
import com.limon.core.annotation.Aop;
import com.limon.test.LogRecieverService;
import com.limon.test.model.ServerLog;
import com.limon.test.proxy.LogRecieverServiceProxyHandler;
import lombok.extern.slf4j.Slf4j;

@Aop(value = LogRecieverServiceProxyHandler.class)
@Slf4j
public class LogRecieverServiceImpl implements LogRecieverService {

    @Aop(value = LogRecieverServiceProxyHandler.class)
    @Override
    public void recieveLog(ServerLog serverLog) {
        log.info(JSONObject.toJSONString(serverLog));
    }
}
