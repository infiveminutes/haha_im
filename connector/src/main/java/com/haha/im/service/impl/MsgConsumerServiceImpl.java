package com.haha.im.service.impl;

import com.google.protobuf.Message;
import com.haha.im.model.Proto;
import com.haha.im.service.MsgConsumerService;
import com.haha.im.service.MsgHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsgConsumerServiceImpl implements MsgConsumerService, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(MsgConsumerServiceImpl.class);

    @Autowired
    private List<MsgHandlerService> msgHandlerServiceList;

    private Map<Class<? extends Message>, MsgHandlerService> msgClazz2handler = new HashMap<Class<? extends Message>, MsgHandlerService>();

    public void afterPropertiesSet() throws Exception {
        for(MsgHandlerService msgHandlerService: msgHandlerServiceList) {
            if(msgHandlerService.handleMsgClazz() != null) {
                msgClazz2handler.put(msgHandlerService.handleMsgClazz(), msgHandlerService);
            }
        }
        initConsumer();
    }

    public void initConsumer() {

    }

    public boolean checkMsg(Message msg) {
        if(msg == null) {
            return false;
        }
        return msgClazz2handler.containsKey(msg.getClass());
    }

    public Proto process(Message msg) {
        if(!checkMsg(msg)) {
            // handler not defined
            logger.error("process, not found msg type, {}", msg);
            return Proto.ERROR;
        }
        return msgClazz2handler.get(msg.getClass()).handleMsg(msg);
    }


}
