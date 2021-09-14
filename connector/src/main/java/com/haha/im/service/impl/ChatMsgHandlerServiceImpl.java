package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import com.haha.im.handler.ConnectorTransferHandler;
import com.haha.im.model.Proto;
import com.haha.im.model.protobuf.Msg;
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

    /**
     * handle chatMsg
     * if dest conn in this server, send the chat to dest
     * else send the chat to transfer
     * @param msg extend message.ack
     * @return
     */
    public Proto handleMsg(Message msg, ChannelHandlerContext ctx) {
        if(!checkStep(msg)) {
            return Proto.creatError("step over limit: " + getStep(msg));
        }
        String destId = getDestId(msg);
        Connect connect = connectManager.getConn(destId);
        if(connect == null) {
            // dest user's conn isn't in this machine, send the chat msg to transfer server
            ChannelHandlerContext randomCtx = ConnectorTransferHandler.randomTransferCtx();
            if(randomCtx == null) {
                logger.error("transfer connect list is empty, can't send chat msg to transfer server, {}", msg);
                return Proto.creatError("transfer connect list is empty");
            }
            randomCtx.channel().writeAndFlush(msg);
            return Proto.OK;
        }else {
            // dest user's conn is in this machine, send chat to dest user
            connect.sendMsg(msg);
        }
        return Proto.OK;
    }

    protected String getDestId(Message msg) {
        return String.valueOf(((Msg.ChatMsg) msg).getDestId());
    }

    protected int getStep(Message msg) {
        return ((Msg.ChatMsg)msg).getStep();
    }

    public Class<? extends Message> handleMsgClazz() {
        return Msg.ChatMsg.class;
    }
}
