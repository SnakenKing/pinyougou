package com.pinyougou;

import static org.junit.Assert.assertTrue;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration("classpath:spring-producer.xml")
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private DefaultMQProducer mqProducer;

    @Test
    public void shouldAnswerWithTrue() throws Exception {
        Message msg = new Message("SPRING_TOPIC","SrpingTAGA","业务的key",new String("spring message").getBytes());
        mqProducer.send(msg);
        Thread.sleep(10000000);
    }
}
