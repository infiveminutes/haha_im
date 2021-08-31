package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;
import com.haha.im.service.MsgHandlerService;

public class InternalMsgHandlerServiceImpl implements MsgHandlerService {
    public Proto handlerMsg(Message msg) {
        return null;
    }

    public Class<? extends Message> handleMsgClazz() {
        return null;
    }
}
