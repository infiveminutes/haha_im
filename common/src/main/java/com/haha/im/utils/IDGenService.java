package com.haha.im.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IDGenService {

    private static SnowFlakeWorker snowFlakeWorker;
    private static AtomicLong netIdGen;

    static {
        snowFlakeWorker = new SnowFlakeWorker();
        netIdGen = new AtomicLong(0);
    }

    public static long getSnowFlakeId() {
        return snowFlakeWorker.nextId();
    }

    public static long getNextNetId() {
        return netIdGen.getAndIncrement();
    }
}
