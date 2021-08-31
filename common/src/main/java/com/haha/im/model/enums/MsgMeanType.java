package com.haha.im.model.enums;

/**
 *
 */
public enum MsgMeanType {
    DELIVERED(0, "已发送"),
    READ(1, "已读"),
    INIT(2, "创建连接"),
    ACK(3, "连接成功");

    MsgMeanType(int code, String msg) {
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
