package com.pinyougou.seckill.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbSeckillGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService extends CoreService<TbSeckillGoods> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize, TbSeckillGoods SeckillGoods);

	/**
	 * 获取所有的秒杀的商品列表
	 * @return
	 */
	List<TbSeckillGoods> findAllFromRedis();

	/**
	 * 从redis中获取该商品的库存和倒计时数据
	 * @param id
	 * @return
	 */
    Map getGoodsById(Long id);
}
