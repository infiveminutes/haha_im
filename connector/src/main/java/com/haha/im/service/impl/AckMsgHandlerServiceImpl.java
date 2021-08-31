package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;
import com.haha.im.service.MsgHandlerService;
import org.springframework.stereotype.Service;

@Service
public class AckMsgHandlerServiceImpl implements MsgHandlerService {
    public Proto handlerMsg(Message msg) {
        return null;
    }

    public Class<? extends Message> handleMsgClazz() {
        return null;
    }
}
