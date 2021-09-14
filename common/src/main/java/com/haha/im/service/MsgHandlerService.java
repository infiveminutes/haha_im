package com.haha.im.service;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;
import io.netty.channel.ChannelHandlerContext;

public interface MsgHandlerService {

    /**
     * handle msg
     * @param msg
     * @return
     */
    Proto handleMsg(Message msg, ChannelHandlerContext ctx);

    Class<? extends Message> handleMsgClazz();

    /**
     * ensure the msg step smaller than MAX_STEP
     * @param msg
     * @return true if pass the check
     */
    boolean checkStep(Message msg);

}
