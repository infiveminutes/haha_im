package com.haha.im.service;

import com.haha.im.model.DO.UserMsgMaxIdDo;

public interface UserMsgMaxIdService {
    Long getUserMsgMaxIdFromDb(String userId);
    boolean setMaxId(String userId, Long maxId);
}
