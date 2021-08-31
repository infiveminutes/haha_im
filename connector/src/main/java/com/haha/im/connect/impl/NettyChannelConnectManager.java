package com.haha.im.connect.impl;

import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class NettyChannelConnectManager implements ConnectManager {

    private final ConcurrentHashMap<String, NettyChannelConnect> netId2Conn = new ConcurrentHashMap<String, NettyChannelConnect>();
    private final ConcurrentHashMap<String, String> userId2NetId = new ConcurrentHashMap<String, String>();

    public void addConn(Connect conn) {
        if(conn == null) {
            return;
        }
        String userId = conn.getUserId();
        String netId = conn.getNetId();

    }

    public Connect getConn(String userId) {
        return null;
    }
}
