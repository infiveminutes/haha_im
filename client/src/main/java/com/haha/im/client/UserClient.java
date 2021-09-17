package com.haha.im.client;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.haha.im.ack.ClientAckWindow;
import com.haha.im.ack.ServerAckWindow;
import com.haha.im.model.enums.ModuleType;
import com.haha.im.model.enums.MsgMeanType;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.utils.HttpClientUtil;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class UserClient {

    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);

    private static final String MSG_ID = "msgId";
    private static final String GET_NEXT_MSG_ID_UTL = "http://localhost:8088/msg_id/next_id";

    private final Long userId;
    private ChannelHandlerContext ctx;
    private boolean connectStatus;
    private ClientAckWindow clientAckWindow;
    private ServerAckWindow serverAckWindow;
    private ExecutorService executorService;


    public UserClient(Long userId, Integer retry, Integer windowSize, Long timeout) {
        this.userId = userId;
        this.connectStatus = true;
        this.clientAckWindow = new ClientAckWindow("client", windowSize, timeout, retry);
        this.serverAckWindow = new ServerAckWindow("client", windowSize, timeout, retry);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void sendMsg(long destId, String msg) {
        if(!checkConnect()) {
            logger.error(String.format("sendMsg fail, not connect, %s, %s, %s", userId, destId, msg));
            return;
        }
        Long msgId = getMsgId();
        if(msgId == null) {
            logger.error(String.format("sendMsg fail, get msgId fail, %s, %s, %s", userId, destId, msg));
            return;
        }
        Msg.ChatMsg chatMsg = buildChatMsg(destId, msgId, msg);
        CompletableFuture<Msg.ChatMsg> future = clientAckWindow.offer(chatMsg, ctx);
        future.thenRunAsync(()->{
            try {
                System.out.println("------msg ack------:"+future.get().getMsgBody());
            } catch (Exception e) {
                logger.error("sendMsg", e);
            }
        });
    }

    private boolean checkConnect() {
        return connectStatus;
    }

    public boolean connect() {
        Msg.InternalMsg msg = buildInitMsg();
        CompletableFuture<Msg.InternalMsg> future = serverAckWindow.offer(msg, ctx);
        try {
            Msg.InternalMsg result = future.get(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("connect error", e);
            return false;
        }
        return true;
    }

    public void ackChat(Msg.AckMsg ackMsg) {
        clientAckWindow.ack(ackMsg);
    }

    public void ackInit(Msg.InternalMsg internalMsg) {
        serverAckWindow.ack(internalMsg);
    }


    private Msg.ChatMsg buildChatMsg(long destId, long msgId, String msg) {
        return Msg.ChatMsg.newBuilder()
                .setId(msgId)
                .setCreateTime(System.currentTimeMillis())
                .setFromId(userId)
                .setDestId(destId)
                .setStep(0)
                .setMsgBody(ByteString.copyFromUtf8(msg))
                .build();
    }

    private Long getMsgId() {
        JSONObject params = new JSONObject();
        params.put("userId", userId+"");
        JSONObject result = HttpClientUtil.doHttpPost(GET_NEXT_MSG_ID_UTL, params.toJSONString());
        if(result == null || !result.containsKey(MSG_ID)) {
            logger.error(String.format("get next msg id error, %s", userId));
            return null;
        }
        return result.getLong(MSG_ID);
    }

    private Msg.InternalMsg buildInitMsg() {
        return Msg.InternalMsg.newBuilder()
                .setId(IDGenService.getSnowFlakeId())
                .setFrom(ModuleType.CLIENT.getCode())
                .setDest(ModuleType.CONNECTOR.getCode())
                .setCreateTime(System.currentTimeMillis())
                .setMsgType(MsgMeanType.INIT.getCode())
                .setMsgBody(ByteString.copyFromUtf8(userId+""))
                .build();
    }




}
