package com.haha.im.mq;

import com.google.protobuf.Message;
import com.haha.im.model.protobuf.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@PropertySource( value = "classpath:application.properties")
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "${kafka.topic}", containerFactory = "byteArrayKafkaListenerContainerFactory")
    public void listen(byte[] msgData) {
        Message message = MqMsgDecode.decode(msgData);
        handleMsg(message);
        logger.info("demo receive : " + message.toString());
    }

    private void handleMsg(Message msg){
        if(msg instanceof Msg.ChatMsg) {
            handleChat((Msg.ChatMsg) msg);
        }else if(msg instanceof Msg.AckMsg) {
            handleAck((Msg.AckMsg)msg);
        }else {
            logger.error("handleMsg error, can't handle msg, class:" + msg.getClass());
        }
    }

    private void handleChat(Msg.ChatMsg chatMsg) {
        // todo
    }

    private void handleAck(Msg.AckMsg ackMsg) {
        // todo
    }
}
