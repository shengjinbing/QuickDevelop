package com.modesty.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangzhiyuan
 */

public class ThreadPoolService {
    private static final ExecutorService SERVICE = Executors.newScheduledThreadPool(1);
    private ThreadPoolService() {}
    public static void execute(Runnable task) {
        SERVICE.execute(task);
    }
}