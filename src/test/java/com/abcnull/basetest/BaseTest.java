package com.abcnull.basetest;

import com.abcnull.pageobject.page.LoginPage;
import com.abcnull.util.PropertiesReader;
import com.abcnull.util.RedisUtil;
import com.abcnull.util.WordartDisplayer;
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
     * 对外暴露
     */
    public RedisUtil redisUtil;

    /**
     * 驱动基类
     */
    private BaseDriver baseDriver;

    /**
     * 驱动
     * 对外暴露
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
        // 显示文字 webuitest4j
        WordartDisplayer.display();
        // 配置文件读取
        PropertiesReader.readProperties(propertiesPath);
        // redis 连接池初始化操作
        RedisUtil.initJedisPool();
        // todo : 这里可以自己定制其他工具初始化操作（看需要）
    }

    /**
     * 执行一个测试用例之前执行
     * 这里做多线程的处理
     *
     * @param browserName    浏览器名（必传）
     * @param terminal       终端 pc/h5（默认是 pc，对于 h5 需要传 h5）
     * @param deviceName     设备名（默认是 desktop，对于 h5 需要传手机型号）
     * @param remoteIP       远端 ip（远端运行必传）
     * @param remotePort     端口（默认是 4444）
     * @param browserVersion 浏览器版本
     * @throws Exception 匹配不到 browserName 异常
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
        // todo : 由于线程隔离设为 test，这里可以通过 new 一个对象来达到线程隔离的效果，可以做其他的扩展定制（看需要）
        /* todo : 登录操作可以放在这里（看需要）
         * LoginPage loginPage = new LoginPage(driver);
         * loginPage.loginByUI();
         */
    }

    /**
     * 执行一个测试用例中的类方法之前执行
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        // todo : 登录操作可以放在这里（看需要）
        LoginPage loginPage = new LoginPage(driver, redisUtil);
        loginPage.enterPage();
        loginPage.loginByAPI();
    }

    /**
     * 执行一个测试用例中的类方法之后执行
     */
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        // todo : 登录的注销或其他操作可以放在这里（看需要）
    }

    /**
     * 执行一个测试用例之后执行
     *
     * @throws InterruptedException sleep 休眠异常
     */
    @AfterTest(alwaysRun = true)
    public void afterTest() throws InterruptedException {
        // 驱动退出关闭浏览器
        baseDriver.closeBrowser();
        // redis 连接回收
        redisUtil.returnJedis();
        // todo : 其他工具的释放操作（看需要）
    }

    /**
     * 执行一个测试套之后执行
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        // todo : 可自己定制（看需要）
    }
}