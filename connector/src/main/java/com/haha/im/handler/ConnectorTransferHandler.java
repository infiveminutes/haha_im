package com.haha.im.handler;

import com.google.protobuf.Message;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectorTransferHandler extends SimpleChannelInboundHandler<Message> {

    private Logger logger = LoggerFactory.getLogger(ConnectorTransferHandler.class);

    private static final ConcurrentHashMap<String, ChannelHandlerContext> netId2ctx = new ConcurrentHashMap<String, ChannelHandlerContext>();
    private static final CopyOnWriteArrayList<String> netIdList = new CopyOnWriteArrayList<String>();
    private static final AttributeKey<String> NetId = AttributeKey.newInstance("net_id");

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String netId = String.valueOf(IDGenService.getNextNetId());
        ctx.channel().attr(NetId).set(netId);
        netId2ctx.put(netId, ctx);
        netIdList.add(netId);
        logger.info("channel active connector and transfer, netId:" + netId);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String netId = ctx.channel().attr(NetId).get();
        if(netId != null) {
            netId2ctx.remove(netId);
            netIdList.remove(netId);
        }
        super.channelInactive(ctx);
    }

    public static ChannelHandlerContext randomTransferCtx() {
        String netId = null;
        try{
            // Make the first transfer server the default server
            netId = netIdList.get(0);
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
        int size = netIdList.size();
        int index = (int)System.currentTimeMillis() % size;
        try{
            netId = netIdList.get(index);
            return netId2ctx.get(netId);
        }catch (IndexOutOfBoundsException e) {
            return netId2ctx.get(netId);
        }
    }
}
