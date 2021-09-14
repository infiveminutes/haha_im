package com.haha.im.connect;

import io.netty.channel.ChannelHandlerContext;

public interface ConnectManager {
    void addConn(Connect conn);
    Connect getConn(String userId);
    void removeConn(String netId);
    void removeConn(Connect conn);
    Connect getConn(ChannelHandlerContext ctx);
}
