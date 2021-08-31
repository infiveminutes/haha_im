package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.service.MsgHandlerService;
import org.springframework.stereotype.Service;

@Service
public class ChatMsgHandlerServiceImpl implements MsgHandlerService {

    /**
     * handle chatMsg
     * if dest conn in this server, send the chat to dest
     * else send the chat to transfer
     * @param msg extend message.ack
     * @return
     */
    public Proto handlerMsg(Message msg) {
        return null;
    }

    public Class<? extends Message> handleMsgClazz() {
        return Msg.ChatMsg.class;
    }
}
