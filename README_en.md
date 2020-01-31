[TOC]

**[中文](https://github.com/abcnull/webuitest4j) | [Blog](https://blog.csdn.net/abcnull/article/details/104119940)**

# Brief Intro

This project is a WebUI test project written in Java that uses selenium dependencies

java + selenium + testng + maven + PO + multithreaded + slf4j + log4j2 + screenshot + extentreports + redis + muti-driver + h5 + priority + jenkins + grid

# Project Features

- PO mode
- multithreaded runs
- slf4j + log4j2 log system
- error screenshot
- extentreports and other reports
- redis set and get key
- muti-driver
- chrome h5 support
- reset priority
- params of jenkins job support 
- hub support

# Usage

After pulling out the github project, you can run the local testng.xml or run the pom file. `mvn test` in console is also available. It is best to install the redis service locally

The PO object is split into data, and element location and page operation are placed in three packages.The PO object needs to inherit from PageCommon.After writing the PO object, I wrote the test case directly. The test case would inherit the BaseTest. The test case would call the operation method of the PO to realize the test process. I organized the test cases into testng.xml, which I could use Jenkins to keep building

# Project Structure

![1580447619807](https://github.com/abcnull/Image-Resources/blob/master/webuitest4j/1580447619807.png)

- base package

  There are BaseTest test base classes, BaseDriver drive base classes.In BaseTest, BaseDriver is mainly used to generate the driver, the configuration file reader is used to fetch the configuration file and the redis utility class is used to fetch the connection.In BaseDriver, it's all about configuring browsers

- common package

  There are two classes BrowserCommon and PageCommon, BrowserCommon is mainly for the most basic elements and page methods, PageCommon is mainly for different pages similar complex operations to encapsulate

- PO object

  The data package focuses on page data, the locator package focuses on page element positioning, and the page package focuses on page operations

- testcase package

  All of these are test cases, which inherit from BaseTest and call the page class in the page package and call its methods for testing

- testng.xml

  Test case organization

- listener package

  The listener in the test can realize screenshots, test reports, etc

- util package

  Utility

- others

  Others

# Key Code

- BaseTest key code

  ```java
  @BeforeTest(alwaysRun = true)
      @Parameters({"browserName", "terminal", "deviceName", "remoteIP", "remotePort", "browserVersion"})
      public void beforeTest(@Optional("chrome") String browserName, @Optional("pc") String terminal, @Optional("desktop") String deviceName, @Optional() String remoteIP, @Optional("4444") int remotePort, @Optional() String browserVersion) throws Exception {
          /* redis 新连接获取 */
          redisUtil = new RedisUtil();
          // 拿到一个新的 jedis 连接，设置 redisUtil 中的 jedis 以及键值超时时间
          redisUtil.setJedisAndExpire(redisUtil.getNewJedis());
          jedis = redisUtil.getJedis();
          /* 驱动配置 */
          baseDriver = new BaseDriver();
          // 如果不存在 hub 地址
          if (remoteIP == null || remoteIP.isEmpty()) {
              baseDriver.startBrowser(browserName, terminal, deviceName);
          }
          // 如果存在 hub 地址
          else {
              baseDriver.startBrowser(browserName, terminal, deviceName, remoteIP, remotePort, browserVersion);
          }
          driver = baseDriver.getDriver();
      }
  ```

- the driver starts the relevant key code

  ```java
  try {
                      // 系统变量设置谷歌驱动
                      System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                      // 下载地址设置
                      HashMap<String, Object> hashMap = new HashMap<>();
                      hashMap.put("download.default_directory", downloadPath);
                      // 驱动可选项配置
                      ChromeOptions chromeOptions = new ChromeOptions();
                      chromeOptions.setExperimentalOption("prefs", hashMap);
                      chromeOptions.addArguments("--no-sandbox");
                      chromeOptions.addArguments("--disable-dev-shm-usage");
                      // 如果是 h5 需要另外设置
                      if (terminal.equals("h5")) {
                          Map<String, String> mobileEmulationMap = new HashMap<>();
                          mobileEmulationMap.put("deviceName", deviceName);
                          chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulationMap);
                      }
                      // 启动 RemoteWebDriver
                      driver = new ChromeDriver(chromeOptions);
                  } catch (Exception e) {
                      e.printStackTrace();
                      log.error(browserName + "浏览器启动失败！");
                  }
  ```

- hub relevant key code

  ```java
  driver = new RemoteWebDriver(new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/"), chromeOptions);
  ```

- testcase code

  ```java
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
  ```

# Notice

1. It doesn't matter if the redis service is not installed, and the project can execute the test cases normally. Although the console shows that redis is not connected, the process of executing the test cases and the output report is not affected at all

2. Running in the project BeforeTest will be a new thread, a thread containing a driver and a redis connection

3. pom.xml file appearing red line is normal and does not affect the execution of `mvn test`, because external parameters are passed.This project can be configured into Jenkins. Jenkins's job creates several parameters that can be passed into the pom file, from which they can be passed into testng.xml and test cases

4. The chrome browser is recommended, and the chrome driver in the project is version 79

# To Optimize

1. It currently supports chrome, firefox, opera, Edge and Internet explorer, but will be considered for non-interface browsers and safari

2. Chrome is the only browser currently available for h5, but firefox will be considered in the future

3. I'll consider adding redis enablement to the project profile later

4. In the future, I'll consider writing the exception type for the project, which currently does not write its own exception classes