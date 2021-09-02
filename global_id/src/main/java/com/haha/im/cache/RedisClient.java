package com.haha.im.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

@Component
public class RedisClient {
    private Logger logger = LoggerFactory.getLogger(RedisClient.class);

    @Value("redis.lock.expire")
    private String lockExpireMillisecond;

    @Autowired
    private JedisPool jedisPool;

    public String get(String key) {
        String value = null;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            value = jedis.get(key);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public String set(String key, String value) {
        Jedis jedis = null;
        String ans = null;
        try{
            jedis = jedisPool.getResource();
            ans = jedis.set(key, value);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return ans;
    }

    /**
     * get redis lock (the time unit is milliseconds)
     * @param key lock key
     * @param id for cas unlock
     * @param lockTime Maximum locked time
     * @param timeout Time to wait for other threads to release the lock
     * @return
     */
    public boolean lock(String key, String id, long lockTime, long timeout) {
        Jedis jedis = null;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        SetParams setParams = SetParams.setParams().nx().px(lockTime);
        try{
            while(true) {
                jedis = jedisPool.getResource();
                String ret = jedis.set(key, id, setParams);
                if("OK".equals(ret)) {
                    return true;
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                if(elapsedTime >= timeout) {
                    return false;
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            logger.error("get lock fail, {}, {}", key, id, e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * use lua script cas unlock
     * @param key
     * @param id
     * @return
     */
    public boolean unlock(String key, String id) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String script =
                    "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                            "   return redis.call('del',KEYS[1]) " +
                            "else" +
                            "   return 0 " +
                            "end";
            Object result = jedis.eval(script, Collections.singletonList(key),
                    Collections.singletonList(id));
            if("1".equals(result.toString())){
                return true;
            }
            return false;
        }catch (Exception e) {
            logger.error("unlock error, {}, {}", key, id, e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

}
