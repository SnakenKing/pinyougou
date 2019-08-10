package com.pinyougou;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * Hello world!
 */
public class App2 {
    public static void main(String[] args) throws Exception{
       //1.创建消费者(设置消费者组 组名)
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group_consumer_1");
        //2.设置nameserv
        consumer.setNamesrvAddr("127.0.0.1:9876");
        //3.订阅主题( tag)
        //参数1 表示订阅的主题名
        //参数2 表示主题里面的tag的名称  * 表示所有的TAG
        consumer.subscribe("TOPIC_TEST","*");
        //4.设置消费者消费模式:1.集群模式 (默认的)2.广播模式
        consumer.setMessageModel(MessageModel.BROADCASTING);
        //5.设置监听器(获取消息)
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    //接收消息
                    System.out.println(context.toString());
                    for (MessageExt msg : msgs) {
                        System.out.println("获取到的消息的ID2222:"+msg.getMsgId());
                        System.out.println("获取到的消息的主题2222:"+msg.getTopic());
                        System.out.println("获取到的消息的TAG2222:"+msg.getTags());
                        //获取消息体
                        byte[] body = msg.getBody();

                        String  bodystring = new String(body);
                        System.out.println(bodystring);

                    }
                    // ConsumeConcurrentlyStatus.CONSUME_SUCCESS 消息消费成功
                    // ConsumeConcurrentlyStatus.RECONSUME_LATER 稍后重新消费 rocket有重试机制.
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        //6.开始监听消息
        consumer.start();
        Thread.sleep(100000);
        //7.关闭
        consumer.shutdown();
    }
}
