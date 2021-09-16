package com.haha.im.ack;

import com.google.protobuf.ByteString;
import com.haha.im.exception.AckWindowFullException;
import com.haha.im.model.enums.ModuleType;
import com.haha.im.model.enums.MsgMeanType;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.utils.IDGenService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * thread safe
 */
public class ServerAckWindow {
    private static final Logger logger = LoggerFactory.getLogger(ServerAckWindow.class);

    private static final ConcurrentHashMap<String, ServerAckWindow> netId2AckWindow;
    private static final Executor executor;

    static {
        netId2AckWindow = new ConcurrentHashMap<>();
        executor = Executors.newSingleThreadExecutor();
    }

    // todo need concurrentHashMap here?
    private HashMap<String, SendMsgTask<Msg.InternalMsg>> reqId2Task;
    private Integer windowSize;
    private ReentrantLock lock;
    private Long timeout;  // millisecond
    private String netId;
    private Integer retry;

    public ServerAckWindow(String netId, Integer windowSize, Long timeout, Integer retry) {
        this.windowSize = windowSize;
        this.timeout = timeout;
        this.reqId2Task = new HashMap<>();
        this.lock = new ReentrantLock();
        this.netId = netId;
        this.retry = retry;

        netId2AckWindow.put(netId, this);
    }

    public CompletableFuture<Msg.InternalMsg> offer(Msg.InternalMsg msg, ChannelHandlerContext ctx) {
        CompletableFuture<Msg.InternalMsg> exceptionFuture = new CompletableFuture<>();
        if(windowSize != null && reqId2Task.size() >= windowSize) {
            exceptionFuture.completeExceptionally(new AckWindowFullException(netId));
            return exceptionFuture;
        }
        lock.lock();
        try{
            if(reqId2Task.size() >= windowSize) {
                exceptionFuture.completeExceptionally(new AckWindowFullException(netId));
                return exceptionFuture;
            }
            SendMsgTask<Msg.InternalMsg> task = new SendMsgTask<>(msg, ctx::writeAndFlush, retry);
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

    public void ack(Msg.InternalMsg msg) {
        String reqId = msg.getMsgBody().toStringUtf8();
        SendMsgTask<Msg.InternalMsg> sendMsgTask = reqId2Task.get(reqId);
        if(sendMsgTask == null) {
            return;
        }
        sendMsgTask.complete(msg);
        reqId2Task.remove(reqId);
    }

    public static ServerAckWindow getAckWindowByNetId(String netId) {
        return netId2AckWindow.get(netId);
    }


}
