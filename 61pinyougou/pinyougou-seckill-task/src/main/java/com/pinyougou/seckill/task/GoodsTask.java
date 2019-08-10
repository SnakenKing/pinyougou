package com.pinyougou.seckill.task;

import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.task *
 * @since 1.0
 */

@Component
public class GoodsTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //定时执行 该方法  将符合条件的秒杀的商品存储到REDIS

    //cron 表达式  :指定何时执行该方法

    @Scheduled(cron = "0/5 * * * * ?")
    public void pushGoods() {
        //1.从数据库查询符合条件的数据
        //库存>0  status=1
        // 开始<当前时间<结束

        // id not in (123,456)

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThan("stockCount", 0);
        criteria.andEqualTo("status", "1");
        Date date = new Date();
        criteria.andLessThan("startTime", date);//开始<当前时间
        criteria.andGreaterThan("endTime", date);//当前时间<结束

        //排除掉REDIS已有的商品   所有的商品的ID 集合
        Set<Long> keys = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();

        if (keys != null && keys.size() > 0) {
            criteria.andNotIn("id", keys);
        }


        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
        //2.存到redis中  bigkey field  value

        for (TbSeckillGoods seckillGood : seckillGoods) {
            //一个商品就是一个队列   队列的元素的个数 就是该商品的库存数
            pushQueueGoods(seckillGood);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillGood.getId(), seckillGood);
        }

    }

    private  void pushQueueGoods(TbSeckillGoods seckillGood){
        //bound 中的参数就是队列的KEY 要唯一
        // leftPush 中的参数 就是:压入队列的元素

        for (Integer i = 0; i < seckillGood.getStockCount(); i++) {
            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+seckillGood.getId()).leftPush(seckillGood.getId());
        }

    }

}
