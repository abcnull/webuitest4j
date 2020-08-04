package com.abcnull.basetest;

import org.openqa.selenium.WebDriver;

/**
 * 驱动责任链头结点
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:52
 */
public class HeadHandler extends DriverHandler {
    /**
     * 启动本地浏览器
     *
     * @param browserName 浏览器名
     * @param terminal    终端 pc/h5
     * @param deviceName  设备名
     * @return WebDriver
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName) {
        return next.startBrowser(browserName, terminal, deviceName);
    }

    /**
     * 启动远端浏览器
     *
     * @param browserName    浏览器名
     * @param terminal       终端 pc/h5
     * @param deviceName     设备名
     * @param remoteIP       远端 ip
     * @param remotePort     端口
     * @param browserVersion 浏览器版本
     * @return WebDriver
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) {
        return next.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
    }
}
