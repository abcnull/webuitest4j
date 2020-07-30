package com.abcnull.basepage;

import com.abcnull.util.PropertiesReader;
import com.abcnull.util.RedisUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;

/**
 * 封装浏览器中界面上最基本操作
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
public class BaseBrowser {
    /**
     * 驱动
     */
    protected WebDriver driver;

    /**
     * 动作
     */
    protected Actions actions;

    /**
     * 脚本
     */
    protected JavascriptExecutor je;

    /**
     * 显示等待
     */
    protected WebDriverWait wait;

    /**
     * redis 连接工具类
     */
    protected RedisUtil redisUtil;

    /**
     * 构造器 1
     *
     * @param driver 驱动
     */
    public BaseBrowser(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.je = ((JavascriptExecutor) driver);
        // 显示等待时长
        long timeout = Long.parseLong(PropertiesReader.getKey("driver.timeouts.webDriverWait"));
        this.wait = new WebDriverWait(driver, timeout);
    }

    /**
     * 构造器 2
     *
     * @param driver    驱动
     * @param redisUtil redis 连接工具类
     */
    public BaseBrowser(WebDriver driver, RedisUtil redisUtil) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.je = ((JavascriptExecutor) driver);
        // 显示等待时长
        long timeout = Long.parseLong(PropertiesReader.getKey("driver.timeouts.webDriverWait"));
        this.wait = new WebDriverWait(driver, timeout);
        this.redisUtil = redisUtil;
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
     * @param content 输入的内容，支持多内容，可以键盘输入
     * @return 输入框元素
     */
    public WebElement sendInput(By locator, CharSequence... content) {
        WebElement inputElement = locateElement(locator);
        inputElement.clear();
        inputElement.sendKeys(content);
        return inputElement;
    }

    /**
     * 移动到指定元素
     *
     * @param locator 元素定位
     */
    public void moveToElement(By locator) {
        actions.moveToElement(locateElement(locator)).perform();
    }

    /**
     * 拖拽指定元素
     *
     * @param fromLocator 从...元素
     * @param toLocator   至...元素
     */
    public void dragAndDropElement(By fromLocator, By toLocator) {
        wait.until(ExpectedConditions.elementToBeClickable(fromLocator));
        actions.dragAndDrop(locateElement(fromLocator), locateElement(toLocator)).perform();
    }

    /**
     * 跳转页面
     *
     * @param url 网址
     */
    public void enterPage(String url) {
        driver.get(url);
    }

    /*============================== 切换窗口句柄 ==============================*/

    /**
     * 查找下一个句柄，若只有一个窗口则句柄不变
     *
     * @return 驱动
     */
    public WebDriver switchNextHandle() {
        // 当前窗口句柄
        String currentHandle = driver.getWindowHandle();
        // 所有窗口句柄
        Set<String> allHandlesSet = driver.getWindowHandles();
        // 寻找下一个句柄
        for (String handle : allHandlesSet) {
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
    public WebDriver switchHandleByNum(int num) {
        // 所有窗口句柄
        Set<String> allHandlesSet = driver.getWindowHandles();
        Object[] allHandlesArr = allHandlesSet.toArray();
        // 切换句柄
        return driver.switchTo().window(allHandlesArr[num - 1].toString());
    }

    /**
     * 多窗口切换句柄，依据传入的窗口标题
     *
     * @param title contains(窗口 title)
     * @return 驱动
     * @throws Exception 找不到指定窗口句柄异常
     */
    public WebDriver switchHandleByTitle(String title) throws Exception {
        // 当前窗口句柄
        String currentHandle = driver.getWindowHandle();
        // 所有窗口句柄
        Set<String> allHandlesSet = driver.getWindowHandles();
        // 寻找第一个 title 符合的句柄
        for (String handle : allHandlesSet) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(title)) {
                return driver;
            }
        }
        driver.switchTo().window(currentHandle);
        throw new Exception(title + "窗口的句柄不存在");
    }

    /**
     * 多窗口切换句柄，依据传入的窗口 url
     *
     * @param url contains(窗口 url)
     * @return 驱动
     * @throws Exception 找不到指定窗口句柄异常
     */
    public WebDriver switchHandleByUrl(String url) throws Exception {
        // 当前窗口句柄
        String currentHandle = driver.getWindowHandle();
        // 所有窗口句柄
        Set<String> allHandlesSet = driver.getWindowHandles();
        // 寻找第一个 url 符合的句柄
        for (String handle : allHandlesSet) {
            driver.switchTo().window(handle);
            if (driver.getCurrentUrl().contains(url)) {
                return driver;
            }
        }
        driver.switchTo().window(currentHandle);
        throw new Exception(url + "窗口的句柄不存在");
    }

    /*============================== 切换 frame 结构 ==============================*/

    /**
     * 根据元素位置切换 frame 结构
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
     * 执行 JS 脚本
     *
     * @param script JS 脚本
     */
    public void executeScript(String script) {
        je.executeScript(script);
    }

    /**
     * 执行 JS 脚本
     *
     * @param script JS 脚本
     * @param args   对象元素数组
     */
    public void executeScript(String script, Object... args) {
        je.executeScript(script, args);
    }

    /**
     * 滑动到页面最顶上
     */
    public void scrollToTop() {
        executeScript("window.scrollTo(window.pageXOffset, 0)");
    }

    /**
     * 滑动到页面最低端
     */
    public void scrollToBottom() {
        executeScript("window.scrollTo(window.pageXOffset, document.body.scrollHeight)");
    }

    /**
     * 滑动使得元素和窗口顶端对齐
     *
     * @param by 需要和页面顶端对齐的元素
     */
    public void scrollElementTopToTop(By by) {
        executeScript("arguments[0].scrollIntoView(true);", driver.findElement(by));
    }

    /**
     * 滑动使得元素和窗口底部对齐
     *
     * @param by 需要和页面底端对齐的元素
     */
    public void scrollElementBottomToBottom(By by) {
        executeScript("arguments[0].scrollIntoView(false);", driver.findElement(by));
    }

    /**
     * 滑动到页面最右边
     */
    public void scrollToRight() {
        executeScript("window.scrollTo(document.body.scrollWidth, window.pageYOffset)");
    }

    /**
     * 滑动到页面最左边
     */
    public void scrollToLeft() {
        executeScript("0, window.pageYOffset)");
    }

    // todo : 页面中其他的最基本操作，可自行封装
}
