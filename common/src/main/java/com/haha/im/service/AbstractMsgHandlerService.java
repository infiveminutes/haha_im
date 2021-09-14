package com.haha.im.service;

import com.google.protobuf.Message;
import com.haha.im.model.protobuf.Msg;

public abstract class AbstractMsgHandlerService implements MsgHandlerService{

    public static final int MAX_STEP = 3;

    @Override
    public boolean checkStep(Message msg) {
        if(msg instanceof Msg.AckMsg) {
            return ((Msg.AckMsg) msg).getStep() <= MAX_STEP;
        }else if(msg instanceof Msg.ChatMsg) {
            return ((Msg.ChatMsg) msg).getStep() <= MAX_STEP;
        }
        return true;
    }
}
