package com.haha.im.service.impl;

import com.haha.im.service.UserMsgIdService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserMsgIdServiceMemoryImpl implements UserMsgIdService {

    @Value("msgid.increase_step")
    private long step;

    private ConcurrentHashMap<String, AtomicLong> userId2curId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicLong> userId2MaxId = new ConcurrentHashMap<>();

    @Override
    public Long getNextMsgId(String userId) {
        if(userId == null) {
            return null;
        }
        AtomicLong maxId = userId2MaxId.get(userId);
        if(maxId == null) {

        }
        return null;
    }
}
