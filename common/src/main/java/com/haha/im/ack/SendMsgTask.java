package com.haha.im.ack;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * thread safe
 * @param <T>
 */
public class SendMsgTask<T> {

    private static final Logger logger = LoggerFactory.getLogger(SendMsgTask.class);

    private T msg;
    // need thread safe method
    private Consumer<T> consumer;
    private volatile long processTime;
    private CompletableFuture<T> future;
    private AtomicInteger retry;
    // only process once, if totalRetry is null,
    private final Integer totalRetry;

    public SendMsgTask(T msg, Consumer<T> consumer, Integer totalRetry) {
        this.msg = msg;
        this.consumer = consumer;
        this.future = new CompletableFuture<>();
        this.totalRetry = totalRetry;
        this.retry = new AtomicInteger(0);
    }

    public boolean process() {
        int totalRetryTime = 1;
        if(totalRetry != null) {
            totalRetryTime = totalRetry;
        }
        if(retry.incrementAndGet()<=totalRetryTime) {
            processTime = System.currentTimeMillis();
            try{
                consumer.accept(msg);
            }catch (Exception e) {
                logger.error("process error", e);
            }
            return true;
        }else {
            return false;
        }
    }

    public T getMsg() {
        return msg;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public long getProcessTime() {
        return processTime;
    }

    public boolean complete(T msg) {
        return future.complete(msg);
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }
}
