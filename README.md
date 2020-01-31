[TOC]

**[English]() | [博客](https://blog.csdn.net/abcnull/article/details/104119940)**

# 简介

该项目是 java 编写的使用 selenium 依赖包的 WebUI 测试项目

java + selenium + testng + maven + PO + 多线程 + slf4j + log4j + 截图 + extentreports + redis + 多浏览器 + h5 + 优先级设置 + jenkins + grid 平台

# 框架功能

- PO 思想模式
- 多线程
- slf4j + log4j 日志
- 出错截图
- extentreports 等多种测试报告
- redis 存取值
- 多种浏览器支持测试
- 谷歌 h5 测试支持
- 调整了优先级
- 支持 jenkins job 传参构建
- 支持运行 hub 的浏览器节点

# 使用介绍

将 github 项目拉取到本之后，可以运行本地 testng.xml，或者运行 pom 文件，mvn test 控制台运行也是可行的，本地最好安装一下 redis 服务，没有也可以正常运行，对测试流程和报告产出没有影响，但控制台会显示 redis 连接有问题。

编写时候在关注页面操作，PO 对象拆分成了数据，元素定位和页面操作三个部分放在三个包中，PO 对象需要继承 PageCommon，PO 对象写完之后，直接编写测试用例，测试用例会继承 BaseTest，测试用例调用 PO 的操作方法实现测试流程，将测试用例组织到 testng.xml 中实现，最后可以使用 jenkins 来持续构建

# 关系结构

![1580447619807](https://github.com/abcnull/Image-Resources/blob/master/webuitest4j/1580447619807.png)

- base 包

  有 BaseTest 测试基类，BaseDriver 驱动基类，在 BaseTest 中主要使用到了 BaseDriver 来产生驱动，还使用配置文件读取器去取配置文件和使用了 redis 工具类拿到连接。在 BaseDriver 中主要是做各种浏览器的配置

- common 包

  含有 BrowserCommon 和 PageCommon 两个类，BrowserCommon 主要是对最基本的元素和页面方法进行了，PageCommon 主要是对不同页面相似的复杂操作进行封装

- PO 对象

  data 包关注页面数据，locator 包关注页面元素定位，page 包关注页面操作

- testcase 包

  其中的都是测试用例，测试用例都会继承 BaseTest，并且其中会调用 page 包中的页面类并调用其中的方法进行测试

- testng.xml

  测试用例组织

- listener 包

  测试中的监听器，实现截图，测试报告等

- util 包

  工具类

- 其他

  其他

# 关键代码

- BaseTest 关键代码

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

- 驱动启动关键代码

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

- hub 相关代码

  ```java
  driver = new RemoteWebDriver(new URL("http://" + remoteIP + ":" + remotePort + "/wd/hub/"), chromeOptions);
  ```

- 测试用例代码

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

# 注意点

1. 没有装 redis 的话也没关系，项目运行也能正常执行测试用例，虽然控制台显示 redis 连接不到，但是执行测试用例产出报告一套流程是没有丝毫影响的

2. 项目中运行到 BeforeTest 时候会是一个新的线程，一个线程含有一个驱动和一个 redis 连接

3. pom.xml 文件报红是正常的，并不影响 mvn test 的执行，报红是因为其中进行了外部传参，这个项目可以配置到 jenkins 中，jenkins 的 job 中创建几个参数，这些参数就可以传进 pom 文件中了，再从 pom 文件中成功传入 testng.xml 和测试用例中

4. 建议采用谷歌浏览器，项目中谷歌驱动是 79 版本的

# 待优化

1. 目前支持谷歌，火狐，欧朋，Edge，IE 浏览器，以后会考虑加上对无界面浏览器和 safari 浏览器的支持

2. 目前对于 h5 的支持只有谷歌浏览器，往后会考虑 h5 支持火狐

3. 往后会考虑将 redis 是否启用这一参数加进项目配置文件

4. 往后会考虑项目中编写该项目的异常类型，目前该项目没有编写其自有的异常类