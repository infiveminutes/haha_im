package com.haha.im.connect;

public interface ConnectManager {
    void addConn(Connect conn);
    Connect getConn(String userId);
    void removeConn(String netId);
    void removeConn(Connect conn);
}
