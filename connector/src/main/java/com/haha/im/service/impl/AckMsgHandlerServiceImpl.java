package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.model.protobuf.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AckMsgHandlerServiceImpl extends ChatMsgHandlerServiceImpl {

    private Logger logger = LoggerFactory.getLogger(AckMsgHandlerServiceImpl.class);

    @Override
    protected String getDestId(Message msg) {
        return String.valueOf(((Msg.AckMsg) msg).getDestId());
    }

    public Class<? extends Message> handleMsgClazz() {
        return Msg.AckMsg.class;
    }
}
