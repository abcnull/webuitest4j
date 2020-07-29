package com.abcnull.basetest;

import com.abcnull.util.PropertiesReader;
import com.abcnull.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.IOException;

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
     * redis 连接的工具类
     */
    public RedisUtil redisUtil;

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
     * 由于 BeforeSuite 不会多线程去执行，因此对于配置文件读取未使用线程安全的操作
     *
     * @param propertiesPath 整个项目的测试配置文件相对于项目的路径
     * @throws IOException IOException
     */
    @BeforeSuite(alwaysRun = true)
    @Parameters({"propertiesPath"})
    public void beforeSuite(@Optional("src/test/resources/config/config.properties") String propertiesPath) throws IOException {
        // 配置文件读取
        PropertiesReader.readProperties(propertiesPath);
        // redis 连接池初始化操作
        RedisUtil.initJedisPool();
        // todo : 这里可以自己定制其他工具初始化操作
    }

    /**
     * 执行一个测试用例之前执行
     * 这里做多线程的处理
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
        redisUtil.initJedis();
        /* 驱动配置 */
        baseDriver = new BaseDriver();
        driver = baseDriver.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
        // todo : 由于线程隔离设为 test，这里可以通过 new 一个对象来达到线程隔离的效果，可以做其他的扩展定制
    }

    /**
     * 执行一个测试用例中的类方法之前执行
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        // todo : 登录操作可以放在这里
    }

    /**
     * 执行一个测试用例中的类方法之后执行
     */
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        // todo : 登录的注销或其他操作可以放在这里
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
        // todo : 可自己定制
    }
}