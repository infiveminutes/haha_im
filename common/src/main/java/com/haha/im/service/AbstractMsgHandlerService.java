package com.haha.im.service;

import com.google.protobuf.Message;
import com.haha.im.model.protobuf.Msg;

public abstract class AbstractMsgHandlerService implements MsgHandlerService{

    public static final int MAX_STEP = 3;

    @Override
    public Message checkStep(Message msg) {
        if(msg instanceof Msg.AckMsg) {
            Msg.AckMsg ackMsg = (Msg.AckMsg) msg;
            if(ackMsg.getStep() > MAX_STEP) {
                return null;
            }
            msg = Msg.AckMsg.newBuilder(ackMsg).setStep(ackMsg.getStep() + 1).build();
        }else if(msg instanceof Msg.ChatMsg) {
            Msg.ChatMsg chatMsg = (Msg.ChatMsg) msg;
            if(chatMsg.getStep() > MAX_STEP) {
                return null;
            }
            msg = Msg.ChatMsg.newBuilder(chatMsg).setStep(chatMsg.getStep() + 1).build();
        }
        return msg;
    }
}
