package com.haha.im.service.impl;

import com.haha.im.service.UserMsgIdService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(prefix = "user_msg_id", name = "next_id_impl", havingValue = "redis")
public class UserMsgIdServiceRedisImpl implements UserMsgIdService {

    // todo 实现一个分布式的id生产器, 实现主备切换
    @Override
    public Long getNextMsgId(String userId) {
        return null;
    }
}
