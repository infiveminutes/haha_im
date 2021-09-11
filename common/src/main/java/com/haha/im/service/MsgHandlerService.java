package com.haha.im.service;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;

public interface MsgHandlerService {

    /**
     * handle msg
     * @param msg
     * @return
     */
    Proto handleMsg(Message msg);

    Class<? extends Message> handleMsgClazz();

}
