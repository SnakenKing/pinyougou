package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;
import entity.Goods;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);

	/**
	 * 添加商品的数据 包含3个表的数据
	 * @param goods 组合对象
	 */
	void add(Goods goods);

	Goods findOne(Long id);

	void update(Goods goods);

	/**
	 * 批量审核SPU商品
	 * @param ids SPU的ID的数组
	 * @param status 要更新的状态的值
	 */
    void updateStatus(Long[] ids, String status);

	/**
	 * 根据商品的SPU 的ID 获取该spu下的所有的SKU的列表数据
	 * @param ids
	 * @return
	 */
	List<TbItem> findTbItemListByIds(Long[] ids);
}
