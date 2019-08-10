package com.pinyougou;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration("classpath:spring-consumer.xml")
@RunWith(SpringRunner.class)
public class AppTest {



    @Test
    public void shouldAnswerWithTrue() {

        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
