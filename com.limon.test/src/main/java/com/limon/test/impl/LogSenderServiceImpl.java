package com.limon.test.impl;

import com.limon.core.annotation.RPCReference;
import com.limon.core.annotation.RPCService;
import com.limon.test.LogRecieverService;
import com.limon.test.LogSenderService;
import com.limon.test.model.ServerLog;

@RPCService
public class LogSenderServiceImpl implements LogSenderService {

    @RPCReference
    private LogRecieverService logRecieverService;

    @Override
    public void sendServerLog() {
        logRecieverService.recieveLog(new ServerLog("server-test01",8080));
    }
}
