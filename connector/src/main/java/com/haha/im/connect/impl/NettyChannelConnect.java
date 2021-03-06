package com.haha.im.connect.impl;

import com.google.protobuf.Message;
import com.haha.im.connect.Connect;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyChannelConnect implements Connect {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannelConnect.class);
    private String netId;
    private String userId;
    private ChannelHandlerContext ctx;

    public NettyChannelConnect(String userId, ChannelHandlerContext ctx) {
        // todo 此处ctx为空的处理
        this.ctx = ctx;
        this.netId = String.valueOf(IDGenService.getNextNetId());
        this.userId = userId;
        ctx.channel().attr(NetId).set(this.netId);
    }


    public String getUserId() {
        return userId;
    }

    public String getNetId() {
        return netId;
    }

    @Override
    public String getConnectorId() {
        return null;
    }

    public void close() {
        closeChannel();
    }

    public void sendMsg(Message msg) {
        ctx.channel().writeAndFlush(msg);
    }

    public ChannelFuture closeChannel() {
        return ctx.channel().close();
    }
}
