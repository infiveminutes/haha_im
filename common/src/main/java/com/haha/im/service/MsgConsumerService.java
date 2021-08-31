package com.haha.im.service;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;

public interface MsgConsumerService {

    void initConsumer();

    boolean checkMsg(Message msg);

    Proto process(Message msg);
}
