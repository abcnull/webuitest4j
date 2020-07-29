package com.abcnull.basetest;

import org.openqa.selenium.WebDriver;

/**
 * 驱动责任链尾结点
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/29 18:52
 */
public class TailHandler extends DriverHandler {
    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName) {
        return null;
    }

    @Override
    public WebDriver startBrowser(String browserName, String terminal, String deviceName, String remoteIP, int remotePort, String browserVersion) {
        return null;
    }
}
