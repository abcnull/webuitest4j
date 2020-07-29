package com.abcnull.util;

import com.abcnull.constant.TestConstant;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 操作 redis 的工具类
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/22
 */
@Slf4j
public class RedisUtil {
    /**
     * redis 连接池
     */
    private static JedisPool jedisPool;

    /**
     * jedis 连接
     */
    private Jedis jedis;

    /**
     * jedis 键值对默认过期时间（s）
     */
    private int jedisExpireTime;

    /**
     * 初始化 redis 连接池
     *
     * @return JedisPool
     */
    public static JedisPool initJedisPool() {
        /* redis 的连接参数 */
        String redisIp = PropertiesReader.getKey("redis.ip");
        int redisPort = Integer.parseInt(PropertiesReader.getKey("redis.port"));
        String redisPwd = PropertiesReader.getKey("redis.pwd");
        /* redis 连接池的配置参数 */
        int jedisPoolMaxTotal = Integer.parseInt(PropertiesReader.getKey("jedis.pool.maxTotal"));
        int jedisPoolMaxIdle = Integer.parseInt(PropertiesReader.getKey("jedis.pool.maxIdle"));
        int jedisPoolMaxWaitMillis = Integer.parseInt(PropertiesReader.getKey("jedis.pool.maxWaitMillis"));
        boolean jedisPoolTestOnBorrow = Boolean.parseBoolean(PropertiesReader.getKey("jedis.pool.testOnBorrow"));
        boolean jedisPoolTestOnReturn = Boolean.parseBoolean(PropertiesReader.getKey("jedis.pool.testOnReturn"));
        /* redis 连接池执行配置 */
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(jedisPoolMaxTotal);
        jedisPoolConfig.setMaxIdle(jedisPoolMaxIdle);
        jedisPoolConfig.setMaxWaitMillis(jedisPoolMaxWaitMillis);
        jedisPoolConfig.setTestOnBorrow(jedisPoolTestOnBorrow);
        jedisPoolConfig.setTestOnReturn(jedisPoolTestOnReturn);
        /* 连接 redis 服务器生成 redis 连接池 */
        try {
            log.info("开始初始化 redis 连接池");
            if (redisPwd.isEmpty()) {
                jedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, TestConstant.THREE_THOUSANG);
            } else {
                jedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, TestConstant.TEN_THOUSANG, redisPwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("redis 连接池生成失败！");
        }
        return jedisPool;
    }

    /**
     * 初始化 redis 连接
     *
     * @return jedis 连接
     */
    public Jedis initJedis() {
        // 若 jedis 已经有一个了就干掉
        if (jedis != null) {
            returnJedis();
        }
        // 产生新的 jedis
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从连接池中生成一个新的连接失败！");
        }
        // 默认过期时间
        String jedisExpireTimeStr = PropertiesReader.getKey("jedis.expireTime");
        if (jedisExpireTimeStr != null && !jedisExpireTimeStr.isEmpty()) {
            jedisExpireTime = Integer.parseInt(jedisExpireTimeStr);
        }
        log.info("redis 连接池生成成功并产生一个新的连接");
        return jedis;
    }

    /**
     * 获取 redis 连接池对象
     *
     * @return redis 连接池
     */
    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * 获取连接池中的 jedis 连接
     *
     * @return jedis 连接
     */
    public Jedis getJedis() {
        return jedis;
    }

    /**
     * 设置键值对
     *
     * @param key   键
     * @param value 值
     */
    public void setKey(String key, String value) {
        jedis.set(key, value);
        // 若配置中设置了默认过期时间
        if (jedisExpireTime != 0) {
            jedis.expire(key, jedisExpireTime);
        }
    }

    /**
     * 设置键值对
     *
     * @param key       键
     * @param value     值
     * @param seconds   过期时间
     */
    public void setKey(String key, String value, int seconds) {
        jedis.set(key, value);
        // 设置键值过期时间
        jedis.expire(key, seconds);
    }

    /**
     * 依据键得到值
     *
     * @param key 键
     * @return 值
     */
    public String getKey(String key) {
        return jedis.get(key);
    }

    /**
     * 归还 jedis 连接
     */
    public void returnJedis() {
        if (jedis != null) {
            jedis.close();
            jedis = null;
            jedisExpireTime = 0;
        }
        log.info("jedis 已归还！");
    }
}
