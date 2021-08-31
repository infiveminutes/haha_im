package com.haha.im.start;

import com.haha.im.ioc.ConnectorIocConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class main {
    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(ConnectorIocConfig.class);
        Object o = applicationContext.getBean("snowFlakeWorker");
        o = applicationContext.getBean("IDGenManager");
        System.out.println(o);
    }
}
