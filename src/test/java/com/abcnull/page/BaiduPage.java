package com.abcnull.page;

import com.abcnull.common.PageCommon;
import com.abcnull.data.BaiduData;
import com.abcnull.locator.BaiduLocator;
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
public class BaiduPage extends PageCommon {
    /**
     * 唯一构造器
     *
     * @param driver    驱动
     * @param redisUtil redis 存储工具类
     */
    public BaiduPage(WebDriver driver, RedisUtil redisUtil) {
        super(driver, redisUtil);
    }

    /**
     * 重写跳转页面操作
     */
    public void jumpPage() {
        log.info("跳转进入百度页面");
        super.jumpPage(BaiduData.URL);
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
         * redisUtil 工具类是对 jedis 的一层封装，其中已设置了所有键值对有效时间
         * redisUtil 来存取键值对，放在一个线程中被别的类使用到
         * redisUtil 存取键值对设置了键值过期时限，这个时限在 properties 文件中已配置
         * 若想自己设置时限可以直接使用 jedis 而不使用 redisUtil，如下所示：
         */
        redisUtil.setKey("a", "1");
        System.out.println("redis 中的值为：" + redisUtil.getKey("a"));

        /*
        jedis.set("a", "1");
        // 设置键值过期时间 600 s
        jedis.expire("a", 600);
        */

        // 返回是否进入指定页面
        return ifTitleContains(BaiduData.TEXT);
    }
}
