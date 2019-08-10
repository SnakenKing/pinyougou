package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
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
    private OrderService orderService;//订单的服务

    @RequestMapping("/createNative")
    public Map createNative() {


        //1获取当前的登录的用户的ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //2从redis中获取当前用户的ID 对应的日志记录
        TbPayLog payLog = orderService.getPayLogByUserId(userId);

        //3.获取日志记录中的支付订单号 和支付的总金额



        //1.生成支付的订单号

        //long out_trade_no = new IdWorker(0, 0).nextId();

        //2.要有支付的金额
        //String total_fee = "1";//一分钱

        //3.调用支付服务的方法 (方法内部 采用发送请求给支付系统)

        if(payLog!=null) {
            Map resultMap = weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
            return resultMap;
        }
        //4.返回map
        return new HashMap();
    }



    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        try {
            //1.创建一个reuslt对象
            Result result = new Result(false, "支付失败");

            //2.循环调用 支付服务 (不停的查询支付订单的状态)
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                //如果超过5分钟就是超时了.
                if (count >= 100) {
                    result = new Result(false, "支付超时");
                    break;
                }

                Map<String, String> resultMap = weixinPayService.queryPayStatus(out_trade_no);


                //支付成功
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    result = new Result(true, "支付成功");
                    //1.更新支付的状态(支付的时间,支付的状态,transaction_id) 2.删除redis的记录  3. 更新商品订单的状态

                    orderService.updateStatus(out_trade_no,resultMap.get("transaction_id"));
                    break;
                }

               /* if(resultMap.get("trade_state").equals("CLOSED")){
                    //支付失败
                    break;

                }*/

            }
            //3.判断 支付的状态 返回result对象

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "支付失败");
        }


    }


}
