package com.haha.im.ack;


import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SendMsgTask<T> {
    private T msg;
    private Consumer<T> consumer;
    private long processTime;
    private CompletableFuture<T> future;

    public SendMsgTask(T msg, Consumer<T> consumer) {
        this.msg = msg;
        this.consumer = consumer;
        this.future = new CompletableFuture<>();
    }

    public CompletableFuture<T> process() {
        processTime = System.currentTimeMillis();
        consumer.accept(msg);
        return future;
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
}
