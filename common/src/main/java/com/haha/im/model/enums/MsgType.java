package com.haha.im.model.enums;

import com.haha.im.model.protobuf.Msg;

public enum MsgType {
    CHAT(0, "消息", Msg.ChatMsg.class),
    ACK(1, "回复", Msg.AckMsg.class),
    INTERNAL(2, "内部消息", Msg.InternalMsg.class)
    ;

    MsgType(int code, String msg, Class<?> clazz) {
        this.code = code;
        this.msg = msg;
        this.clazz = clazz;
    }

    int code;
    String msg;
    Class<?> clazz;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Class<?> getClazz() {
        return clazz;
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
        for(MsgType msgType: values()) {
            if(msgType.getClazz() == clazz){
                return msgType;
            }
        }
        return null;
    }

    public static Class<?> code2Clazz(int code) {
        for(MsgType msgType: values()) {
            if(msgType.getCode() == code){
                return msgType.getClazz();
            }
        }
        return null;
    }

}
