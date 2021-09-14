package com.haha.im.exception;

public class AckWindowFullException extends Exception{

    public AckWindowFullException(String netId) {
        super(String.format("netId:%s, ackWindow is full", netId));
    }
}
