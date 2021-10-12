package com.haha.im.mq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.haha.im.model.enums.MsgType;
import com.haha.im.parse.ParseMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqMsgDecode {

    private static final Logger logger = LoggerFactory.getLogger(MqMsgDecode.class);

    public static Message decode(byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }
        int msgTypeCode = bytes[0];
        Class<?> clazz = MsgType.code2Clazz(msgTypeCode);
        if(clazz == null) {
            return null;
        }
        byte[] target = new byte[bytes.length-1];
        System.arraycopy(bytes, 1, target, 0, target.length);
        try{
            return ParseMsgUtil.parse(msgTypeCode, target);
        }catch (InvalidProtocolBufferException e) {
            logger.error("decode error", e);
        }
        return null;
    }
}
