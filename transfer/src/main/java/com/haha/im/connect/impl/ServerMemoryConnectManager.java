package com.haha.im.connect.impl;

import com.haha.im.connect.Connect;
import com.haha.im.connect.ConnectManager;
import com.haha.im.service.UserStatusService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerMemoryConnectManager implements ConnectManager {

    @Autowired
    private UserStatusService userStatusService;

    private final ConcurrentHashMap<String, Connect> connectId2Connect = new ConcurrentHashMap<>();

    @Override
    public void addConn(Connect conn) {
        if(conn == null || conn.getConnectorId() == null) {
            return;
        }
        String connectorId = conn.getConnectorId();
        Connect oldConn = connectId2Connect.get(connectorId);
        if(oldConn != null) {
            if(connectorId.equals(oldConn.getConnectorId())) {
                // already connected
                return;
            }else {
                oldConn.close();
            }
        }
        connectId2Connect.put(connectorId, conn);
    }

    @Override
    public Connect getConn(String userId) {
        String connectorId = userStatusService.getConnectorId(userId);
        if(StringUtils.isBlank(connectorId)) {
            return null;
        }
        return connectId2Connect.get(connectorId);
    }

    @Override
    public void removeConn(String netId) {

    }

    @Override
    public void removeConn(Connect conn) {

    }

    @Override
    public Connect getConn(ChannelHandlerContext ctx) {
        return null;
    }
}
