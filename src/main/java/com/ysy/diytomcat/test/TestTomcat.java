package com.ysy.diytomcat.test;


import cn.hutool.core.date.*;
import cn.hutool.core.util.*;
import com.ysy.diytomcat.util.MiniBrowser;
import org.junit.*;

import java.util.concurrent.*;

public class TestTomcat {
    private static int port = 18080;
    private static String ip = "127.0.0.1";

    @BeforeClass
    public static void beforeClass() {
        //所有测试开始前看diy tomcat 是否已经启动了
        if (NetUtil.isUsableLocalPort(port)) {
            System.err.println("请先启动 位于端口: " + port + " 的diy tomcat，否则无法进行单元测试");
            System.exit(1);
        } else {
            System.out.println("检测到 diy tomcat已经启动，开始进行单元测试");
        }
    }

    @Test
    public void testHelloTomcat() {
        String html = getContentString("/");
        Assert.assertEquals(html, "Hello DIY Tomcat");
    }

    @Test
    public void testaHtml() {
        String html = getContentString("/a.html");
        Assert.assertEquals(html, "I am fun, Thanks.");
    }

    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));
        TimeInterval timeInterval = DateUtil.timer();
        //连续执行3个任务，可以简单地理解成3个任务同时开始
        for (int i = 0; i < 3; i++) {
            threadPool.execute(new Runnable() {
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }
        //shutdown 尝试关闭线程池，但是如果 线程池里有任务在运行，就不会强制关闭，直到任务都结束了，才关闭。
        threadPool.shutdown();
        //会给线程池1个小时的时间去执行，如果超过1个小时了也会返回，如果在一个小时内任务结束了，就会马上返回。
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        long duration = timeInterval.intervalMs();

        Assert.assertTrue(duration < 3000);
    }

    @Test
    public void testbIndex() {
        String html = getContentString("/b/index.html");
        Assert.assertEquals(html, "Hello DIY Tomcat from index.html@b");
    }

    @Test
    public void testaIndex() {
        String html = getContentString("/a/index.html");
        Assert.assertEquals(html, "Hello DIY Tomcat from index.html@a");
    }

    @Test
    public void test404() {
        String response = getHttpString("/not_exist.html");
        containAssert(response, "HTTP/1.1 404 Not Found");
    }

    @Test
    public void test500() {
        String response = getHttpString("/500.html");
        containAssert(response, "HTTP/1.1 500 Internal Server Error");
    }

    private void containAssert(String html, String string) {
        boolean match = StrUtil.containsAny(html, string);
        Assert.assertTrue(match);
    }

    private String getHttpString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String http = MiniBrowser.getHttpString(url);
        return http;
    }

    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }
}