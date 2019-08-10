package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;


    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative() {


        //1获取当前的登录的用户的ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //2.从redis中获取秒杀订单的对象 (本身有金额 和支付的订单号)
        TbSeckillOrder tbSeckillOrder = seckillOrderService.getOrderByUserId(userId);

        if (tbSeckillOrder != null) {
            double v = tbSeckillOrder.getMoney().doubleValue();
            long fen = (long) (v * 100);
            Map resultMap = weixinPayService.createNative(tbSeckillOrder.getId() + "", fen + "");
            return resultMap;
        }
        //4.返回map
        return new HashMap();
    }


    /**
     * 检测支付的状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //调用支付的服务  查询支付的状态
        Map<String, String> resultMap = weixinPayService.queryPayStatus(out_trade_no);


        //支付成功
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            Result result = new Result(true, "支付成功");

            //更新秒杀订单的状态

            seckillOrderService.updateStatus(userId, resultMap.get("transaction_id"));

            return result;
            //支付失败
        } else if (resultMap.get("trade_state").equals("NOTPAY")) {
            Result result = new Result(false, "406");//未支付
            return result;
        } else {
            Result result = new Result(false, "407");//支付失败
            return result;
        }

    }

    @RequestMapping("/deleteOrder")
    public Result deleteOrder() {
        //1获取当前的登录的用户的ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //2. 调用方法(业务 关闭微信的订单 ,)

        TbSeckillOrder seckillOrder = seckillOrderService.getOrderByUserId(userId);
        String out_trade_no = seckillOrder.getId() + "";

        Map resultMap = weixinPayService.closePay(out_trade_no);

        //3.恢复库存
        if (resultMap.get("result_code").equals("SUCCESS")) {
            seckillOrderService.deleteOrder(userId);
            return new Result(true, "关闭成功");
        } else if ("ORDERCLOSED".equals(resultMap.get("err_code"))) {

            //已经关闭了
            return new Result(true, "关闭成功");
        } else if (resultMap.get("err_code").equals("ORDERPAID")) {//已经支付
            Map<String, String> resultMap1 = weixinPayService.queryPayStatus(out_trade_no);
            seckillOrderService.updateStatus(userId, resultMap1.get("transaction_id"));
            return new Result(true, "已经支付");
        } else {
            //.....不处理了.
            //
        }

        return new Result(true, "发生异常");


    }


}
