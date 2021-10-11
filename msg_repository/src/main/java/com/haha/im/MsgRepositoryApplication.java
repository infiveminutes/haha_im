package com.haha.im;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.haha.im.dao")
@SpringBootApplication
public class MsgRepositoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgRepositoryApplication.class, args);
    }
}
