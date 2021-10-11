package com.haha.im.ioc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;


@ComponentScan(value = "com.haha.im")
@PropertySource( value = "classpath:application.yml")
public class TransferIocConfig {
}
