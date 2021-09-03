package com.haha.im.service.impl;

import com.haha.im.service.UserMsgIdService;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

//@Service
public class UserMsgIdServiceRedisImpl implements UserMsgIdService {



    @Override
    public Long getNextMsgId(String userId) {
        return null;
    }
}

class User {
    private String userId;
    private AtomicLong curId;
    private Long maxId;

}
