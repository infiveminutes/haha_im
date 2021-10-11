package com.haha.im.utils;

import java.util.concurrent.*;

public class ThreadPools {
    private static final ExecutorService commonExecutor = new ThreadPoolExecutor(8, 8, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.AbortPolicy());

    public static void submitTask(Runnable runnable) {
        commonExecutor.execute(runnable);
    }

}
