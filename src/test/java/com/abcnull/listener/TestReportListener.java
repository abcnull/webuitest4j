package com.abcnull.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 测试报告监听器
 * BeautifulReport 模板样式
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2020/1/26
 */
@Slf4j
public class TestReportListener implements IReporter {
    /**
     * Data 日期
     */
    private static final Date date = new Date();

    /**
     * 日期类型
     */
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日H时m分s秒");

    /**
     * 报告时间
     */
    private static final String reportdate = simpleDateFormat.format(date);

    /**
     * 报告名字
     */
    private static final String reportName = "WebUI自动化测试报告-" + reportdate;

    /**
     * 产出报告依据的模板的路径（包括模板文件）
     */
    private final String templatePath = this.getClass().getResource("/").getPath() + "report" + File.separator + "template.html";

    /**
     * 产出报告的路径（不包括报告文件）
     */
    private final String reportDirPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "test-output" + File.separator + "report";

    /**
     * 产出报告的路径（包括报告文件）
     */
    private final String reportPath = reportDirPath + File.separator + reportName + ".html";

    /**
     * 测试成功数
     */
    private int testsPass;

    /**
     * 测试失败数
     */
    private int testsFail;

    /**
     * 测试跳过数
     */
    private int testsSkip;

    /**
     * 开始时间字符串
     */
    private String beginTime;

    /**
     * 总时间字符串
     */
    private long totalTime;

    /**
     * 测试报告标题
     */
    private final String project = "WebUI自动化测试报告";

