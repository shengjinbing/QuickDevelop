package com.modesty.logger.file;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

class ThreadPoolService {
    private static final ExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    private ThreadPoolService() {
    }

    public static void execute(Runnable task) {
        SERVICE.execute(task);
    }
}
