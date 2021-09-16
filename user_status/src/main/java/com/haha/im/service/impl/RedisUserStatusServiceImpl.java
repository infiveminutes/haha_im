package com.haha.im.service.impl;

import com.haha.im.cache.RedisClient;
import com.haha.im.service.UserStatusService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource( value = "classpath:application.yml")
public class RedisUserStatusServiceImpl implements UserStatusService {

    @Value("${user_status.redis.prefix}")
    private String redisPrefix;

    @Autowired
    private RedisClient redisClient;

    public boolean online(String userId, String connectorId) {
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(connectorId)) {
            return false;
        }
        String key = redisPrefix + userId;
        redisClient.set(key, connectorId);
        return false;
    }

    public boolean offline(String userId, String connectorId) {
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(connectorId)) {
            return false;
        }
        String key = redisPrefix + userId;
        redisClient.casDel(key, connectorId);
        return true;
    }

    public String getConnectorId(String userId) {
        if(StringUtils.isBlank(userId)) {
            return "";
        }
        String key = redisPrefix + userId;
        return redisClient.get(key);
    }
}
