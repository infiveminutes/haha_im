package com.haha.im.handler;

import com.google.protobuf.Message;
import com.haha.im.client.UserClient;
import com.haha.im.model.enums.MsgMeanType;
import com.haha.im.model.protobuf.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnectorHandler extends SimpleChannelInboundHandler<Message> {
    private Logger logger = LoggerFactory.getLogger(ClientConnectorHandler.class);

    private UserClient userClient;

    public ClientConnectorHandler(UserClient userClient) {
        super();
        this.userClient = userClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if(message.getClass().equals(Msg.AckMsg.class)) {
            userClient.ackChat((Msg.AckMsg)message);
        }else if(message.getClass().equals(Msg.InternalMsg.class)) {
            Msg.InternalMsg internalMsg = (Msg.InternalMsg)message;
            if(internalMsg.getMsgType() != MsgMeanType.ACK.getCode()) {
                return;
            }
            userClient.ackInit((Msg.InternalMsg)message);
        }else {
            logger.error("channelRead0, can't handle this msg, "+message.getClass().toString());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        userClient.setCtx(ctx);
        super.channelActive(ctx);
    }


}
