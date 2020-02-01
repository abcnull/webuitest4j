package com.abcnull.base;

import com.abcnull.constant.TestConstant;
import com.abcnull.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 终端选择 pc 或者 h5
     */
    private String terminal;

    /**
     * 设备选择
     */
    private String deviceName;

    /**
     * 下载路径（绝对地址）
     */
    private String downloadPath;

    /**
     * chrome 驱动路径
     */
    private String chromeDriverPath;

    /**
     * firefox 驱动路径
     */
    private String firefoxDriverPath;

    /**
     * opera 驱动路径
     */
    private String operaDriverPath;

    /**
     * ie 驱动路径
     */
    private String ieDriverPath;

    /**
     * edge 驱动路径
     */
    private String edgeDriverPath;

    /**
     * 隐式等待时长（s）
     */
    private long implicitlyWait;

    /**
     * 页面加载等待时长（s）
     */
    private long pageLoadTimeout;

    /**
     * 脚本等待时长（s）
     */
    private long setScriptTimeout;

    /**
     * hub ip 地址
     */
    private String remoteIP;

    /**
     * hub 端口号
     */
    private int remotePort;

    /**
     * 本地线程存储用来存驱动
     */
    public static ThreadLocal<WebDriver> threadLocal = new ThreadLocal<>();

    /**
     * 无参构造器
     * 初始化多种驱动路径参数
     * 初始化文件下载路径参数
     * 初始化多项等待时长参数
     *
     * @throws IOException 来源于文件下载路径的获取
     */
    public BaseDriver() throws IOException {
        /* 驱动的 test-classes 路径 */
        String driverParentPath = this.getClass().getResource("/").getPath() + "driver" + File.separator;
        /* 多种驱动文件路径配置参数 */
        chromeDriverPath = driverParentPath + PropertiesReader.getKey("driver.chromeDriver");
        firefoxDriverPath = driverParentPath + PropertiesReader.getKey("driver.firefoxDriver");
        operaDriverPath = driverParentPath + PropertiesReader.getKey("driver.operaDriver");
        ieDriverPath = driverParentPath + PropertiesReader.getKey("driver.ieDriver");
        edgeDriverPath = driverParentPath + PropertiesReader.getKey("driver.edgeDriver");
        /* 文件下载路径 */
        downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");
        /* 等待时长参数 */
        implicitlyWait = Long.valueOf(PropertiesReader.getKey("driver.timeouts.implicitlyWait"));
        pageLoadTimeout = Long.valueOf(PropertiesReader.getKey("driver.timeouts.pageLoadTimeout"));
        setScriptTimeout = Long.valueOf(PropertiesReader.getKey("driver.timeouts.setScriptTimeout"));
    }

    /**
     * 启动 hub 中的浏览器节点
     *
     * @param browserName    浏览器名
     * @param terminal       pc 或者 h5
     * @param deviceName     设备名称
     * @param remoteIP       hub ip 地址
     * @param remotePort     hub 端口号
     * @param browserVersion 浏览器版本
     * @throws Exception 抛出浏览器启动失败的异常
     */
    public void startBrowser(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) throws Exception {
        /* 驱动基本信息参数 */
        this.browserName = browserName.toLowerCase();
        /* 终端设备信息参数 */
        this.terminal = terminal.toLowerCase();
        this.deviceName = deviceName;
        /* hub 信息配置 */
        this.remoteIP = remoteIP;
        this.remotePort = remotePort;
        this.browserVersion = browserVersion;
        /* 选择驱动，启动浏览器 */
        switch (browserName) {
            // 1.谷歌
            case "chrome":
                try {
                    // 下载地址设置
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("download.default_directory", downloadPath);
                    // 驱动可选项配置
                    DesiredCapabilities desiredCapabilities = new DesiredCapabilities("chrome", browserVersion, Platform.ANY);
                    ChromeOptions chromeOptions = new ChromeOptions().merge(desiredCapabilities);
                    chromeOptions.setExperimentalOption("prefs", hashMap);
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    // 如果是 h5 需要另外设置
                    if (terminal.equals("h5")) {
                        Map<String, String> mobileEmulationMap = new HashMap<>();
                        mobileEmulationMap.put("deviceName", deviceName);
                        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulationMap);
                    }
                    // 启动 RemoteWebDriver
                    driver = new RemoteWebDriver(new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/"), chromeOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "hub 浏览器启动失败！");
                }
                break;
            // 2.火狐
            case "firefox":
                try {
                    /* 下载地址设置 */
                    FirefoxProfile firefoxProfile = new FirefoxProfile();
                    // 0 表示桌面，1 表示“我的下载”，2 表示自定义
                    firefoxProfile.setPreference("browser.download.folderList", "2");
                    firefoxProfile.setPreference("browser.download.dir", downloadPath);
                    /* 驱动可选项配置 */
                    DesiredCapabilities desiredCapabilities = new DesiredCapabilities("firefox", browserVersion, Platform.ANY);
                    FirefoxOptions firefoxOptions = new FirefoxOptions().merge(desiredCapabilities);
                    firefoxOptions.setProfile(firefoxProfile);
                    firefoxOptions.addArguments("--no-sandbox");
                    firefoxOptions.addArguments("--disable-dev-shm-usage");
                    /* 启动 RemoteWebDriver */
                    driver = new RemoteWebDriver(new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/"), firefoxOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "hub 浏览器启动失败！");
                }
                break;
            // 3.欧朋
            case "opera":
                try {
                    // 下载地址设置
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("download.default_directory", downloadPath);
                    // 驱动可选项配置
                    DesiredCapabilities desiredCapabilities = new DesiredCapabilities("opera", browserVersion, Platform.ANY);
                    OperaOptions operaOptions = new OperaOptions().merge(desiredCapabilities);
                    operaOptions.setExperimentalOption("prefs", hashMap);
                    operaOptions.addArguments("--no-sandbox");
                    operaOptions.addArguments("--disable-dev-shm-usage");
                    // 启动 RemoteWebDriver
                    driver = new RemoteWebDriver(new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/"), operaOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "hub 浏览器启动失败！");
                }
                break;
            // 4.Edge
            case "edge":
                try {
                    // 驱动可选项配置
                    DesiredCapabilities desiredCapabilities = new DesiredCapabilities("edge", browserVersion, Platform.ANY);
                    EdgeOptions edgeOptions = new EdgeOptions().merge(desiredCapabilities);
                    // 启动 RemoteWebDriver
                    driver = new RemoteWebDriver(new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/"), edgeOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "hub 浏览器启动失败！");
                }
                break;
            // 5.IE
            case "ie":
                try {
                    // IE 浏览器安全设置
                    DesiredCapabilities desiredCapabilities = DesiredCapabilities.internetExplorer();
                    desiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                    InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions().merge(desiredCapabilities);
                    // 启动 RemoteWebDriver
                    driver = new RemoteWebDriver(new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/"), internetExplorerOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "hub 浏览器启动失败！");
                }
                break;
            default:
                throw new Exception("暂不支持的浏览器类型");
        }
        /* 驱动设置等待时长 */
        // 隐式等待
        driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
        // 页面加载等待
        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        // 脚本等待
        driver.manage().timeouts().setScriptTimeout(setScriptTimeout, TimeUnit.SECONDS);
        /* 窗口最大化 */
        driver.manage().window().maximize();
        /* 驱动存进线程本地存储 */
        threadLocal.set(driver);
        log.info("浏览器：" + browserName + " " + browserVersion + " 在 " + remoteIP + ":" + remotePort + " 中成功启动！");
    }

    /**
     * 启动本机中的浏览器
     *
     * @param browserName 浏览器名
     * @param terminal    pc 或者 h5
     * @param deviceName  设备名称
     * @throws Exception 抛出浏览器启动失败的异常
     */
    public void startBrowser(String browserName, String terminal, String deviceName) throws Exception {
        /* 驱动基本信息参数 */
        this.browserName = browserName.toLowerCase();
        /* 终端设备信息参数 */
        this.terminal = terminal.toLowerCase();
        this.deviceName = deviceName;
        /* 选择驱动，启动浏览器 */
        switch (browserName) {
            // 1.谷歌
            case "chrome":
                try {
                    // 系统变量设置谷歌驱动
                    System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                    // 下载地址设置
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("download.default_directory", downloadPath);
                    // 驱动可选项配置
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setExperimentalOption("prefs", hashMap);
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    // 如果是 h5 需要另外设置
                    if (terminal.equals("h5")) {
                        Map<String, String> mobileEmulationMap = new HashMap<>();
                        mobileEmulationMap.put("deviceName", deviceName);
                        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulationMap);
                    }
                    // 启动 RemoteWebDriver
                    driver = new ChromeDriver(chromeOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "浏览器启动失败！");
                }
                break;
            // 2.火狐
            case "firefox":
                try {
                    /* 系统变量设置火狐驱动 */
                    System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
                    /* 下载地址设置 */
                    FirefoxProfile firefoxProfile = new FirefoxProfile();
                    // 0 表示桌面，1 表示“我的下载”，2 表示自定义
                    firefoxProfile.setPreference("browser.download.folderList", "2");
                    firefoxProfile.setPreference("browser.download.dir", downloadPath);
                    /* 驱动可选项配置 */
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.setProfile(firefoxProfile);
                    firefoxOptions.addArguments("--no-sandbox");
                    firefoxOptions.addArguments("--disable-dev-shm-usage");
                    /* 启动 RemoteWebDriver */
                    driver = new FirefoxDriver(firefoxOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "浏览器启动失败！");
                }
                break;
            // 3.欧朋
            case "opera":
                try {
                    // 系统变量设置欧朋驱动
                    System.setProperty("webdriver.opera.driver", operaDriverPath);
                    // 下载地址设置
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("download.default_directory", downloadPath);
                    // 驱动可选项配置
                    OperaOptions operaOptions = new OperaOptions();
                    operaOptions.setExperimentalOption("prefs", hashMap);
                    operaOptions.addArguments("--no-sandbox");
                    operaOptions.addArguments("--disable-dev-shm-usage");
                    // 启动 RemoteWebDriver
                    driver = new OperaDriver(operaOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "浏览器启动失败！");
                }
                break;
            // 4.Edge
            case "edge":
                try {
                    // 系统变量设置 Edge 驱动
                    System.setProperty("webdriver.edge.driver", edgeDriverPath);
                    // 驱动可选项配置
                    EdgeOptions edgeOptions = new EdgeOptions();
                    // 启动 RemoteWebDriver
                    driver = new EdgeDriver(edgeOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "浏览器启动失败！");
                }
                break;
            // 5.IE
            case "ie":
                try {
                    // 系统变量设置 IE 驱动
                    System.setProperty("webdriver.ie.driver", ieDriverPath);
                    // IE 浏览器安全设置
                    DesiredCapabilities desiredCapabilities = DesiredCapabilities.internetExplorer();
                    desiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                    InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions().merge(desiredCapabilities);
                    // 启动 RemoteWebDriver
                    driver = new InternetExplorerDriver(internetExplorerOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(browserName + "浏览器启动失败！");
                }
                break;
            // 都没匹配到则抛出异常
            default:
                throw new Exception("暂不支持的浏览器类型");
        }
        /* 驱动设置等待时长 */
        // 隐式等待
        driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
        // 页面加载等待
        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        // 脚本等待
        driver.manage().timeouts().setScriptTimeout(setScriptTimeout, TimeUnit.SECONDS);
        /* 窗口最大化 */
        driver.manage().window().maximize();
        // 存进线程本地存储
        threadLocal.set(driver);
        log.info((terminal.equals("h5")) ? ("浏览器：" + browserName + " h5 成功启动！") : ("浏览器：" + browserName + " 成功启动！"));
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
        // 存进线程本地存储
        threadLocal.set(driver);
    }

    /**
     * 驱动结束并关闭浏览器
     */
    public void closeBrowser() throws InterruptedException {
        // JS 显示弹出框表示测试结束
        ((JavascriptExecutor) driver).executeScript("alert('测试完成，浏览器在3s后关闭！')");
        Thread.sleep(TestConstant.THREE_THOUSANG);
        if (driver != null) {
            driver.quit();
        }
        log.info(browserName + "浏览器已成功关闭！");
    }
}
