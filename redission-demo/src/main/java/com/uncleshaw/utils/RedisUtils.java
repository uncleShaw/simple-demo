package com.uncleshaw.utils;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.concurrent.TimeUnit;


public class RedisUtils {

    private static final RedissonClient redissonClient;

    static {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://127.0.0.1:6379");
        singleServerConfig.setDatabase(3);
        singleServerConfig.setPassword("shaw");
        redissonClient = Redisson.create(config);
    }

    /**
     * 获取一个 key
     */
    public String getString(String key) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        return result.get() == null ? null : result.get().toString();
    }


    /**
     * 获取一个key的过期时间
     */
    public long getTime(String key) {
        return redissonClient.getPermitExpirableSemaphore(key).remainTimeToLive();
    }


    /**
     * 设置一个 key vlaue的值
     */
    public void setString(String key, Object value) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        if (!result.isExists()) {
            result.set(value);
        }
    }


    /**
     * 设置key的过期时间 time为秒单位
     */
    public void setStringTime(String key, Object value, Long time) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        if (!result.isExists()) {
            result.set(value, time, TimeUnit.SECONDS);
        }
    }


    /**
     * 判断这个key是否存在
     */
    public boolean hasString(String key) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        return result.isExists();
    }


    /**
     * 删除一个key
     */
    public boolean delString(String key) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        return result.delete();
    }


    /**
     * 自增
     */
    public long incr(String key, long delta) {
        return this.redissonClient.getAtomicLong(key).addAndGet(delta);
    }

    /**
     * 自增+1
     */
    public long incr(String key) {
        return this.redissonClient.getAtomicLong(key).incrementAndGet();
    }

    /**
     * 设置AtomicLong的值
     *
     * @param key
     * @param data
     */
    public void setAtomicLong(String key, Long data) {
        this.redissonClient.getAtomicLong(key).set(data);
    }

}