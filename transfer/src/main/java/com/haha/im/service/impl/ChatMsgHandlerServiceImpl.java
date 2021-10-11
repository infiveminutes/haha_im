package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import com.haha.im.model.Proto;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.mq.Producer;
import com.haha.im.service.AbstractMsgHandlerService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMsgHandlerServiceImpl extends AbstractMsgHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(ChatMsgHandlerServiceImpl.class);

    @Autowired
    private ConnectManager connectManager;

    @Autowired
    private Producer producer;

    /**
     * handle chatMsg
     * if target user is online forward msg to connector
     * else put the chatMsg into mq and persist it
     * @param msg extend message.ack
     * @return
     */
    public Proto handleMsg(Message msg, ChannelHandlerContext ctx) {
        msg = checkStep(msg);
        if(msg == null) {
            return Proto.creatError("step over limit");
        }
        String userId = getDestId(msg);
        Connect connectorConn = connectManager.getConn(String.valueOf(userId));
        if(connectorConn == null) {
            // dest user is offline
            handleOffline(msg);
        }else{
            // dest user if online, send chatMsg to target connector
            connectorConn.sendMsg(msg);
        }
        return Proto.OK;
    }

    protected void handleOffline(Message msg) {
        producer.send(msg);
    }

    protected String getDestId(Message msg) {
        return String.valueOf(((Msg.ChatMsg) msg).getDestId());
    }

    public Class<? extends Message> handleMsgClazz() {
        return Msg.ChatMsg.class;
    }

}
