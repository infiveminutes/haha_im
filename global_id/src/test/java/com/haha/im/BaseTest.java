package com.haha.im;

import com.haha.im.service.UserMsgIdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GlobalIdApplication.class)
public class BaseTest {

    @Autowired
    private UserMsgIdService userMsgIdService;


    Executor executor = Executors.newFixedThreadPool(10);

    @Test
    public void aaa() {
        for(int i=0; i<10000; i++) {
            executor.execute(this::run);
        }
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        System.out.println(Thread.currentThread()+ "----"+ userMsgIdService.getNextMsgId("haha"));
    }

}
