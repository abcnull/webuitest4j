package com.abcnull.pageobject.page;

import com.abcnull.basepage.BasePage;
import com.abcnull.pageobject.data.AbcnullData;
import com.abcnull.pageobject.locator.AbcnullLocator;
import com.abcnull.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

/**
 * abcnull 页面对象
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
@Slf4j
public class AbcnullPage extends BasePage {
    /**
     * 构造器 1
     *
     * @param driver 驱动
     */
    public AbcnullPage(WebDriver driver) {
        super(driver);
    }

    /**
     * 构造器 2
     *
     * @param driver    驱动
     * @param redisUtil redis 存储工具类
     */
    public AbcnullPage(WebDriver driver, RedisUtil redisUtil) {
        super(driver, redisUtil);
    }

    /**
     * 进入 abcnull 页面
     */
    public void enterPage() {
        log.info("跳转进入 abcnull 页面");
        super.enterPage(AbcnullData.URL);
    }

    /**
     * 搜索操作
     *
     * @return 搜索后是否进入指定页面
     */
    public boolean search() {
        log.info("搜索");
        // abcnull 页面搜索框搜索数据
        sendInput(AbcnullLocator.SEARCH_INPUT, AbcnullData.BLOGTITLE);
        // 点击搜索按钮
        clickButton(AbcnullLocator.SEARCH_BUTTON);
        /*
         * 搜索之后会产生一个新页面
         * 由于我的浏览器开启会多一个 data 页面，因此我这里有三个页面
         * 没有 data 页面的小伙伴建议采用 switchNextHandle 方法
         */
        switchHandleByNum(3);
        // 返回是否进入指定页面
        return ifTitleContains(AbcnullData.BLOGTITLE);
    }
}
