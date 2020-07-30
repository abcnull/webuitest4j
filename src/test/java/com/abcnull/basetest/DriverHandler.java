package com.abcnull.basetest;

import org.openqa.selenium.WebDriver;

/**
 * 驱动责任链模式 handler 抽象类
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:38
 */
public abstract class DriverHandler {


    /**
     * 后继 DriverHandler 结点
     */
    public DriverHandler next;

    /**
     * 判断启动本地或远端
     *
     * @param browserName    浏览器名
     * @param terminal       终端 pc/h5
     * @param deviceName     设备名
     * @param remoteIP       远端 ip
     * @param remotePort     端口
     * @param browserVersion 浏览器版本
     * @return WebDriver
     * @throws Exception 匹配不到 browserName 异常
     */
    public WebDriver start(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) throws Exception {
        // 通过 remoteIP 是不是空来判定在本地还是远端运行
        if (remoteIP == null || remoteIP.isEmpty()) {
            // terminal 为 pc 可以允许 deviceName 为空
            return startBrowser(browserName, terminal, deviceName);
        } else {
            return startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
        }
    }

    /**
     * 运行本地
     *
     * @param browserName 浏览器名
     * @param terminal    终端 pc/h5
     * @param deviceName  设备名
     * @return WebDriver
     * @throws Exception 匹配不到 browserName 异常
     */
    public abstract WebDriver startBrowser(String browserName, String terminal, String deviceName) throws Exception;

    /**
     * 运行远端
     *
     * @param browserName    浏览器名
     * @param terminal       终端 pc/h5
     * @param deviceName     设备名
     * @param remoteIP       远端 ip
     * @param remotePort     端口
     * @param browserVersion 浏览器版本
     * @return WebDriver
     * @throws Exception 匹配不到 browserName 异常
     */
    public abstract WebDriver startBrowser(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) throws Exception;

    /**
     * 后继结点赋值
     *
     * @param next 后继结点
     */
    public DriverHandler setNext(DriverHandler next) {
        this.next = next;
        return this.next;
    }
}
