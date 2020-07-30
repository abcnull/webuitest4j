package com.abcnull.pageobject.page;

import com.abcnull.basepage.BasePage;
import com.abcnull.pageobject.data.BaiduData;
import com.abcnull.pageobject.locator.BaiduLocator;
import com.abcnull.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

/**
 * 百度页面对象
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
@Slf4j
public class BaiduPage extends BasePage {
    /**
     * 构造器 1
     *
     * @param driver 驱动
     */
    public BaiduPage(WebDriver driver) {
        super(driver);
    }

    /**
     * 构造器 2
     *
     * @param driver    驱动
     * @param redisUtil redis 存储工具类
     */
    public BaiduPage(WebDriver driver, RedisUtil redisUtil) {
        super(driver, redisUtil);
    }

    /**
     * 进入百度页面
     */
    public void enterPage() {
        log.info("跳转进入百度页面");
        super.enterPage(BaiduData.URL);
    }

    /**
     * 搜索操作
     *
     * @return 搜索后是否进入指定页面
     */
    public boolean search() {
        log.info("搜索");
        // 百度搜索框搜索数据
        sendInput(BaiduLocator.SEARCH_INPUT, BaiduData.TEXT);
        // 点击搜索按钮
        clickButton(BaiduLocator.SEARCH_BUTTON);

        /*
         * redisUtil 工具类是对 jedisPool 和 jedis 的一层封装，配置文件中已设置了所有键值对有效时间
         * redisUtil 存取键值对设置了键值过期时限，这个时限在 properties 文件中已配置，若配置文件取不到表示无限时间
         * 可以传 2 个或 3 各参数，具体可自行查看代码
         *
         * redisUtil.setKey("a", "1");
         * System.out.println("redis 中的值为：" + redisUtil.getKey("a"));
         */

        // 返回是否进入指定页面
        return ifTitleContains(BaiduData.TEXT);
    }
}
