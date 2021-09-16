package com.haha.im.ack;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * thread safe
 * @param <T>
 */
public class SendMsgTask<T> {
    private T msg;
    private Consumer<T> consumer;
    private long processTime;
    private CompletableFuture<T> future;
    private AtomicInteger retry;
    // only process once, if totalRetry is null,
    private final Integer totalRetry;

    public SendMsgTask(T msg, Consumer<T> consumer, Integer totalRetry) {
        this.msg = msg;
        this.consumer = consumer;
        this.future = new CompletableFuture<>();
        this.totalRetry = totalRetry;
    }

    public boolean process() {
        int totalRetryTime = 1;
        if(totalRetry != null) {
            totalRetryTime = totalRetry;
        }
        if(retry.incrementAndGet()<=totalRetryTime) {
            processTime = System.currentTimeMillis();
            consumer.accept(msg);
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
