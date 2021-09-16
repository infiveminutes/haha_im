package com.haha.im;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.haha.im.model.enums.ModuleType;
import com.haha.im.model.enums.MsgMeanType;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.utils.HttpClientUtil;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.runtime.Bytes;

import java.util.HashMap;
import java.util.Map;

public class UserClient {

    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);

    private static final String MSG_ID = "msgId";
    private static final String GET_NEXT_MSG_ID_UTL = "localhost:8088/msg_id/next_id";

    private Long userId;
    private ChannelHandlerContext ctx;
    private boolean connectStatus = false;

    public void sendMsg(String fromId, String destId, String msg) {
        if(!checkConnect()) {
            logger.error(String.format("sendMsg fail, not connect, %s, %s, %s", fromId, destId, msg));
            return;
        }
        Msg.InternalMsg internalMsg = buildInitMsg()


    }

    private boolean checkConnect() {
        return connectStatus;
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

    private String getMsgId() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId+"");
        JSONObject result = HttpClientUtil.doHttpPost(GET_NEXT_MSG_ID_UTL, params);
        if(result == null || !result.containsKey(MSG_ID)) {
            logger.error(String.format("get next msg id error, %s", userId));
            return null;
        }
        return result.getString(MSG_ID);
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
