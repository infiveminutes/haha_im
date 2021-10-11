package com.haha.im.model.enums;

import com.google.protobuf.Message;
import com.haha.im.model.protobuf.Msg;

public enum MsgType {
    CHAT(0, "消息"),
    ACK(1, "回复"),
    INTERNAL(2, "内部消息")
    ;

    MsgType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    int code;
    String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static MsgType code2Enum(int code) {
        for(MsgType t: MsgType.values()) {
            if(t.getCode() == code) {
                return t;
            }
        }
        return null;
    }

    public static MsgType clazz2Enum(Class<?> clazz) {
        if(clazz == Msg.ChatMsg.class) {
            return CHAT;
        }else if(clazz == Msg.AckMsg.class) {
            return ACK;
        }else if(clazz == Msg.InternalMsg.class) {
            return INTERNAL;
        }else {
            return null;
        }
    }

}
