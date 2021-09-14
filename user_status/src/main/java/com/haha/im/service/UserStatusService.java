package com.haha.im.service;

public interface UserStatusService {
    /**
     * register user online
     * @param userId
     * @param connectorId
     * @return true if the process successful
     */
    boolean online(String userId, String connectorId);

    /**
     * log off
     * @param userId
     * @param connectorId cas log off
     * @return true if the process successful
     */
    boolean offline(String userId, String connectorId);

    /**
     * get user connectorId
     * @param userId
     * @return connectorId if user status is online
     */
    String getConnectorId(String userId);
}
