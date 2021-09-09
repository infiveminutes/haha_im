package com.haha.im.service.impl;

import com.haha.im.service.UserMsgIdService;
import com.haha.im.service.UserMsgMaxIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 消息id生成器 内存版
 * 只支持单机部署
 */
@Service
@ConditionalOnProperty(prefix = "user_msg_id", name = "next_id_impl", havingValue = "memory")
public class UserMsgIdServiceMemoryImpl implements UserMsgIdService {

    private Logger logger = LoggerFactory.getLogger(UserMsgIdServiceMemoryImpl.class);

    @Value("${msgid.increase_step}")
    private long step;

    @Autowired
    private UserMsgMaxIdService userMsgMaxIdService;

    private ConcurrentHashMap<String, AtomicLong> userId2curId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> userId2MaxId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ReentrantLock> userId2Lock = new ConcurrentHashMap<>();  // 每个用户拥有一把锁

    private final Object lock = new Object();

    @Override
    public Long getNextMsgId(String userId) {
        if(userId == null) {
            return null;
        }
        AtomicLong curId = userId2curId.get(userId);
        Long maxId = userId2MaxId.get(userId);
        if(curId == null || maxId == null) {
            // 当内存中的curId或maxId不存在时, 去数据库中拉取maxId, 此过程需要加锁
            Long ret = null;
            ReentrantLock userLock = getLock(userId);
            userLock.lock();
            try{
                Long maxIdFromDb = userMsgMaxIdService.getUserMsgMaxIdFromDb(userId);
                userId2curId.put(userId, new AtomicLong(maxIdFromDb+1));
                ret = maxIdFromDb;
                maxIdFromDb += step;
                userMsgMaxIdService.setMaxId(userId, maxIdFromDb);
                userId2MaxId.put(userId, maxIdFromDb);
            }catch (Exception e) {
                logger.error("getNextMsgId", e);
            }finally {
                userLock.unlock();
            }
            return ret;
        }
        long nextId = curId.incrementAndGet();
        if(nextId < maxId) {
            return nextId;
        }
        ReentrantLock userLock = getLock(userId);
        userLock.lock();
        try{
            maxId = userId2MaxId.get(userId);
            if(maxId > nextId) {
                return nextId;
            }
            Long maxIdFromDb = userMsgMaxIdService.getUserMsgMaxIdFromDb(userId);
            if(maxIdFromDb > nextId) {
                return nextId;
            }
            long nextMaxId = nextId + step;
            userMsgMaxIdService.setMaxId(userId, nextMaxId);
            userId2MaxId.put(userId, nextMaxId);
        }catch (Exception e) {
            logger.error("getNextMsgId", e);
        }finally {
            userLock.unlock();
        }
        return nextId;
    }

    private ReentrantLock getLock(String userId) {
        ReentrantLock userLock = userId2Lock.get(userId);
        if(userLock == null) {
            synchronized (lock) {
                if(!userId2Lock.containsKey(userId)){
                    userLock = new ReentrantLock();
                    userId2Lock.put(userId, userLock);
                }else {
                    userLock = userId2Lock.get(userId);
                }
            }
        }
        return userLock;
    }
}
