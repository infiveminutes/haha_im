package com.haha.im.connect.impl;

import com.google.protobuf.Message;
import com.haha.im.connect.Connect;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerNettyChannelConnect implements Connect {

    private static final Logger logger = LoggerFactory.getLogger(ServerNettyChannelConnect.class);
    private String netId;
    private String connectorId;
    private ChannelHandlerContext ctx;

    public ServerNettyChannelConnect(String connectorId, ChannelHandlerContext ctx) {
        // todo 此处ctx为空的处理
        this.ctx = ctx;
        this.netId = String.valueOf(IDGenService.getNextNetId());
        this.connectorId = connectorId;
        ctx.channel().attr(NetId).set(this.netId);
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getNetId() {
        return netId;
    }

    @Override
    public String getConnectorId() {
        return connectorId;
    }

    @Override
    public void close() {
        ctx.channel().close();
    }

    @Override
    public void sendMsg(Message msg) {
        ctx.channel().writeAndFlush(msg);
    }
}
