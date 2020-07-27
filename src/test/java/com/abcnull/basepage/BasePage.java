package com.abcnull.basepage;

import com.abcnull.util.RedisUtil;
import org.openqa.selenium.WebDriver;
import redis.clients.jedis.Jedis;

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
     * @param driver 驱动
     * @param jedis  Jedis 连接
     */
    public BasePage(WebDriver driver, Jedis jedis) {
        super(driver, jedis);
    }

    /**
     * 构造器 3
     *
     * @param driver    驱动
     * @param redisUtil redis 连接工具类
     */
    public BasePage(WebDriver driver, RedisUtil redisUtil) {
        super(driver, redisUtil);
    }

    /**
     * 很多 WEB 项目采用了一些框架，页面各个模块比较相似，因此这里设立了一个 PageCommon 类型来存放公用的操作方法
     * PageCommon 与 BrowserCommon 不同的是 PageCommon 存放各模块页面可以公用的操作，BrowserPage 只存放最基本的页面操作
     * <p>
     * function1
     */
    public void function1() {
        // 某项公用操作1
    }

    /**
     * function2
     *
     * @return true
     */
    public boolean function2() {
        // 某项公用操作2
        return true;
    }

    /**
     * function3
     *
     * @return true
     */
    public boolean function3() {
        // 某项公用操作3
        return true;
    }
}
