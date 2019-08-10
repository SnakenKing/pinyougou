package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.listener *
 * @since 1.0
 */
public class SearchMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

        try {
            //1.循环遍历消息
            for (MessageExt msg : msgs) {
                //2.获取消息体  自己数组
                byte[] body = msg.getBody();
                //3.转成字符串
                String bodystrng = new String(body);
                //4.转成对象  (有METHOD)
                MessageInfo messageInfo = JSON.parseObject(bodystrng, MessageInfo.class);

                //5.判断METHOD的值 去执行到底是 add/update/delete

                switch (messageInfo.getMethod()) {
                    case 1: {
                        //新增
                        break;
                    }
                    case 2: {
                        //更新
                        //1.获取messageinfo中的sku的列表数据
                        String context1 = messageInfo.getContext().toString();//SKU的列表的数据的SJON字符串 List<tbitem> =[{},{}]

                        //2.调用服务的方法 更新到ES中
                        List<TbItem> itemList = JSON.parseArray(context1, TbItem.class);
                        itemSearchService.updateIndex(itemList);
                        break;
                    }
                    case 3: {
                        //删除
                        //获取messageinfo里面的数组数据 long[]
                        //转成字符串
                        String context1 = messageInfo.getContext().toString();// [1,2,3,3]
                        //转成对象
                        Long[] ids = JSON.parseObject(context1, Long[].class);
                        //删除ES的数据
                        itemSearchService.deleteByIds(ids);
                        break;
                    }
                    default: {
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
