package com.haha.im.connect;

public interface ConnectManager {
    void addConn(Connect conn);
    Connect getConn(String userId);
}
