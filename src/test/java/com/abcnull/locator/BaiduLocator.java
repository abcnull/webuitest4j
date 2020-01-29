package com.abcnull.locator;

import org.openqa.selenium.By;

/**
 * 百度页面对象：元素定位
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
public class BaiduLocator {
    /**
     * 百度首页搜索框定位
     */
    public static final By SEARCH_INPUT = By.xpath("//input[@id='kw']");

    /**
     * 百度首页搜索按钮定位
     */
    public static final By SEARCH_BUTTON = By.xpath("//input[@id='su']");
}
