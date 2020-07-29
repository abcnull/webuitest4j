package com.abcnull.basetest;

import com.abcnull.util.PropertiesReader;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 欧朋驱动配置
 * todo : 手机浏览器 h5 暂缺
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:53
 */
public class OperaDriverHandler extends DriverHandler {
    /**
     * 启动本地 opera
     * todo : 手机浏览器 h5 暂缺
     *
     * @param browserName   浏览器名
     * @param terminal      终端 pc/h5
     * @param deviceName    设备名
     * @return WebDriver
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName) {
        /* 当不是 opera 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("opera")) {
            return next.startBrowser(browserName, terminal, deviceName);
        }

        /* 驱动配置进环境变量 */
        // 驱动根路径 /target/test-classes/driver
        String driverParentPath = this.getClass().getResource("/").getPath() + "driver" + File.separator;
        // opera 驱动路径
        String operaDriverPath = driverParentPath + PropertiesReader.getKey("driver.operaDriver");
        // 系统变量设置欧朋驱动
        System.setProperty("webdriver.opera.driver", operaDriverPath);

        /* 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");
        Map<String, Object> downloadMap = new HashMap<>();
        downloadMap.put("download.default_directory", downloadPath);

        /* 驱动可选项配置 */
        OperaOptions operaOptions = new OperaOptions();
        // 配置下载路径
        operaOptions.setExperimentalOption("prefs", downloadMap);
        // --no-sandbox
        operaOptions.addArguments("--no-sandbox");
        // --disable-dev-shm-usage
        operaOptions.addArguments("--disable-dev-shm-usage");

        /* todo : 如果要测手机浏览器 h5 */

        /* 启动 WebDriver */
        return new OperaDriver(operaOptions);
    }

    /**
     * 启动远端 opera
     * todo : 手机浏览器 h5 暂缺
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
        /* 当不是 opera 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("opera")) {
            return next.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
        }

        /* 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");
        Map<String, Object> downloadMap = new HashMap<>();
        downloadMap.put("download.default_directory", downloadPath);

        /* 驱动可选项配置 */
        // 配置远端浏览器版本
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities("opera", browserVersion, Platform.ANY);
        OperaOptions operaOptions = new OperaOptions().merge(desiredCapabilities);
        // 配置下载路径
        operaOptions.setExperimentalOption("prefs", downloadMap);
        // --no-sandbox
        operaOptions.addArguments("--no-sandbox");
        // --disable-dev-shm-usage
        operaOptions.addArguments("--disable-dev-shm-usage");

        /* todo : 如果要测手机浏览器 h5 */

        /* 启动 RemoteWebDriver */
        URL url = null;
        try {
            url = new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new RemoteWebDriver(url, operaOptions);
    }
}
