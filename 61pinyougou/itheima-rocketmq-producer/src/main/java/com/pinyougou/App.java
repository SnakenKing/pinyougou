package com.pinyougou;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception{
        //1.创建一个生产者

        DefaultMQProducer producer = new DefaultMQProducer("group_produder_1");
        //2.设置生产者组的组名
        //3.设置nameserver
        producer.setNamesrvAddr("127.0.0.1:9876");
        //4.启动
        producer.start();
        //5.创建消息
        String  str = "hello rokcetmq consumer from producer";
        //1.参数 主题的名称
        //2.参数 主题里标签
        //3.参数 业务的唯一标识
        //3.参数 消息体(字节数组) 可以设置编码UTF-8


        Message msg = new Message("TOPIC_TEST","TagA","业务的唯一标识",str.getBytes(RemotingHelper.DEFAULT_CHARSET));
        //6.发送消息
        producer.send(msg);
        //7.关闭资源
        producer.shutdown();
    }
}
