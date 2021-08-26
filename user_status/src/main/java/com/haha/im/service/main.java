package com.haha.im.service;

import com.haha.im.ioc.CommonIocConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class main {
    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(CommonIocConfig.class);
        Object o = applicationContext.getBean("snowFlakeWorker");
        o = applicationContext.getBean("IDGenManager");
        System.out.println(o);
    }
}
