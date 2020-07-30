package com.abcnull.basetest;

import com.abcnull.constant.TestConstant;
import com.abcnull.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * 驱动基类
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/22
 */
@Slf4j
public class BaseDriver {
    /**
     * 浏览器驱动
     */
    private WebDriver driver;

    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 终端选择 pc 或者 h5
     */
    private String terminal;

    /**
     * 设备选择
     */
    private String deviceName;

    /**
     * hub ip 地址
     */
    private String remoteIP;

    /**
     * hub 端口号
     */
    private int remotePort;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 打开相应的浏览器
     *
     * @param browserName    浏览器名
     * @param terminal       终端 pc/h5
     * @param deviceName     设备名
     * @param remoteIP       远端 ip
     * @param remotePort     端口
     * @param browserVersion 浏览器版本
     * @return WebDriver
     */
    public WebDriver startBrowser(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) throws Exception {
        /* 驱动基本信息参数 */
        this.browserName = browserName.toLowerCase();
        /* 终端设备信息参数 */
        this.terminal = terminal.toLowerCase();
        this.deviceName = deviceName;
        /* hub 信息配置 */
        this.remoteIP = remoteIP;
        this.remotePort = remotePort;
        this.browserVersion = browserVersion;

        /* 初始化驱动的责任链中各个对象 */
        DriverHandler headHandler = new HeadHandler();
        DriverHandler chromeDriverHandler = new ChromeDriverHandler();
        DriverHandler firefoxDriverHandler = new FirefoxDriverHandler();
        DriverHandler operaDriverHandler = new OperaDriverHandler();
        DriverHandler edgeDriverHandler = new EdgeDriverHandler();
        DriverHandler internetExplorerDriverHandler = new InternetExplorerDriverHandler();
        DriverHandler tailHandler = new TailHandler();

        /* 构建一条驱动初始化的完整责任链 */
        headHandler.setNext(chromeDriverHandler).setNext(firefoxDriverHandler).setNext(operaDriverHandler)
                .setNext(edgeDriverHandler).setNext(internetExplorerDriverHandler).setNext(tailHandler);

        /* 通过责任链启动浏览器 */
        this.driver = headHandler.start(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);

        /* 驱动设置等待时长 */
        long implicitlyWait = Long.parseLong(PropertiesReader.getKey("driver.timeouts.implicitlyWait"));
        long pageLoadTimeout = Long.parseLong(PropertiesReader.getKey("driver.timeouts.pageLoadTimeout"));
        long setScriptTimeout = Long.parseLong(PropertiesReader.getKey("driver.timeouts.setScriptTimeout"));
        // 隐式等待
        driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
        // 页面加载等待
        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        // JS 等待
        driver.manage().timeouts().setScriptTimeout(setScriptTimeout, TimeUnit.SECONDS);
        /* 窗口最大化 */
        driver.manage().window().maximize();
        log.info((terminal.toLowerCase().equals("h5")) ? ("浏览器：" + browserName + " h5 成功启动！") : ("浏览器：" + browserName + " 成功启动！"));

        return this.driver;
    }

    /**
     * 取到浏览器驱动
     *
     * @return 浏览器驱动
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * 设置浏览器驱动
     *
     * @param driver 浏览器驱动
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * 关闭浏览器
     *
     * @throws InterruptedException sleep 休眠异常
     */
    public void closeBrowser() throws InterruptedException {
        // JS 显示弹出框表示测试结束
        ((JavascriptExecutor) driver).executeScript("alert('测试完成，浏览器在3s后关闭！')");
        sleep(TestConstant.THREE_THOUSANG);
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        log.info(browserName + "浏览器已成功关闭！");
    }

    /**
     * getBrowserName
     *
     * @return browserName
     */
    public String getBrowserName() {
        return browserName;
    }

    /**
     * getTerminal
     *
     * @return terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * getDeviceName
     *
     * @return deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * getRemoteIP
     *
     * @return remoteIP
     */
    public String getRemoteIP() {
        return remoteIP;
    }

    /**
     * getRemotePort
     *
     * @return remotePort
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * getBrowserVersion
     *
     * @return browserVersion
     */
    public String getBrowserVersion() {
        return browserVersion;
    }
}
