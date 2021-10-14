package com.haha.im;

import com.google.protobuf.ByteString;
import com.haha.im.ioc.TransferIocConfig;
import com.haha.im.model.protobuf.Msg;
import com.haha.im.mq.Producer;
import com.haha.im.server.TransferServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(TransferIocConfig.class);
//        Msg.ChatMsg msg = Msg.ChatMsg.newBuilder().setId(123L).setMsgBody(ByteString.copyFromUtf8("haha")).build();
//        applicationContext.getBean(Producer.class).send(msg);
        TransferServer.start(9199, applicationContext);
    }
}