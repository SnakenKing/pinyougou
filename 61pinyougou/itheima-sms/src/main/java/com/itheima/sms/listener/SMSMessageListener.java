package com.itheima.sms.listener;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.sms.util.SmsUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.sms.listener *
 * @since 1.0
 */
public class SMSMessageListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            //1.循环遍历消息
            for (MessageExt msg : msgs) {
                //2.获取消息体 byte[]
                byte[] body = msg.getBody();
                //3.转成字符串
                String strjson = new String(body, "utf-8");//获取utf-8的编码的数据
                //4.转成JSON独享(map) 手机号 模板 签名 验证码
                Map<String,String> map = JSON.parseObject(strjson, Map.class);
                System.out.println(map);
                //5.调用发短信的API
                SmsUtil.sendSms(map);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;

    }
}
