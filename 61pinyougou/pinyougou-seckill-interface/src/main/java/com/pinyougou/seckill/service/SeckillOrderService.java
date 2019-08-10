package com.pinyougou.seckill.service;
import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService extends CoreService<TbSeckillOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder SeckillOrder);

	/**
	 * 秒杀下单
	 * @param userId
	 * @param id
	 */
	void submitOrder(String userId, Long id);

	/**
	 * 根据用户的ID 获取该用户在redis中的预订单
	 * @param userId
	 * @return
	 */
    TbSeckillOrder getOrderByUserId(String userId);


    void updateStatus(String userId, String transaction_id);

	/**
	 * 1.恢复库
	 * 2.删除预订单
	 * @param userId
	 */
	void deleteOrder(String userId);
}
