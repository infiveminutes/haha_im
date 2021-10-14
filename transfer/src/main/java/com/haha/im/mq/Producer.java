package com.haha.im.mq;

import com.google.protobuf.Message;
import com.haha.im.model.enums.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
@PropertySource( value = "classpath:application.properties")
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, byte[]> template;

    public void send(Message message) {
        try{
            int code = MsgType.clazz2Enum(message.getClass()).getCode();

            byte[] srcB = message.toByteArray();
            byte[] destB = new byte[srcB.length + 1];
            destB[0] = (byte) code;

            System.arraycopy(message.toByteArray(), 0, destB, 1, message.toByteArray().length);
            template.send(topic, destB).get();
        }catch (Exception e) {
            logger.error("send error", e);
        }

    }
}
