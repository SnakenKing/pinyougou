package com.pinyougou.content.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;  





/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl extends CoreServiceImpl<TbContent>  implements ContentService {

	
	private TbContentMapper contentMapper;

	@Autowired
	public ContentServiceImpl(TbContentMapper contentMapper) {
		super(contentMapper, TbContent.class);
		this.contentMapper=contentMapper;
	}

	
	

	
	@Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbContent> all = contentMapper.selectAll();
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent content) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        if(content!=null){			
						if(StringUtils.isNotBlank(content.getTitle())){
				criteria.andLike("title","%"+content.getTitle()+"%");
				//criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(content.getUrl())){
				criteria.andLike("url","%"+content.getUrl()+"%");
				//criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(StringUtils.isNotBlank(content.getPic())){
				criteria.andLike("pic","%"+content.getPic()+"%");
				//criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(StringUtils.isNotBlank(content.getContent())){
				criteria.andLike("content","%"+content.getContent()+"%");
				//criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(StringUtils.isNotBlank(content.getStatus())){
				criteria.andLike("status","%"+content.getStatus()+"%");
				//criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
        List<TbContent> all = contentMapper.selectByExample(example);
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {


		//hget bigkey field1


		//1.从redis中查询 分类对应的广告列表数据  如果 有 直接返回  如果没有 查询数据库
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("CONTENT_REDIS").get(categoryId);
		if(contentList!=null && contentList.size()>0){
			System.out.println("有缓存");
			return contentList;
		}
		//select * from tb-content where category_id =1
		TbContent condition = new TbContent();
		condition.setCategoryId(categoryId);
		List<TbContent> select = contentMapper.select(condition);

		// 2. 将查询到数据写入redis中

		//hset bigkey  field1 value1

		redisTemplate.boundHashOps("CONTENT_REDIS").put(categoryId,select);
		System.out.println("没缓存");

		return select;

	}

}
