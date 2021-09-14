package com.haha.im.model.enums;

public enum ModuleType {

    CONNECTOR(0, "connector"),
    TRANSFER(1, "transfer"),
    CLIENT(2, "client");

    ModuleType(int code, String msg) {
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
