package com.abcnull.testcase;

import com.abcnull.base.BaseTest;
import com.abcnull.page.AbcnullPage;
import org.testng.annotations.Test;

/**
 * abcnull 页面测试用例
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/28
 */
public class AbcnullTest extends BaseTest {
    /**
     * abcnull 页面对象
     */
    private AbcnullPage abcnullPage;

    @Test(description = "abcnull页面_搜索测试", priority = 1)
    public void testSearch() {
        // 初始化 abcnull 页面
        abcnullPage = new AbcnullPage(driver, redisUtil);
        // 进入 abcnull 首页
        abcnullPage.jumpPage();
        // abncull 页面搜索检测
        assert abcnullPage.search();
    }
}
