package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import com.haha.im.connect.impl.ServerNettyChannelConnect;
import com.haha.im.model.Proto;
import com.haha.im.model.enums.ModuleType;
import com.haha.im.model.enums.MsgMeanType;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.service.MsgHandlerService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalMsgHandlerServiceImpl implements MsgHandlerService {
    private static final Logger logger = LoggerFactory.getLogger(InternalMsgHandlerServiceImpl.class);

    @Autowired
    private ConnectManager connectManager;

    @Override
    public Proto handleMsg(Message msg, ChannelHandlerContext ctx) {
        // only receive internalMsg(init) from connector
        Msg.InternalMsg internalMsg = (Msg.InternalMsg) msg;
        if(!checkMsg(internalMsg)) {
            return Proto.creatError("check internal msg fail");
        }
        if(internalMsg.getMsgType() == MsgMeanType.INIT.getCode()) {
            handleInitMsg(internalMsg, ctx);
        }
        return null;
    }

    @Override
    public Class<? extends Message> handleMsgClazz() {
        return Msg.InternalMsg.class;
    }

    @Override
    public Message checkStep(Message msg) {
        return msg;
    }

    private boolean checkMsg(Msg.InternalMsg internalMsg) {
        return internalMsg != null && internalMsg.getMsgType() == MsgMeanType.INIT.getCode() && internalMsg.getFrom() == ModuleType.CONNECTOR.getCode();
    }

    private void handleInitMsg(Msg.InternalMsg internalMsg, ChannelHandlerContext ctx) {
        String connectorId = internalMsg.getMsgBody().toStringUtf8();
        if(StringUtils.isBlank(connectorId)) {
            logger.error("handleInitMsg error, connectorId is blank");
        }
        Connect connect = new ServerNettyChannelConnect(connectorId, ctx);
        connectManager.addConn(connect);
    }
}
