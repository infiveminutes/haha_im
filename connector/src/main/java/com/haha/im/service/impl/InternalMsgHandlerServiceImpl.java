package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.connect.ConnectManager;
import com.haha.im.model.Proto;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.service.MsgHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalMsgHandlerServiceImpl implements MsgHandlerService {

    private Logger logger = LoggerFactory.getLogger(InternalMsgHandlerServiceImpl.class);

    @Autowired
    private ConnectManager connectManager;

    public Proto handleMsg(Message msg) {
        Msg.InternalMsg internalMsg = (Msg.InternalMsg) msg;
        String userId = internalMsg.getMsgBody().toStringUtf8();
        return null;
    }

    public Class<? extends Message> handleMsgClazz() {
        return Msg.InternalMsg.class;
    }
}
