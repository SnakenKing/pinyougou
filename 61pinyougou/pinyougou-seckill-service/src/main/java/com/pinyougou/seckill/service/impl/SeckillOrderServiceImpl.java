package com.pinyougou.seckill.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.seckill.thread.CreateOrderThread;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder> implements SeckillOrderService {


    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, TbSeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (StringUtils.isNotBlank(seckillOrder.getUserId())) {
                criteria.andLike("userId", "%" + seckillOrder.getUserId() + "%");
                //criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getSellerId())) {
                criteria.andLike("sellerId", "%" + seckillOrder.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getStatus())) {
                criteria.andLike("status", "%" + seckillOrder.getStatus() + "%");
                //criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverAddress())) {
                criteria.andLike("receiverAddress", "%" + seckillOrder.getReceiverAddress() + "%");
                //criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + seckillOrder.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiver())) {
                criteria.andLike("receiver", "%" + seckillOrder.getReceiver() + "%");
                //criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getTransactionId())) {
                criteria.andLike("transactionId", "%" + seckillOrder.getTransactionId() + "%");
                //criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
            }

        }
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CreateOrderThread createOrderThread;

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Override
    public void submitOrder(String userId, Long id) {


        //先判断是否正在排队中
        Object userFlag = redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId);

        if (userFlag != null) {
            throw new RuntimeException("正在排队中,不要再点了");
        }

        //判断是否有未支付的订单 如果有 响应
        Object o = redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if (o != null) {//有订单
            throw new RuntimeException("有未支付的订单");
        }


        //1.从REDIS中获取商品的数据
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(id);

        //2.判断 是否有库存  如果 没有 直接抛出异常 卖完了
       /* if (tbSeckillGoods == null || tbSeckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("已售罄");
        }*/

        Object seckillID = redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + id).rightPop();

        if (seckillID == null) {
            throw new RuntimeException("已售罄");
        }

        //将用户压入 排队的 队列中
        redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).leftPush(new SeckillStatus(userId, id, SeckillStatus.SECKILL_queuing));


        //将用户设置一个标志(标识该用户正在排队中) key value

        redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId, id);


        //多线程调用下单的业务


        createOrderThread.handleOrder();


    }

    @Override
    public TbSeckillOrder getOrderByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
    }

    @Override
    public void updateStatus(String userId, String transaction_id) {

        //1.根据用户的ID 获取该用户的预订单
        TbSeckillOrder seckillOrder = this.getOrderByUserId(userId);

        //2.更新预的订单状态( 支付的时间 状态,交易流水)
        seckillOrder.setStatus("1");//已经支付
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setTransactionId(transaction_id);

        //3.更新到数据库中
        seckillOrderMapper.insertSelective(seckillOrder);

        //4.删除预订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);


    }

    @Autowired
    private TbSeckillGoodsMapper getTbSeckillGoodsMapper;

    @Override
    public void deleteOrder(String userId) {
        //1.获取订单
        TbSeckillOrder order = getOrderByUserId(userId);
        //2.获取订单的买的商品的ID
        Long seckillId = order.getSeckillId();
        //3.获取商品的ID 对应的商品对象数据
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);
        if(tbSeckillGoods!=null) {
            //4.恢复库存
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount() + 1);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,tbSeckillGoods);
        }else{
            //从数据库获取商品数据 恢复库存
            TbSeckillGoods tbSeckillGoods1 = getTbSeckillGoodsMapper.selectByPrimaryKey(seckillId);
            tbSeckillGoods1.setStockCount(1);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,tbSeckillGoods1);
        }

        //压入队里
        redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).leftPush(seckillId);
        //5.删除预订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
    }

}
