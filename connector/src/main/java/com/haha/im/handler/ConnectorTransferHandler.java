package com.haha.im.handler;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.haha.im.ack.ServerAckWindow;
import com.haha.im.connect.Connect;
import com.haha.im.model.enums.ModuleType;
import com.haha.im.model.enums.MsgMeanType;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.service.MsgConsumerService;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@Scope("prototype")
public class ConnectorTransferHandler extends SimpleChannelInboundHandler<Message> {
    // 多例

    @Autowired
    private MsgConsumerService msgConsumerService;

    private static final Logger logger = LoggerFactory.getLogger(ConnectorTransferHandler.class);

    public static final String CONNECTOR_ID = String.valueOf(IDGenService.getSnowFlakeId());

    public static final Executor executor = Executors.newSingleThreadExecutor();

    private static final ConcurrentHashMap<String, ChannelHandlerContext> netId2ctx = new ConcurrentHashMap<String, ChannelHandlerContext>();
    private static final CopyOnWriteArrayList<String> netIdList = new CopyOnWriteArrayList<String>();

    private ServerAckWindow serverAckWindow;

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        msgConsumerService.process(message, channelHandlerContext);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String netId = String.valueOf(IDGenService.getNextNetId());
        serverAckWindow = new ServerAckWindow(netId, 5, 5000L, 3);
        ctx.channel().attr(Connect.NetId).set(netId);
        netId2ctx.put(netId, ctx);
        logger.info("channel active connector and transfer, netId:" + netId);
        // this netId will not be used until the server return ack
        Msg.InternalMsg msg = buildInitMsg();
        CompletableFuture<Msg.InternalMsg> f = serverAckWindow.offer(msg, ctx);
        f.thenRunAsync(()->netIdList.add(netId), executor);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String netId = ctx.channel().attr(Connect.NetId).get();
        if(netId != null) {
            netId2ctx.remove(netId);
            netIdList.remove(netId);
        }
        super.channelInactive(ctx);
    }

    private Msg.InternalMsg buildInitMsg() {
        return Msg.InternalMsg.newBuilder()
                .setId(IDGenService.getSnowFlakeId())
                .setFrom(ModuleType.CONNECTOR.getCode())
                .setDest(ModuleType.TRANSFER.getCode())
                .setCreateTime(System.currentTimeMillis())
                .setMsgType(MsgMeanType.INIT.getCode())
                .setMsgBody(ByteString.copyFromUtf8(CONNECTOR_ID))
                .build();
    }

    public static ChannelHandlerContext randomTransferCtx() {
        String netId = null;
        int size = netIdList.size();
        if(size == 0) {
            return null;
        }
        try{
            // Make the first transfer server the default server
            netId = netIdList.get(0);
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
        int index = (int)System.currentTimeMillis() % size;
        try{
            netId = netIdList.get(index);
            return netId2ctx.get(netId);
        }catch (IndexOutOfBoundsException e) {
            return netId2ctx.get(netId);
        }
    }
}
