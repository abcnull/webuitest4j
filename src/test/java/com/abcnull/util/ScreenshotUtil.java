package com.abcnull.util;

import com.abcnull.basetest.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 截图工具
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/22
 */
@Slf4j
public class ScreenshotUtil {
    /**
     * 截图存储路径
     */
    private static String SCREENSHOT_PATH = System.getProperty("user.dir") + File.separator + "target" + File.separator + "test-output" + File.separator + "screenshot";

    /**
     * 截图
     *
     * @param iTestResult i测试结果
     */
    public static void capture(ITestResult iTestResult) {
        log.info("开始截图");
        // 拿到需要截图的驱动
        WebDriver driver = ((BaseTest) iTestResult.getInstance()).driver;
        // 截图目录
        File screenshotFile = new File(SCREENSHOT_PATH);
        // 若文件夹不存在就创建该文件夹
        if (!screenshotFile.exists() && !screenshotFile.isDirectory()) {
            screenshotFile.mkdirs();
        }
        // 截图格式
        String screenshotFormat = PropertiesReader.getKey("output.screenshot.format");
        // 哪个类导致的截图
        String className = iTestResult.getInstance().getClass().getSimpleName();
        // 时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日H时m分s秒");
        String timeStr = simpleDateFormat.format(new Date());
        // 截图名称
        String screenshotName = className + "-" + timeStr;
        try {
            // 截图操作
            File sourcefile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // 截图存储
            FileUtils.copyFile(sourcefile, new File(SCREENSHOT_PATH + File.separator + screenshotName + screenshotFormat));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("截图操作异常！");
        }
    }
}