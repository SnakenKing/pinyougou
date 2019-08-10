package com.pinyougou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration("classpath:spring-consumer2.xml")
@RunWith(SpringRunner.class)
public class AppTest2 {



    @Test
    public void shouldAnswerWithTrue() {

        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
