package com.haha.im.parse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public interface ParseService {
    Message parse(int code, byte[] bytes) throws InvalidProtocolBufferException;
}
