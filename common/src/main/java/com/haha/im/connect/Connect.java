package com.haha.im.connect;

import com.google.protobuf.Message;
import io.netty.util.AttributeKey;

public interface Connect {
    AttributeKey<String> NetId = AttributeKey.newInstance("net_id");
    // used in connector module
    String getUserId();
    String getNetId();
    // used in transfer module
    String getConnectorId();
    void close();
    void sendMsg(Message msg);
}
