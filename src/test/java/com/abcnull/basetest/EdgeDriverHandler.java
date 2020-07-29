package com.abcnull.basetest;

import com.abcnull.util.PropertiesReader;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * edge 驱动配置
 * todo : 指定下载文件路径暂缺
 * todo : 手机浏览器 h5 暂缺
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:53
 */
public class EdgeDriverHandler extends DriverHandler {
    /**
     * 启动本地 edge
     * todo : 指定下载文件路径暂缺
     * todo : 手机浏览器 h5 暂缺
     *
     * @param browserName   浏览器名
     * @param terminal      终端 pc/h5
     * @param deviceName    设备名
     * @return WebDriver
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName) {
        /* 当不是 edge 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("edge")) {
            return next.startBrowser(browserName, terminal, deviceName);
        }

        /* 驱动配置进环境变量 */
        // 驱动根路径 /target/test-classes/driver
        String driverParentPath = this.getClass().getResource("/").getPath() + "driver" + File.separator;
        // edge 驱动路径
        String edgeDriverPath = driverParentPath + PropertiesReader.getKey("driver.edgeDriver");
        // 系统变量设置 edge 驱动
        System.setProperty("webdriver.edge.driver", edgeDriverPath);

        /* todo : 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");

        /* 驱动可选项配置 */
        EdgeOptions edgeOptions = new EdgeOptions();

        /* todo : 如果要测手机浏览器 h5 */

        /* 启动 WebDriver */
        return new EdgeDriver(edgeOptions);
    }

    /**
     * 启动远端 edge
     * todo : 指定下载文件路径暂缺
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
        /* 当不是 edge 进入责任链的下一环 */
        if (!browserName.toLowerCase().equals("edge")) {
            return next.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
        }

        /* todo : 下载地址设置 */
        String downloadPath = System.getProperty("user.dir") + File.separator + PropertiesReader.getKey("driver.downloadPath");

        /* 驱动可选项配置 */
        // 配置远端浏览器版本
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities("edge", browserVersion, Platform.ANY);
        EdgeOptions edgeOptions = new EdgeOptions().merge(desiredCapabilities);

        /* todo : 如果要测手机浏览器 h5 */

        /* 启动 RemoteWebDriver */
        URL url = null;
        try {
            url = new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new RemoteWebDriver(url, edgeOptions);
    }
}