    /**
     * 生成报告
     *
     * @param xmlSuites       List<XmlSuite>
     * @param suites          List<ISuite>
     * @param outputDirectory String
     */
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        log.info("开始产生 BeautifulReport 测试报告");
        List<ITestResult> list = new ArrayList<>();
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> suiteResults = suite.getResults();
            for (ISuiteResult suiteResult : suiteResults.values()) {
                ITestContext testContext = suiteResult.getTestContext();
                IResultMap passedTests = testContext.getPassedTests();
                testsPass = testsPass + passedTests.size();
                IResultMap failedTests = testContext.getFailedTests();
                testsFail = testsFail + failedTests.size();
                IResultMap skippedTests = testContext.getSkippedTests();
                testsSkip = testsSkip + skippedTests.size();
                IResultMap failedConfig = testContext.getFailedConfigurations();
                list.addAll(this.listTestResult(passedTests));
                list.addAll(this.listTestResult(failedTests));
                list.addAll(this.listTestResult(skippedTests));
                list.addAll(this.listTestResult(failedConfig));
            }
        }
        this.sort(list);
        this.outputResult(list);
    }

    /**
     * 将 IResultMap 转化成 ArrayList<ITestResult>
     *
     * @param resultMap IResultMap
     * @return ArrayList<ITestResult>
     */
    private ArrayList<ITestResult> listTestResult(IResultMap resultMap) {
        Set<ITestResult> results = resultMap.getAllResults();
        return new ArrayList<>(results);
    }

    /**
     * 调整顺序
     *
     * @param list List<ITestResult>
     */
    private void sort(List<ITestResult> list) {
        list.sort((r1, r2) -> r1.getStartMillis() < r2.getStartMillis() ? -1 : 1);
    }

    /**
     * 拿到总时长
     *
     * @return long
     */
    public long getTime() {
        return totalTime;
    }

    /**
     * 往模板中输出测试报告
     *
     * @param list List<ITestResult>
     */
    private void outputResult(List<ITestResult> list) {
        try {
            List<ReportInfo> listInfo = new ArrayList<>();
            int index = 0;
            for (ITestResult result : list) {
                String testName = result.getTestContext().getCurrentXmlTest().getName();
                if (index == 0) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    beginTime = formatter.format(new Date(result.getStartMillis()));
                    index++;
                }
                long spendTime = result.getEndMillis() - result.getStartMillis();
                totalTime += spendTime;
                String status = this.getStatus(result.getStatus());
                List<String> log = Reporter.getOutput(result);
                for (int i = 0; i < log.size(); i++) {
                    log.set(i, log.get(i).replaceAll("\"", "\\\\\""));
                }
                Throwable throwable = result.getThrowable();
                if (throwable != null) {
                    log.add(throwable.toString().replaceAll("\"", "\\\\\""));
                    StackTraceElement[] st = throwable.getStackTrace();
                    for (StackTraceElement stackTraceElement : st) {
                        log.add(("    " + stackTraceElement).replaceAll("\"", "\\\\\""));
                    }
                }
                ReportInfo info = new ReportInfo();
                info.setName(testName);
                info.setSpendTime(spendTime + "ms");
                info.setStatus(status);
                info.setClassName(result.getInstanceName());
                info.setMethodName(result.getName());
                info.setDescription(result.getMethod().getDescription());
                info.setLog(log);
                listInfo.add(info);
            }
            Map<String, Object> result = new HashMap<>();
            //result.put("testName", name);
            System.out.printf("测试用例运行总时间：%.3f(min)\n", ((double)(totalTime/1000)/60));
            result.put("testName", this.project);
            result.put("testPass", testsPass);
            result.put("testFail", testsFail);
            result.put("testSkip", testsSkip);
            result.put("testAll", testsPass + testsFail + testsSkip);
            result.put("beginTime", beginTime);
            result.put("totalTime", totalTime + "ms");
            result.put("testResult", listInfo);
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String template = this.read(reportDirPath, templatePath);
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(reportPath)), StandardCharsets.UTF_8));
            assert template != null;
            template = template.replace("${resultData}", gson.toJson(result));
            output.write(template);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拿到状态
     *
     * @param status int 型状态
     * @return String 型状态
     */
    private String getStatus(int status) {
        String statusString = null;
        switch (status) {
            case 1:
                statusString = "成功";
                break;
            case 2:
                statusString = "失败";
                break;
            case 3:
                statusString = "跳过";
                break;
            default:
                break;
        }
        return statusString;
    }

    /**
     * 报告信息
     */
    public static class ReportInfo {
        /**
         * 测试名字
         */
        private String name;

        /**
         * 测试类名
         */
        private String className;

        /**
         * 测试方法名
         */
        private String methodName;

        /**
         * 测试方法中的描述信息
         */
        private String description;

        /**
         * 测试所耗时间
         */
        private String spendTime;

        /**
         * 测试状态
         */
        private String status;

        /**
         * 测试日志列表
         */
        private List<String> log;

        /**
         * 拿到名字
         *
         * @return 名字
         */
        public String getName() {
            return name;
        }

        /**
         * 设置名字
         *
         * @param name 名字
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 拿到类名
         *
         * @return 类名
         */
        public String getClassName() {
            return className;
        }

        /**
         * 设置类名
         *
         * @param className 类名
         */
        public void setClassName(String className) {
            this.className = className;
        }

        /**
         * 拿到方法名
         *
         * @return 方法名
         */
        public String getMethodName() {
            return methodName;
        }

        /**
         * 设置方法名
         *
         * @param methodName 方法名
         */
        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        /**
         * 拿到所耗时间
         *
         * @return 时间
         */
        public String getSpendTime() {
            return spendTime;
        }

        /**
         * 设置所耗时间
         *
         * @param spendTime 时间
         */
        public void setSpendTime(String spendTime) {
            this.spendTime = spendTime;
        }

        /**
         * 拿到状态
         *
         * @return 状态
         */
        public String getStatus() {
            return status;
        }

        /**
         * 设置状态
         *
         * @param status 状态
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * 拿到日志列表
         *
         * @return 日志列表
         */
        public List<String> getLog() {
            return log;
        }

        /**
         * 设置日志列表
         *
         * @param log 日志列表
         */
        public void setLog(List<String> log) {
            this.log = log;
        }

        /**
         * 拿到描述信息
         *
         * @return 描述信息
         */
        public String getDescription() {
            return description;
        }

        /**
         * 设置描述信息
         *
         * @param description 描述信息
         */
        public void setDescription(String description) {
            this.description = description;
        }

    }

    /**
     * 读取模板操作
     *
     * @param reportDirPath 报告路径（不包括报告文件）
     * @param templatePath  模板路径（包括模板文件）
     * @return 模板内容
     */
    private String read(String reportDirPath, String templatePath) {
        // 文件夹不存在时级联创建目录
        File reportDir = new File(reportDirPath);
        if (!reportDir.exists() && !reportDir.isDirectory()) {
            reportDir.mkdirs();
        }
        File templateFile = new File(templatePath);
        InputStream inputStream = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            inputStream = new FileInputStream(templateFile);
            int index;
            byte[] b = new byte[1024];
            while ((index = inputStream.read(b)) != -1) {
                stringBuffer.append(new String(b, 0, index));
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}