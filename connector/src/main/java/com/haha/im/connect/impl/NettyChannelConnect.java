package com.haha.im.connect.impl;

import com.haha.im.connect.Connect;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class NettyChannelConnect implements Connect {

    private static final AttributeKey<String> NetId = AttributeKey.newInstance("net_id");

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
}
