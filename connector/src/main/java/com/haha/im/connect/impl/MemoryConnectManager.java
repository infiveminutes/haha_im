package com.haha.im.connect.impl;

import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryConnectManager implements ConnectManager {

    private final ConcurrentHashMap<String, Connect> netId2Conn = new ConcurrentHashMap<String, Connect>();
    private final ConcurrentHashMap<String, String> userId2NetId = new ConcurrentHashMap<String, String>();

    /**
     * if user already has a conn,
     * close it
     * @param conn
     */
    public void addConn(Connect conn) {
        if(conn == null) {
            return;
        }
        String userId = conn.getUserId();
        String netId = conn.getNetId();
        String oldNetId = userId2NetId.get(userId);
        if(oldNetId != null && !oldNetId.equals(netId)) {
            Connect oldConn = netId2Conn.get(oldNetId);
            if(oldConn != null) {
                oldConn.close();
            }
        }
        userId2NetId.put(userId, netId);
        netId2Conn.put(netId, conn);
    }

    public Connect getConn(String userId) {
        String netId = userId2NetId.get(userId);
        if(netId == null) {
            return null;
        }
        return netId2Conn.get(netId);
    }

    public void removeConn(String netId) {
        if(netId == null){
            return;
        }
        Connect conn = netId2Conn.remove(netId);
        if(conn != null) {
            String userId = conn.getUserId();
            userId2NetId.remove(userId);
            conn.close();
        }
    }

    public void removeConn(Connect conn) {
        if(conn == null) {
            return;
        }
        String userId = conn.getUserId();
        String netId = conn.getNetId();
        userId2NetId.remove(userId);
        netId2Conn.remove(netId);
        conn.close();
    }
}
