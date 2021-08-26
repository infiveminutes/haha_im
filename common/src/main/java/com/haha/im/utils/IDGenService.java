package com.haha.im.utils;

public class IDGenService {

    private static SnowFlakeWorker snowFlakeWorker;

    static {
        snowFlakeWorker = new SnowFlakeWorker();
    }

    public static long getSnowFlakeId() {
        return snowFlakeWorker.nextId();
    }
}
