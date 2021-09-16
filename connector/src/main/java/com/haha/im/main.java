package com.haha.im;

import com.haha.im.ioc.ConnectorIocConfig;
import com.haha.im.start.ConnectorServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class main {
    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(ConnectorIocConfig.class);
        ConnectorServer.start(9098, applicationContext);
    }
}
