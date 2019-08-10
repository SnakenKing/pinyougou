package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbSpecification;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.Specification;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService extends CoreService<TbSpecification> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification Specification);

	/**
	 * 添加规格 和规格选项数据
	 * @param specification 组合对象
	 */
	public void add(Specification specification);
	
}
