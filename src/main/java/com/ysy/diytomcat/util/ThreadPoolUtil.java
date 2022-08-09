package com.ysy.diytomcat.util;

import java.util.concurrent.*;

public class ThreadPoolUtil {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10));

    public static void run(Runnable r) {
        threadPool.execute(r);
    }
}
