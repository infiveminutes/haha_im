package com.haha.im.connect;

import com.google.protobuf.Message;
import io.netty.channel.ChannelFuture;

public interface Connect {
    String getUserId();
    String getNetId();
    void close();
    void sendMsg(Message msg);
}
