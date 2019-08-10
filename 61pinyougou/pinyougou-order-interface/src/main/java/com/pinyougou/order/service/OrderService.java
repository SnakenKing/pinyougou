package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.order.service *
 * @since 1.0
 */
public interface OrderService {
    void add(TbOrder order);

    /**
     * 根据当前的用户的ID 获取 当前用户的支付的日志
     * @param userId
     * @return
     */
    TbPayLog getPayLogByUserId(String userId);

    /**
     *
     * +
     * 更新支付的状态(支付的时间,支付的状态,transaction_id)

     +  删除redis的当前用户的支付日志

     + 更新订单的状态()
     * @param out_trade_no
     * @param transaction_id
     */
    void updateStatus(String out_trade_no, String transaction_id);
}
