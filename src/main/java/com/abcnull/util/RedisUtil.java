package com.abcnull.util;

import com.abcnull.constant.TestConstant;
import lombok.extern.slf4j.Slf4j;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
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
     * redis 服务器 ip
     */
    private static String redisIp;

    /**
     * redis 服务器端口号
     */
    private static int redisPort;

    /**
     * redis 服务器连接密码
     */
    private static String redisPwd;

    /**
     * redis 连接池配置
     */
    private static JedisPoolConfig jedisPoolConfig;

    /**
     * redis 连接池
     */
    private static JedisPool jedisPool;

    /**
     * jedis 连接
     */
    private Jedis jedis;

    /**
     * jedis 键值超时时间
     */
    private int jedisExpireTime;

    /**
     * jedis 最大分配对象数量
     */
    private static int jedisPoolMaxTotal;

    /**
     * jedis 最大保存 idel 状态对象数量
     */
    private static int jedisPoolMaxIdle;

    /**
     * jedis 连接池没有对象返回时最大等待时间
     */
    private static int jedisPoolMaxWaitMillis;

    /**
     * jedis 调用 borrowObject 方法时，是否进行有效检查
     */
    private static boolean jedisPoolTestOnBorrow;

    /**
     * jedis 调用 returnObject 方法时，是否进行有效检查
     */
    private static boolean jedisPoolTestOnReturn;

    /**
     * 本地线程存储用来存 Jedis 连接
     */
    public static ThreadLocal<Jedis> threadLocal = new ThreadLocal<>();

    /*
     * 静态块，在类的加载初期执行
     * redis 连接池初始化操作
     */
    static {
        /* redis 的配置参数 */
        redisIp = PropertiesReader.getKey("redis.ip");
        redisPort = Integer.valueOf(PropertiesReader.getKey("redis.port"));
        redisPwd = PropertiesReader.getKey("redis.pwd");
        /* redis 连接池的配置参数 */
        jedisPoolMaxTotal = Integer.valueOf(PropertiesReader.getKey("jedis.pool.maxTotal"));
        jedisPoolMaxIdle = Integer.valueOf(PropertiesReader.getKey("jedis.pool.maxIdle"));
        jedisPoolMaxWaitMillis = Integer.valueOf(PropertiesReader.getKey("jedis.pool.maxWaitMillis"));
        jedisPoolTestOnBorrow = Boolean.valueOf(PropertiesReader.getKey("jedis.pool.testOnBorrow"));
        jedisPoolTestOnReturn = Boolean.valueOf(PropertiesReader.getKey("jedis.pool.testOnReturn"));
        /* redis 连接池开始配置 */
        jedisPoolConfig = new JedisPoolConfig();
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
     * 获取连接池中新的 jedis 连接
     *
     * @return jedis 连接
     */
    public Jedis getNewJedis() {
        Jedis newJedis = null;
        try {
            newJedis = jedisPool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("redis 连接池生成失败！");
        }
        log.info("redis 连接池生成成功并产生一个新的连接");
        return newJedis;
    }

    /**
     * 获取连接池中的 jedis 连接
     *
     * @return jedis 连接
     */
    public Jedis getJedis() {
        return jedis;
    }

    public void setJedisAndExpire(Jedis jedis) {
        this.jedis = jedis;
        threadLocal.set(jedis);
        // jedis 键值对超时时间(s)
        jedisExpireTime = Integer.valueOf(PropertiesReader.getKey("jedis.expireTime"));
        log.info("redisUtil 设置了一个 redis 连接");
    }

    /**
     * 设置键值对
     *
     * @param key   键
     * @param value 值
     */
    public void setKey(String key, String value) {
        jedis.set(key, value);
        // 设置键值过期时间为 1 h
        jedis.expire(key, jedisExpireTime);
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
        }
        log.info("jedis 已归还！");
    }
}
