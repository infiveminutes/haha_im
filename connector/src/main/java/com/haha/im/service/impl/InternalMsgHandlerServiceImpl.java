package com.haha.im.service.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.haha.im.ack.ServerAckWindow;
import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import com.haha.im.connect.impl.NettyChannelConnect;
import com.haha.im.handler.ConnectorTransferHandler;
import com.haha.im.model.Proto;
import com.haha.im.model.enums.ModuleType;
import com.haha.im.model.enums.MsgMeanType;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.service.AbstractMsgHandlerService;
import com.haha.im.service.UserStatusService;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalMsgHandlerServiceImpl extends AbstractMsgHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(InternalMsgHandlerServiceImpl.class);

    @Autowired
    private ConnectManager connectManager;

    @Autowired
    private UserStatusService userStatusService;

    public Proto handleMsg(Message msg, ChannelHandlerContext ctx) {
        Msg.InternalMsg internalMsg = (Msg.InternalMsg) msg;
        if(!checkMsg(internalMsg)) {
            return Proto.creatError("check internal msg fail");
        }
        String userId = internalMsg.getMsgBody().toStringUtf8();
        return null;
    }

    public Class<? extends Message> handleMsgClazz() {
        return Msg.InternalMsg.class;
    }

    private Proto handleInitMsg(Msg.InternalMsg msg, ChannelHandlerContext ctx) {
        // receive init msg from client
        try{
            String userId = msg.getMsgBody().toStringUtf8();
            userStatusService.online(userId, ConnectorTransferHandler.CONNECTOR_ID);
            Connect connect = new NettyChannelConnect(userId, ctx);
            connectManager.addConn(connect);
            sendAckToClient(connect, msg.getId()+"");
            return Proto.OK;
        }catch (Exception e) {
            logger.error("handleInitMsg error", e);
            return Proto.creatError(e.getMessage());
        }
    }

    private Proto handleAckMsg(Msg.InternalMsg msg, ChannelHandlerContext ctx) {
        // receive ack msg from transfer
        // receive this msg only if connector send a init msg to transfer
        Connect conn = connectManager.getConn(ctx);
        if(conn == null) {
            return Proto.creatError()
        }
        ServerAckWindow.getAckWindowByNetId()
    }

    private boolean checkMsg(Msg.InternalMsg msg) {
        if(msg.getDest() != ModuleType.CONNECTOR.getCode()) {
            return false;
        }
        if(msg.getFrom() == ModuleType.TRANSFER.getCode()) {
            return msg.getMsgType() == MsgMeanType.ACK.getCode();
        }else if(msg.getFrom() == ModuleType.CLIENT.getCode()){
            return msg.getMsgType() == MsgMeanType.INIT.getCode();
        }
        return false;
    }

    private void sendAckToClient(Connect connect, String msgId) {
        Msg.InternalMsg ack = Msg.InternalMsg.newBuilder()
                .setId(IDGenService.getSnowFlakeId())
                .setFrom(ModuleType.CONNECTOR.getCode())
                .setDest(ModuleType.CLIENT.getCode())
                .setCreateTime(System.currentTimeMillis())
                .setMsgType(MsgMeanType.ACK.getCode())
                .setMsgBody(ByteString.copyFromUtf8(msgId))
                .build();
        connect.sendMsg(ack);
    }
}
