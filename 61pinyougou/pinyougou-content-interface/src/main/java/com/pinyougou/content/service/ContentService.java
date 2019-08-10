package com.pinyougou.content.service;
import java.util.List;
import com.pinyougou.pojo.TbContent;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ContentService extends CoreService<TbContent> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent Content);

	/**
	 * 根据广告分类的ID 获取广告分类下的所有的广告的列表
	 * @param categoryId
	 * @return
	 */
    List<TbContent> findByCategoryId(Long categoryId);
}
