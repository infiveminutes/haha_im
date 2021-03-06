package com.haha.im.handler;

import com.google.protobuf.Message;
import com.haha.im.service.MsgConsumerService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ConnectorClientHandler extends SimpleChannelInboundHandler<Message> {

    @Autowired
    private MsgConsumerService msgConsumerService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        msgConsumerService.process(msg, ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // todo 用户下线清理连接及修改用户状态
        super.channelInactive(ctx);
    }
}
