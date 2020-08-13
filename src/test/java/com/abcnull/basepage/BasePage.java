package com.abcnull.basepage;

import com.abcnull.util.RedisUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 封装各个模块页面都可以使用的操作方法
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
public class BasePage extends BaseBrowser {
    /**
     * 构造器 1
     *
     * @param driver 驱动
     */
    public BasePage(WebDriver driver) {
        super(driver);
    }

    /**
     * 构造器 2
     *
     * @param driver    驱动
     * @param redisUtil redis 连接工具类
     */
    public BasePage(WebDriver driver, RedisUtil redisUtil) {
        super(driver, redisUtil);
    }

    /*============================== 页面可共用的操作 ==============================*/

    /**
     * 很多 WEB 项目采用了一些框架，页面各个模块比较相似，因此这里设立了一个 BasePage 类型来存放公用的操作方法
     * BasePage 与 BaseBrowser 不同的是 BasePage 存放各模块页面可以公用的操作，BaseBrowser 只存放最基本的页面操作
     * <p>
     * function1
     */
    public void function1() {
        // todo : 某项公用操作 1（自己封装）
    }

    /**
     * function2
     *
     * @return true
     */
    public boolean function2() {
        // todo : 某项公用操作 2（自己封装）
        return true;
    }

    /**
     * function3
     *
     * @return true
     */
    public boolean function3() {
        // todo : 某项公用操作 3（自己封装）
        return true;
    }

    /*============================== 页面基本断言 ==============================*/

    /**
     * 判断当前页面标题是否是指定标题
     *
     * @param title 指定标题
     * @return 布尔值
     */
    public boolean ifTitleIs(String title) {
        return wait.until(ExpectedConditions.titleIs(title));
    }

    /**
     * 判断当前页面标题是否含有指定文本
     *
     * @param text 指定文本
     * @return 布尔值
     */
    public boolean ifTitleContains(String text) {
        return wait.until(ExpectedConditions.titleContains(text));
    }

    /**
     * 判断当前页面某个元素的文本值是否是指定文本
     *
     * @param locator 页面元素定位
     * @param text    指定文本
     * @return 布尔值
     */
    public boolean ifTextExists(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // todo : 页面中的公用操作可自行封装
}
