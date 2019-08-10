package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService orderService;

    /**
     * 下单
     *
     * @param id 秒杀商品的ID
     * @return
     */
    @RequestMapping("/submitOrder")
    public Result submitOrder(Long id) {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(userId)) {
                return new Result(false, "403");//代表没登录
            }
            orderService.submitOrder(userId, id);
            return new Result(true, "排队中,请稍等");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "下单失败");
        }
    }

    @RequestMapping("/queryOrderStatus")
    public Result queryOrderStatus() {

        //1.获取当前用户的ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userId)) {
            return new Result(false, "403");//代表没登录
        }

        //2.根据当前的用户的ID 获取该用户的预订单数据
        TbSeckillOrder order = orderService.getOrderByUserId(userId);


        //3.判断并返回result
        if (order != null) {
            return new Result(true, "下单成功");
        } else {
            return new Result(false, "排队中....");
        }


    }
}
