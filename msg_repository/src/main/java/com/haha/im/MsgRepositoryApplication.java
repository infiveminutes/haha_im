package com.haha.im;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@MapperScan(value = "com.haha.im.dao")
@SpringBootApplication
@PropertySource( value = "classpath:application.properties")
public class MsgRepositoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgRepositoryApplication.class, args);
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer(){
        return new TomcatServletWebServerFactory(8081) ;
    }
}
