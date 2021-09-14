package com.haha.im.connect;

import com.google.protobuf.Message;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;

public interface Connect {
    AttributeKey<String> NetId = AttributeKey.newInstance("net_id");

    String getUserId();
    String getNetId();
    void close();
    void sendMsg(Message msg);
}
