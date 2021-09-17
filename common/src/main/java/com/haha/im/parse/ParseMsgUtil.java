package com.haha.im.parse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.haha.im.model.enums.MsgType;
import com.haha.im.model.protobuf.Msg;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ParseMsgUtil {

    private Map<MsgType, Parse> parseMap = new HashMap<>();

    public ParseMsgUtil() {
        parseMap.put(MsgType.CHAT, Msg.ChatMsg::parseFrom);
        parseMap.put(MsgType.ACK, Msg.AckMsg::parseFrom);
        parseMap.put(MsgType.INTERNAL, Msg.InternalMsg::parseFrom);
    }

    public Message parse(int code, byte[] bytes) throws InvalidProtocolBufferException {
        MsgType type = MsgType.code2Enum(code);
        if(type == null) {
            return null;
        }
        return Optional.ofNullable(parseMap.get(type))
                .orElseThrow(()->new IllegalArgumentException(String.format("type %s doesn't have parser", code)))
                .process(bytes);
    }

    @FunctionalInterface
    public interface Parse {
        Message process(byte[] bytes) throws InvalidProtocolBufferException;
    }
}
