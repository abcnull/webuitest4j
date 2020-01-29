package com.abcnull.testcase;

import com.abcnull.base.BaseTest;
import com.abcnull.page.BaiduPage;
import org.testng.annotations.Test;

/**
 * 百度页面测试用例
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
public class BaiduTest extends BaseTest {
    /**
     * 百度页面对象
     */
    private BaiduPage baiduPage;

    @Test(description = "百度首页_搜索测试", priority = 1)
    public void testSearch() {
        // 初始化百度页面
        baiduPage = new BaiduPage(driver, redisUtil);
        // 进入百度首页
        baiduPage.jumpPage();
        // 百度页面搜索检测
        assert baiduPage.search();
    }
}
