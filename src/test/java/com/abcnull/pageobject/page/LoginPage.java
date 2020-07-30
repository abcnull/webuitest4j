package com.abcnull.pageobject.page;

import com.abcnull.basepage.BasePage;
import com.abcnull.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

/**
 * 登录页面对象
 *
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/7/30 13:12
 */
@Slf4j
public class LoginPage extends BasePage {
    /**
     * 构造器 1
     *
     * @param driver 驱动
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * 构造器 2
     *
     * @param driver    驱动
     * @param redisUtil redis 工具
     */
    public LoginPage(WebDriver driver, RedisUtil redisUtil) {
        super(driver, redisUtil);
    }

    /**
     * 进入登录页面
     */
    public void enterPage() {
        log.info("跳转进入登录页面");
        // todo : super.enterPage(LoginData.URL);
    }

    /**
     * UI 点击的方式登录
     */
    public void loginByUI() {
        // todo : UI 方式登录
    }

    /**
     * API 访问登录接口实现登陆
     * 常用 HttpClient
     */
    public void loginByAPI() {
        // todo : API 方式登录
    }

    /**
     * 使用已知的 cookie/sessionid/access_token 实现登录
     * 使用 WebDriver 的 setCookies 或者直接操作 js 往浏览器中存储这些值
     */
    public void loginBySession() {
        // todo : 使用已知的 cookie/sessionid/access_token 登录
    }
}
