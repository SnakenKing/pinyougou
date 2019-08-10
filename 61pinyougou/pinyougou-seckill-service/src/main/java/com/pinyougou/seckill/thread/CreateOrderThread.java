package com.pinyougou.seckill.thread;

import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.thread *
 * @since 1.0
 */
public class CreateOrderThread {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;


    //多线程下单
    @Async//异步注解 底层实现就是多线程
    public void handleOrder() {
        //从redis队列中获取商品的数据和用户的数据

        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).rightPop();


        //模拟耗时炒作

        try {
            System.out.println("下单开始=======================================");

            System.out.println("当前的线程的名称:" + Thread.currentThread().getName());

            Thread.sleep(10000);

            System.out.println("下单结束=======================================");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (seckillStatus != null) {
            TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillStatus.getGoodsId());


            //3.减库存 设置回redis中
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount() - 1);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillStatus.getGoodsId(), tbSeckillGoods);


            //4.判断是否减到0 更新到数据库中 删除redis中的秒杀商品

            if (tbSeckillGoods.getStockCount() <= 0) {
                tbSeckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods);
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(seckillStatus.getGoodsId());
            }

            //5.下秒杀订单存储到redis中
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            seckillOrder.setId(new IdWorker(0, 1).nextId());
            seckillOrder.setSeckillId(seckillStatus.getGoodsId());
            seckillOrder.setMoney(tbSeckillGoods.getCostPrice());
            seckillOrder.setUserId(seckillStatus.getUserId());
            seckillOrder.setSellerId(tbSeckillGoods.getSellerId());//
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");//未支付的状态

            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(seckillStatus.getUserId(), seckillOrder);//bigkey field value

            //移除排队标记
            redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());

        }
    }


}
