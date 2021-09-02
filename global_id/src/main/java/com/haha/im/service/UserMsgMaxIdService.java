package com.haha.im.service;

public interface UserMsgMaxIdService {
    Long getUserMsgMaxIdFromDb(String userId);
    boolean setMaxId(String userId, long maxId);
}
