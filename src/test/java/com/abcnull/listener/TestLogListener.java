package com.abcnull.listener;

import com.abcnull.util.ScreenshotUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * 测试日志监听器
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2019/9/9
 */
@Slf4j
public class TestLogListener extends TestListenerAdapter {
    /**
     * 开始
     *
     * @param iTestContext ITestContext
     */
    @Override
    public void onStart(ITestContext iTestContext) {
        super.onStart(iTestContext);
        log.info(String.format("====================%s开始====================", iTestContext.getName()));
    }

    /**
     * 测试开始
     *
     * @param iTestResult ITestResult
     */
    @Override
    public void onTestStart(ITestResult iTestResult) {
        super.onTestStart(iTestResult);
        log.info(String.format("========%s.%s测试开始========", iTestResult.getInstanceName(), iTestResult.getName()));
    }

    /**
     * 测试成功
     *
     * @param iTestResult ITestResult
     */
    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        super.onTestSuccess(iTestResult);
        log.info(String.format("========%s.%s测试通过========", iTestResult.getInstanceName(), iTestResult.getName()));
    }

    /**
     * 测试失败
     *
     * @param iTestResult ITestResult
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {
        super.onTestFailure(iTestResult);
        log.error(String.format("========%s.%s测试失败,失败原因如下：\n%s========", iTestResult.getInstanceName(), iTestResult.getName(), iTestResult.getThrowable()));
        // 失败时候抛出异常进行截图
        ScreenshotUtil.capture(iTestResult);
    }

    /**
     * 测试跳过
     *
     * @param iTestResult ITestResult
     */
    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        super.onTestSkipped(iTestResult);
        log.info(String.format("========%s.%s跳过测试========", iTestResult.getInstanceName(), iTestResult.getName()));
    }

    /**
     * 结束
     *
     * @param iTestContext ITestContext
     */
    @Override
    public void onFinish(ITestContext iTestContext) {
        super.onFinish(iTestContext);
        log.info(String.format("====================%s结束====================", iTestContext.getName()));
    }
}