package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.listener *
 * @since 1.0
 */
public class PageMessageListener implements MessageListenerConcurrently {
    //监听消息 消费消息
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        //1.循环遍历消息对象

        try {
            for (MessageExt msg : msgs) {
                //2.获取消息体(字节数组)
                byte[] body = msg.getBody();

                //3.转成STRING
                String strjson = new String(body, "utf-8");

                //4.转成messageinfo对象
                MessageInfo messageInfo = JSON.parseObject(strjson, MessageInfo.class);


                //5.判断 方法类型(/add/updadate/delete) 进行 相关的处理
                if (messageInfo.getMethod() == MessageInfo.METHOD_ADD) {//生成静态页
                    //6.使用freemarker生成静态页
                    String idsstring = messageInfo.getContext().toString();
                    Long[] longs = JSON.parseObject(idsstring, Long[].class);
                    for (Long aLong : longs) {
                        genHTML("item.ftl", aLong);
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Value("${pageDir}")
    private String pageDir;

    /**
     * 模板 +  数据集 =html
     *
     * @param templateName 模板名称
     * @param id           要生成的秒杀商品的ID
     */
    private void genHTML(String templateName, Long id) {
        FileWriter writer = null;
        try {
            //1.创建配置类configuration

            //2.设置模板所在的目录 和字符编码

            Configuration configuration = configurer.getConfiguration();

            //3.创建模板文件,加载模板文件

            Template template = configuration.getTemplate(templateName);

            //4.创建数据集
            Map model = new HashMap();

            //查询秒杀商品的数据
            TbSeckillGoods tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
            model.put("seckillGoods", tbSeckillGoods);


            //5.创建写流 指定输出的文件名
            writer = new FileWriter(new File(pageDir + id + ".html"));

            //6.执行生成的动作
            template.process(model, writer);

            //7.关闭流

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
