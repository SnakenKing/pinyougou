package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.order.service.impl *
 * @since 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private IdWorker idWorker;

    /**
     * 1. 订单拆单 一个商家就是一个订单
     * 2. id生成
     *
     * @param order
     */
    @Override
    public void add(TbOrder order) {

        //1.从redis中获取到购物车列表 (List<Cart>) 一个CART就是一个商家


        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART_REDIS_PREFIX").get(order.getUserId());

        double totalFee = 0;

        List<String> orderList = new ArrayList<>();

        for (Cart cart : cartList) {//
            //2.循环遍历 创建订单表 要拆单
            TbOrder ordernew = new TbOrder();
            long orderId = idWorker.nextId();
            orderList.add(orderId + "");//
            ordernew.setOrderId(orderId);//不能自增需要生成
            //ordernew.setPayment();//计算 //todo
            ordernew.setPaymentType(order.getPaymentType());
            ordernew.setPostFee("0");//包邮
            ordernew.setStatus("1");//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关
            ordernew.setCreateTime(new Date());
            ordernew.setUpdateTime(ordernew.getCreateTime());
            ordernew.setUserId(order.getUserId());
            ordernew.setReceiverAreaName(order.getReceiverAreaName());//收货地址
            ordernew.setReceiverMobile(order.getReceiverMobile());//手机号
            ordernew.setReceiverZipCode("518000");//邮编
            ordernew.setReceiver(order.getReceiver());//收货人
            ordernew.setSourceType("2");//PC
            ordernew.setSellerId(cart.getSellerId());//商家的ID


            //用户在每一个商家买的商品的明细列表
            double totalMoney = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //3.创建订单选项表
                //3.1 生成选项的iD
                long orderItemId = idWorker.nextId();

                orderItem.setId(orderItemId);

                //3.2 订单的ID
                orderItem.setOrderId(orderId);


                totalMoney += orderItem.getTotalFee().doubleValue();

                orderItemMapper.insert(orderItem);
            }

            totalFee += totalMoney;//所有的商家的总金额  元

            ordernew.setPayment(new BigDecimal(totalMoney));//应付金额

            orderMapper.insert(ordernew);


        }

        //创建支付日志 记录
        TbPayLog payLog = new TbPayLog();


        payLog.setOutTradeNo(idWorker.nextId() + "");
        payLog.setCreateTime(new Date());
        double v = totalFee * 100;
        long fen = (long) v;
        payLog.setTotalFee(fen);
        payLog.setUserId(order.getUserId());

        payLog.setTradeState("0");//未支付
        payLog.setOrderList(orderList.toString().replace("[", "").replace("]", ""));//    [1,2,3]
        payLog.setPayType("1");//微信支付
        payLogMapper.insert(payLog);

        //添加到redis中

        redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);


        //4.清除掉购物车的数据

        redisTemplate.boundHashOps("CART_REDIS_PREFIX").delete(order.getUserId());//删除登录的用户的购物车


    }

    @Override
    public TbPayLog getPayLogByUserId(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateStatus(String out_trade_no, String transaction_id) {
        //1.先获取到支付日志记录数据
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);

        //2.更新 状态
        payLog.setPayTime(new Date());//支付的世界
        payLog.setTransactionId(transaction_id);
        payLog.setTradeState("1");//已支付

        payLogMapper.updateByPrimaryKey(payLog);


        //3.获取到该支付日志对应的商品的订单id
        String orderList = payLog.getOrderList();//  37,38

        String[] split = orderList.split(",");

        for (String orderId : split) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
            //4.对应订单状态进行更新

            tbOrder.setStatus("2");tbOrder.setPaymentTime(new Date());
            tbOrder.setUpdateTime(tbOrder.getPaymentTime());

            orderMapper.updateByPrimaryKey(tbOrder);

        }
        //5.删除掉用户的redis中日志记录
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

    }


}
