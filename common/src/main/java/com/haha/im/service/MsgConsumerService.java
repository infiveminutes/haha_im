package com.haha.im.service;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;
import io.netty.channel.ChannelHandlerContext;

public interface MsgConsumerService {

    void initConsumer();

    boolean checkMsg(Message msg);

    Proto process(Message msg, ChannelHandlerContext ctx);
}
