package com.abcnull.basetest;

import com.abcnull.exception.BrowserNameException;
import org.openqa.selenium.WebDriver;

/**
 * 驱动责任链尾结点
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:52
 */
public class TailHandler extends DriverHandler {
    /**
     * @param browserName 浏览器名
     * @param terminal    终端 pc/h5
     * @param deviceName  设备名
     * @return WebDriver
     * @throws Exception 匹配不到 browserName 异常
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName) {
        throw new BrowserNameException("不支持的浏览器类型" + "(" + browserName + ")");
    }

    /**
     * @param browserName    浏览器名
     * @param terminal       终端 pc/h5
     * @param deviceName     设备名
     * @param remoteIP       远端 ip
     * @param remotePort     端口
     * @param browserVersion 浏览器版本
     * @return WebDriver
     * @throws Exception 匹配不到 browserName 异常
     */
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) {
        throw new BrowserNameException("不支持的浏览器类型" + "(" + browserName + ")");
    }
}
