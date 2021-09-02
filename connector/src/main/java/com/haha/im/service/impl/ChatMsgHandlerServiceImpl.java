package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import com.haha.im.model.Proto;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.service.MsgHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMsgHandlerServiceImpl implements MsgHandlerService {

    @Autowired
    private ConnectManager connectManager;

    /**
     * handle chatMsg
     * if dest conn in this server, send the chat to dest
     * else send the chat to transfer
     * @param msg extend message.ack
     * @return
     */
    public Proto handlerMsg(Message msg) {
        String destId = String.valueOf(((Msg.ChatMsg) msg).getDestId());
        Connect connect = connectManager.getConn(destId);
        if(connect == null) {
            // dest user's conn isn't in this machine, send the chat msg to transfer server

        }else {
            // dest user's conn is in this machine, send chat to dest user
            connect.sendMsg(msg);
        }
        return null;
    }

    private void sendChatMsg(Msg.ChatMsg chat, Connect conn) {
        Msg.ChatMsg.newBuilder().mergeFrom(chat).setId()
    }

    public Class<? extends Message> handleMsgClazz() {
        return Msg.ChatMsg.class;
    }
}
