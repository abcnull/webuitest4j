package com.abcnull.locator;

import org.openqa.selenium.By;

/**
 * abcnull 页面对象：元素定位
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
public class AbcnullLocator {
    /**
     * abcnull 页面搜索框定位
     */
    public static final By SEARCH_INPUT = By.xpath("//input[@id='toolber-keyword']");

    /**
     * abcnull 页面搜索按钮定位
     */
    public static final By SEARCH_BUTTON = By.xpath("//div[@class='search_bar onlySearch searchUser']/a/img");
}
