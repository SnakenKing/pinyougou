package com.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.page.listener *
 * @since 1.0
 */
public class PageMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            //1.循环遍历消息
            for (MessageExt msg : msgs) {
                //2.获取消息体
                byte[] body = msg.getBody();
                //3.转成字符串
                String bodystring = new String(body,"utf-8");// messageinfo的类型的字符串
                //4.转成messageinfo对象  有  methods  add update delete
                MessageInfo messageInfo = JSON.parseObject(bodystring, MessageInfo.class);
                //5.判断方法 做相关的业务处理(生成静态页 删除静态)
                switch (messageInfo.getMethod()) {
                    case 1: {
                        //新增
                        break;
                    }
                    case 2: {
                        //更新

                        //获取到消息体本身 itemList 的字符串类型
                        String s = messageInfo.getContext().toString();
                        //转成数组对象
                        List<TbItem> itemList = JSON.parseArray(s, TbItem.class);
                        //生成静态页
                        Set<Long> set = new HashSet<>();
                        for (TbItem tbItem : itemList) {
                            set.add(tbItem.getGoodsId());
                        }
                        for (Long goodsId : set) {
                            itemPageService.genItemHtml(goodsId);
                        }
                        break;
                    }
                    case 3: {
                        //删除
                        String s = messageInfo.getContext().toString(); //[1232131,12321,313]
                        Long[] longs = JSON.parseObject(s, Long[].class);
                        //移除掉静态页面

                        for (Long aLong : longs) {
                            itemPageService.delete(aLong);

                        }

                        break;
                    }
                    default: {
                        //mor
                        break;
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
