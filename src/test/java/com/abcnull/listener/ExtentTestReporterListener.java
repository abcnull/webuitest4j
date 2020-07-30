package com.abcnull.listener;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ResourceCDN;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.TestAttribute;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 定制测试模板监听器
 * ExtentReports 定制模板样式
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/27
 */
@Slf4j
public class ExtentTestReporterListener implements IReporter {
    /**
     * 报告文件存储路径
     */
    private static final String OUTPUT_FOLDER = System.getProperty("user.dir") + File.separator + "target" + File.separator + "test-output" + File.separator + "extentreport" + File.separator;

    /**
     * 报告名
     */
    private static final String FILE_NAME = "WebUI自动化测试报告";

    /**
     * 节点
     */
    private ExtentReports extent;

    /**
     * 生成报告
     *
     * @param xmlSuites       List<XmlSuite>
     * @param suites          List<ISuite>
     * @param outputDirectory String
     */
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        log.info("开始产生 extentreports 测试报告");
        init();
        boolean createSuiteNode = false;
        if (suites.size() > 1) {
            createSuiteNode = true;
        }
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();
            // 如果 suite 里面没有任何用例，直接跳过，不在报告里生成
            if (result.size() == 0) {
                continue;
            }
            // 统计 suite 下的成功、失败、跳过的总用例数
            int suiteFailSize = 0;
            int suitePassSize = 0;
            int suiteSkipSize = 0;
            ExtentTest suiteTest = null;
            // 存在多个 suite 的情况下，在报告中将同一个一个 suite 的测试结果归为一类，创建一级节点。
            if (createSuiteNode) {
                suiteTest = extent.createTest(suite.getName()).assignCategory(suite.getName());
            }
            boolean createSuiteResultNode = false;
            if (result.size() > 1) {
                createSuiteResultNode = true;
            }
            for (ISuiteResult r : result.values()) {
                ExtentTest resultNode;
                ITestContext context = r.getTestContext();
                if (createSuiteResultNode) {
                    // 没有创建 suite 的情况下，将在 SuiteResult 的创建为一级节点，否则创建为 suite 的一个子节点。
                    if (null == suiteTest) {
                        resultNode = extent.createTest(r.getTestContext().getName());
                    } else {
                        resultNode = suiteTest.createNode(r.getTestContext().getName());
                    }
                } else {
                    resultNode = suiteTest;
                }
                if (resultNode != null) {
                    resultNode.getModel().setName(suite.getName() + " : " + r.getTestContext().getName());
                    if (resultNode.getModel().hasCategory()) {
                        resultNode.assignCategory(r.getTestContext().getName());
                    } else {
                        resultNode.assignCategory(suite.getName(), r.getTestContext().getName());
                    }
                    resultNode.getModel().setStartTime(r.getTestContext().getStartDate());
                    resultNode.getModel().setEndTime(r.getTestContext().getEndDate());
                    // 统计 SuiteResult 下的数据
                    int passSize = r.getTestContext().getPassedTests().size();
                    int failSize = r.getTestContext().getFailedTests().size();
                    int skipSize = r.getTestContext().getSkippedTests().size();
                    suitePassSize += passSize;
                    suiteFailSize += failSize;
                    suiteSkipSize += skipSize;
                    if (failSize > 0) {
                        resultNode.getModel().setStatus(Status.FAIL);
                    }
                    resultNode.getModel().setDescription(String.format("Pass: %s ; Fail: %s ; Skip: %s ;", passSize, failSize, skipSize));
                }
                buildTestNodes(resultNode, context.getFailedTests(), Status.FAIL);
                buildTestNodes(resultNode, context.getSkippedTests(), Status.SKIP);
                buildTestNodes(resultNode, context.getPassedTests(), Status.PASS);
            }
            if (suiteTest != null) {
                suiteTest.getModel().setDescription(String.format("Pass: %s ; Fail: %s ; Skip: %s ;", suitePassSize, suiteFailSize, suiteSkipSize));
                if (suiteFailSize > 0) {
                    suiteTest.getModel().setStatus(Status.FAIL);
                }
            }

        }
        extent.flush();
    }

    /**
     * 初始化文件存放路径
     */
    private void init() {
        // 文件夹不存在的话进行创建
        File reportDir = new File(OUTPUT_FOLDER);
        if (!reportDir.exists() && !reportDir.isDirectory()) {
            reportDir.mkdir();
        }
        // 时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日H时m分s秒");
        String timeStr = simpleDateFormat.format(new Date());
        // 产出的报告全名
        String fullFileName = FILE_NAME + "-" + timeStr + ".html";
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(OUTPUT_FOLDER + fullFileName);
        // 设置静态文件的 DNS
        // 怎么样解决 cdn.rawgit.com 访问不了的情况
        htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);

        htmlReporter.config().setDocumentTitle("WebUI自动化测试报告");
        htmlReporter.config().setReportName("WebUI自动化测试报告");
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setCSS(".node.level-1  ul{ display:none;} .node.level-1.active ul{display:block;}");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setReportUsesManualConfiguration(true);
    }

    /**
     * 节点创建
     *
     * @param extenttest ExtentTest
     * @param tests      IResultMap
     * @param status     Status
     */
    private void buildTestNodes(ExtentTest extenttest, IResultMap tests, Status status) {
        // 存在父节点时，获取父节点的标签
        String[] categories = new String[0];
        if (extenttest != null) {
            List<TestAttribute> categoryList = extenttest.getModel().getCategoryContext().getAll();
            categories = new String[categoryList.size()];
            for (int index = 0; index < categoryList.size(); index++) {
                categories[index] = categoryList.get(index).getName();
            }
        }

        ExtentTest test;

        if (tests.size() > 0) {
            // 调整用例排序，按时间排序
            Set<ITestResult> treeSet = new TreeSet<>(new Comparator<ITestResult>() {
                @Override
                public int compare(ITestResult o1, ITestResult o2) {
                    return o1.getStartMillis() < o2.getStartMillis() ? -1 : 1;
                }
            });
            treeSet.addAll(tests.getAllResults());
            for (ITestResult result : treeSet) {
                Object[] parameters = result.getParameters();
                StringBuffer name = new StringBuffer();
                // 如果有参数，则使用参数的 toString 组合代替报告中的 name
                for (Object param : parameters) {
                    name.append(param.toString());
                }
                if (name.length() > 0) {
                    if (name.length() > 50) {
                        name = new StringBuffer(name.substring(0, 49) + "...");
                    }
                } else {
                    name = new StringBuffer(result.getMethod().getMethodName());
                }
                if (extenttest == null) {
                    test = extent.createTest(name.toString());
                } else {
                    // 作为子节点进行创建时，设置同父节点的标签一致，便于报告检索。
                    test = extenttest.createNode(name.toString()).assignCategory(categories);
                }
                // test.getModel().setDescription(description.toString());
                // test = extent.createTest(result.getMethod().getMethodName());
                for (String group : result.getMethod().getGroups())
                    test.assignCategory(group);

                List<String> outputList = Reporter.getOutput(result);
                for (String output : outputList) {
                    // 将用例的 log 输出报告中
                    test.debug(output);
                }
                if (result.getThrowable() != null) {
                    test.log(status, result.getThrowable());
                } else {
                    test.log(status, "Test " + status.toString().toLowerCase() + "ed");
                }

                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));
            }
        }
    }

    /**
     * 将 millis 时长转化为 Calendar 类型时间
     *
     * @param millis 毫秒
     * @return Data
     */
    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}
