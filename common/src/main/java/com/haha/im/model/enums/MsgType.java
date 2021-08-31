package com.haha.im.model.enums;

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

}