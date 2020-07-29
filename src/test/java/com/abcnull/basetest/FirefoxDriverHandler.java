package com.abcnull.basetest;

import com.abcnull.util.PropertiesReader;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 火狐驱动配置
 * todo : 手机浏览器 h5 暂缺
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:53
 */
public class FirefoxDriverHandler extends DriverHandler {
    /**
     * 启动本地 firefox
     * todo : 手机浏览器 h5 暂缺
     *
     * @param browserName   浏览器名
     * @param terminal      终端 pc/h5
     * @param deviceName    设备名
     * @return WebDriver
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName) {
        /* 当不是 firefox 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("firefox")) {
            return next.startBrowser(browserName, terminal, deviceName);
        }

        /* 驱动配置进环境变量 */
        // 驱动根路径 /target/test-classes/driver
        String driverParentPath = this.getClass().getResource("/").getPath() + "driver" + File.separator;
        // firefox 驱动路径
        String firefoxDriverPath = driverParentPath + PropertiesReader.getKey("driver.firefoxDriver");
        // 系统变量设置火狐驱动
        System.setProperty("webdriver.gecko.driver", firefoxDriverPath);

        /* 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        // 0 表示桌面，1 表示“我的下载”，2 表示自定义
        firefoxProfile.setPreference("browser.download.folderList", "2");
        firefoxProfile.setPreference("browser.download.dir", downloadPath);

        /* 驱动可选项配置 */
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        // 配置下载路径
        firefoxOptions.setProfile(firefoxProfile);
        // --no-sandbox
        firefoxOptions.addArguments("--no-sandbox");
        // --disable-dev-shm-usage
        firefoxOptions.addArguments("--disable-dev-shm-usage");

        /* todo : 如果要测手机浏览器 h5 */

        /* 启动 WebDriver */
        return new FirefoxDriver(firefoxOptions);
    }

    /**
     * 启动远端 firefox
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
        /* 当不是 firefox 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("firefox")) {
            return next.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
        }

        /* 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        // 0 表示桌面，1 表示“我的下载”，2 表示自定义
        firefoxProfile.setPreference("browser.download.folderList", "2");
        firefoxProfile.setPreference("browser.download.dir", downloadPath);

        /* 驱动可选项配置 */
        // 配置远端浏览器版本
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities("firefox", browserVersion, Platform.ANY);
        FirefoxOptions firefoxOptions = new FirefoxOptions().merge(desiredCapabilities);
        // 配置下载路径
        firefoxOptions.setProfile(firefoxProfile);
        // --no-sandbox
        firefoxOptions.addArguments("--no-sandbox");
        // --disable-dev-shm-usage
        firefoxOptions.addArguments("--disable-dev-shm-usage");

        /* todo : 如果要测手机浏览器 h5 */

        /* 启动 RemoteWebDriver */
        URL url = null;
        try {
            url = new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new RemoteWebDriver(url, firefoxOptions);
    }
}
