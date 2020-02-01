package com.abcnull.base;

import com.abcnull.util.PropertiesReader;
import com.abcnull.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Properties;

/**
 * 测试基类
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/22
 */
@Slf4j
public class BaseTest {
    /**
     * 配置文件
     */
    private static Properties properties;

    /**
     * redis 连接池
     */
    private static JedisPool jedisPool;

    /**
     * redis 连接的工具类
     */
    public RedisUtil redisUtil;

    /**
     * jedis 连接
     */
    public Jedis jedis;

    /**
     * 驱动基类
     */
    private BaseDriver baseDriver;

    /**
     * 驱动
     */
    public WebDriver driver;

    /**
     * 执行一个测试套之前执行
     * 进行测试配置文件的读取工作
     *
     * @param propertiesPath 测试配置文件项目路径
     * @throws IOException IOException
     */
    @BeforeSuite(alwaysRun = true)
    @Parameters({"propertiesPath"})
    public void beforeSuite(@Optional("src/test/resources/config/config.properties") String propertiesPath) throws IOException {
        // 配置文件读取，存进静态变量 properties
        properties = PropertiesReader.readProperties(propertiesPath);
        // redis 连接池初始化操作
        jedisPool = RedisUtil.getJedisPool();
    }

    /**
     * 执行一个测试用例之前执行
     *
     * @param browserName    浏览器名
     * @param browserVersion 浏览器版本
     * @param remoteIP       hub 平台地址
     * @param remotePort     hub 平台端口号
     * @throws Exception 抛出浏览器没有启动成功异常
     */
    @BeforeTest(alwaysRun = true)
    @Parameters({"browserName", "terminal", "deviceName", "remoteIP", "remotePort", "browserVersion"})
    public void beforeTest(@Optional("chrome") String browserName, @Optional("pc") String terminal, @Optional("desktop") String deviceName, @Optional() String remoteIP, @Optional("4444") int remotePort, @Optional() String browserVersion) throws Exception {
        /* redis 新连接获取 */
        redisUtil = new RedisUtil();
        // 拿到一个新的 jedis 连接，设置 redisUtil 中的 jedis 以及键值超时时间
        redisUtil.setJedisAndExpire(redisUtil.getNewJedis());
        jedis = redisUtil.getJedis();
        /* 驱动配置 */
        baseDriver = new BaseDriver();
        // 如果不存在 hub 地址
        if (remoteIP == null || remoteIP.isEmpty()) {
            baseDriver.startBrowser(browserName, terminal, deviceName);
        }
        // 如果存在 hub 地址
        else {
            baseDriver.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
        }
        driver = baseDriver.getDriver();
    }

    /**
     * 执行一个测试用例中的类方法之前执行
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        // 登录操作
    }

    /**
     * 执行一个测试用例中的类方法之后执行
     */
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        // 注销操作
    }

    /**
     * 执行一个测试用例之后执行
     */
    @AfterTest(alwaysRun = true)
    public void afterTest() throws InterruptedException {
        // 驱动退出关闭浏览器
        baseDriver.closeBrowser();
        // redis 连接回收
        redisUtil.returnJedis();
    }

    /**
     * 执行一个测试套之后执行
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
    }
}