package com.haha.im.model;

public class Proto<T> {
    Integer code;
    String msg;
    T data;

    public Proto(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Proto(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Proto() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static final int OK_CODE = 0;
    public static final String OK_MSG = "OK";

    public static final int ERROR_CODE = 1000;
    public static final String ERROR_MSG = "error";

    public static final Proto OK = new Proto(OK_CODE, OK_MSG);
    public static final Proto ERROR = new Proto(OK_CODE, OK_MSG);

    public static<T> Proto<T> creatOK(T t) {
        return new Proto<T>(OK_CODE, OK_MSG, t);
    }

    @Override
    public String toString() {
        return "Proto{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
