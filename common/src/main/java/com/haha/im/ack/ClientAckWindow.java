package com.haha.im.ack;

import com.haha.im.exception.AckWindowFullException;
import com.haha.im.model.protobuf.Msg;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ClientAckWindow {
    private static final Logger logger = LoggerFactory.getLogger(ClientAckWindow.class);

    private ConcurrentHashMap<String, SendMsgTask<Msg.ChatMsg>> reqId2Task;
    private Integer windowSize;
    private ReentrantLock lock;
    private Long timeout;  // millisecond
    private String netId;
    private Integer retry;
    private volatile ExecutorService executor;

    public ClientAckWindow(String netId, Integer windowSize, Long timeout, Integer retry) {
        this.windowSize = windowSize;
        this.timeout = timeout;
        this.reqId2Task = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
        this.netId = netId;
        this.retry = retry;
        this.executor = Executors.newSingleThreadExecutor();

        executor.submit(this::doRetryAndClean);
    }

    public CompletableFuture<Msg.ChatMsg> offer(Msg.ChatMsg msg, ChannelHandlerContext ctx) {
        CompletableFuture<Msg.ChatMsg> exceptionFuture = new CompletableFuture<>();
        if(windowSize == null || reqId2Task.size() >= windowSize) {
            exceptionFuture.completeExceptionally(new AckWindowFullException(netId));
            return exceptionFuture;
        }
        lock.lock();
        try{
            if(reqId2Task.size() >= windowSize) {
                exceptionFuture.completeExceptionally(new AckWindowFullException(netId));
                return exceptionFuture;
            }
            SendMsgTask<Msg.ChatMsg> task = new SendMsgTask<>(msg, ctx::writeAndFlush, retry);
            boolean processSuc = task.process();
            if(!processSuc) {
                exceptionFuture.completeExceptionally(new Exception("retry over time"));
                return exceptionFuture;
            }
            reqId2Task.put(String.valueOf(msg.getId()), task);
            return task.getFuture();
        }catch (Exception e) {
            logger.error("offer error", e);
            exceptionFuture.completeExceptionally(e);
            return exceptionFuture;
        }
        finally {
            lock.unlock();
        }
    }

    public void ack(Msg.AckMsg msg) {
        String reqId = String.valueOf(msg.getAckMsgId());
        SendMsgTask<Msg.ChatMsg> sendMsgTask = reqId2Task.get(reqId);
        if(sendMsgTask == null) {
            return;
        }
        sendMsgTask.complete(sendMsgTask.getMsg());
        reqId2Task.remove(reqId);
    }

    private void doRetryAndClean() {
        while(true) {
            for (Map.Entry<String, SendMsgTask<Msg.ChatMsg>> entry : reqId2Task.entrySet()) {
                if (timeout < System.currentTimeMillis() - entry.getValue().getProcessTime()) {
                    // timeout, retry
                    if (!entry.getValue().process()) {
                        // Run out of retries
                        entry.getValue().getFuture().completeExceptionally(new Exception("retry over time"));
                        reqId2Task.remove(entry.getKey());
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("doRetryAndClean error", e);
            }
        }
    }


}
