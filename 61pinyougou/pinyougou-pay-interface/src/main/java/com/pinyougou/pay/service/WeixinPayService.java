package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.pay.service *
 * @since 1.0
 */
public interface WeixinPayService {
    /**
     * 根据品优购产生的订单 和 支付的金额 调用统一下单的API 发送请求 获取支付的连接 转成Map返回
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map createNative(String out_trade_no, String total_fee);

    /**
     * 根据支付的订单号 查询该支付的订单对应的状态  调用微信查询订单的API
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no);


    /**
     * 关闭微信订单
     * @param out_trade_no
     * @return
     */
    Map closePay(String out_trade_no);
}
