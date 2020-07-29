package com.abcnull.basetest;

import com.abcnull.util.PropertiesReader;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 谷歌驱动配置
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:52
 */
public class ChromeDriverHandler extends DriverHandler {
    /**
     * 启动本地 chrome
     *
     * @param browserName   浏览器名
     * @param terminal      终端 pc/h5
     * @param deviceName    设备名
     * @return WebDriver
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName) {
        /* 当不是 chrome 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("chrome")) {
            return next.startBrowser(browserName, terminal, deviceName);
        }

        /* 驱动配置进环境变量 */
        // 驱动根路径 /target/test-classes/driver
        String driverParentPath = this.getClass().getResource("/").getPath() + "driver" + File.separator;
        // chrome 驱动路径
        String chromeDriverPath = driverParentPath + PropertiesReader.getKey("driver.chromeDriver");
        // 系统变量设置谷歌驱动
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        /* 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");
        Map<String, Object> downloadMap = new HashMap<>();
        downloadMap.put("download.default_directory", downloadPath);

        /* 驱动可选项配置 */
        ChromeOptions chromeOptions = new ChromeOptions();
        // 配置下载路径
        chromeOptions.setExperimentalOption("prefs", downloadMap);
        // --no-sandbox
        chromeOptions.addArguments("--no-sandbox");
        // --disable-dev-shm-usage
        chromeOptions.addArguments("--disable-dev-shm-usage");

        /* 如果要测手机浏览器 h5 */
        if (terminal.toLowerCase().equals("h5")) {
            Map<String, String> mobileMap = new HashMap<>();
            mobileMap.put("deviceName", deviceName);
            // 配置 h5 的手机机型等
            chromeOptions.setExperimentalOption("mobileEmulation", mobileMap);
        }

        /* 启动 WebDriver */
        return new ChromeDriver(chromeOptions);
    }

    /**
     * 启动远端 chrome
     *
     * @param browserName       浏览器名
     * @param terminal          终端 pc/h5
     * @param deviceName        设备名
     * @param remoteIP          远端 ip
     * @param remotePort        端口
     * @param browserVersion    浏览器版本
     * @return WebDriver
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) {
        /* 当不是 chrome 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("chrome")) {
            return next.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
        }

        /* 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");
        Map<String, Object> downloadMap = new HashMap<>();
        downloadMap.put("download.default_directory", downloadPath);

        /* 驱动可选项配置 */
        // 配置远端浏览器版本
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities("chrome", browserVersion, Platform.ANY);
        ChromeOptions chromeOptions = new ChromeOptions().merge(desiredCapabilities);
        // 配置下载路径
        chromeOptions.setExperimentalOption("prefs", downloadMap);
        // --no-sandbox
        chromeOptions.addArguments("--no-sandbox");
        // --disable-dev-shm-usage
        chromeOptions.addArguments("--disable-dev-shm-usage");

        /* 如果要测手机浏览器 h5 */
        if (terminal.toLowerCase().equals("h5")) {
            Map<String, String> mobileMap = new HashMap<>();
            mobileMap.put("deviceName", deviceName);
            // 配置 h5 的手机机型等
            chromeOptions.setExperimentalOption("mobileEmulation", mobileMap);
        }

        /* 启动 RemoteWebDriver */
        URL url = null;
        try {
            url = new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new RemoteWebDriver(url, chromeOptions);
    }
}
