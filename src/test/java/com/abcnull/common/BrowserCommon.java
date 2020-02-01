package com.abcnull.common;

import com.abcnull.util.PropertiesReader;
import com.abcnull.util.RedisUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 封装浏览器中界面上最基本操作
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
public class BrowserCommon {
    /**
     * 驱动
     */
    protected WebDriver driver;

    /**
     * 显示等待
     */
    protected WebDriverWait wait;

    /**
     * Jedis 连接
     */
    protected Jedis jedis;

    /**
     * redis 连接工具类
     */
    protected RedisUtil redisUtil;

    /**
     * 构造器 1
     *
     * @param driver 驱动
     */
    public BrowserCommon(WebDriver driver) {
        this.driver = driver;
        // 显示等待时长
        long timeout = Long.valueOf(PropertiesReader.getKey("driver.timeouts.webDriverWait"));
        wait = new WebDriverWait(driver, timeout);
    }

    /**
     * 构造器 2
     *
     * @param driver 驱动
     * @param jedis  Jedis 连接
     */
    public BrowserCommon(WebDriver driver, Jedis jedis) {
        this.driver = driver;
        // 显示等待时长
        long timeout = Long.valueOf(PropertiesReader.getKey("driver.timeouts.webDriverWait"));
        wait = new WebDriverWait(driver, timeout);
        this.jedis = jedis;
    }

    /**
     * 构造器 3
     *
     * @param driver    驱动
     * @param redisUtil redis 连接工具类
     */
    public BrowserCommon(WebDriver driver, RedisUtil redisUtil) {
        this.driver = driver;
        // 显示等待时长
        long timeout = Long.valueOf(PropertiesReader.getKey("driver.timeouts.webDriverWait"));
        wait = new WebDriverWait(driver, timeout);
        this.redisUtil = redisUtil;
        this.jedis = redisUtil.getJedis();
    }

    /*============================== 基本元素操作 ==============================*/

    /**
     * 通过元素定位拿到 WebElement 元素对象
     *
     * @param locator By 类型元素定位
     * @return 定位到的元素
     */
    public WebElement locateElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * 点击元素
     *
     * @param locator By 类型元素定位
     * @return 点击的元素
     */
    public WebElement clickButton(By locator) {
        WebElement buttonElement = locateElement(locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        buttonElement.click();
        return buttonElement;
    }

    /**
     * 输入框输入数据
     *
     * @param locator By 类型元素定位
     * @param content 输入的内容
     * @return 输入框元素
     */
    public WebElement sendInput(By locator, String content) {
        WebElement inputElement = locateElement(locator);
        inputElement.clear();
        inputElement.sendKeys(content);
        return inputElement;
    }

    /**
     * 跳转页面
     *
     * @param url 网址
     */
    public void jumpPage(String url) {
        driver.get(url);
    }

    /*============================== 切换句柄 ==============================*/

    /**
     * 查找下一个句柄，建议两个窗口使用
     *
     * @return 驱动
     */
    public WebDriver switchNextHandle() {
        // 当前窗口句柄
        String currentHandle = driver.getWindowHandle();
        // 所有窗口句柄
        Set<String> allHandles = driver.getWindowHandles();
        // 寻找下一个句柄
        for (String handle : allHandles) {
            if (!handle.equals(currentHandle)) {
                return driver.switchTo().window(handle);
            }
        }
        return driver;
    }

    /**
     * 多窗口切换句柄，依据传入的句柄号码
     *
     * @param num 号码从 1 开始
     * @return 驱动
     */
    public WebDriver switchHandle(int num) {
        // 当前窗口句柄
        String currentHandle = driver.getWindowHandle();
        // 所有窗口句柄
        Set<String> allHandlesSet = driver.getWindowHandles();
        List<String> allHandlesList = new ArrayList<>(allHandlesSet);
        // 切换句柄
        return driver.switchTo().window(allHandlesList.get(num - 1));
    }

    /**
     * 切换 frame 结构
     *
     * @param locator frame 定位
     * @return 驱动
     */
    public WebDriver switchFrame(By locator) {
        return driver.switchTo().frame(locateElement(locator));
    }

    /**
     * 切换父 frame 结构
     *
     * @return 驱动
     */
    public WebDriver switchParentFrame() {
        return driver.switchTo().parentFrame();
    }

    /**
     * 跳出 frame 结构
     *
     * @return 驱动
     */
    public WebDriver switchOutOfFrame() {
        return driver.switchTo().defaultContent();
    }

    /*============================== JS 操作 ==============================*/

    /**
     * 页面滑动到最顶上
     */
    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
    }

    /**
     * 页面滑动到最低端
     */
    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    /**
     * 页面滑动使得元素顶端和页面顶端对齐
     *
     * @param by 需要和页面顶端对齐的元素
     */
    public void scrollElementTopToTop(By by) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(by));
    }

    /**
     * 页面滑动使得元素底端和页面底端对齐
     *
     * @param by 需要和页面底端对齐的元素
     */
    public void scrollElementBottomToBottom(By by) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", driver.findElement(by));
    }

    /*============================== 页面断言 ==============================*/

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
}
