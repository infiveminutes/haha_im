package com.haha.im.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class IDGenManager {

    @Bean(name = "snowFlakeWorker")
    public SnowFlakeWorker getSnowFlakeWorker() {
        return new SnowFlakeWorker();
    }
}
