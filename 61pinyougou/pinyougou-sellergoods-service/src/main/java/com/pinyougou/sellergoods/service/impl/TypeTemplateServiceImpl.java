package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;  

import com.pinyougou.sellergoods.service.TypeTemplateService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl extends CoreServiceImpl<TbTypeTemplate>  implements TypeTemplateService {

	
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
    private TbSpecificationOptionMapper optionMapper;

	@Autowired
	public TypeTemplateServiceImpl(TbTypeTemplateMapper typeTemplateMapper) {
		super(typeTemplateMapper, TbTypeTemplate.class);
		this.typeTemplateMapper=typeTemplateMapper;
	}

	
	

	
	@Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbTypeTemplate> all = typeTemplateMapper.selectAll();
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	

    @Autowired
    private RedisTemplate redisTemplate;

	 @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate typeTemplate) {
	     //紧跟着第一个查询才会被分页
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if(typeTemplate!=null){			
						if(StringUtils.isNotBlank(typeTemplate.getName())){
				criteria.andLike("name","%"+typeTemplate.getName()+"%");
				//criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getSpecIds())){
				criteria.andLike("specIds","%"+typeTemplate.getSpecIds()+"%");
				//criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getBrandIds())){
				criteria.andLike("brandIds","%"+typeTemplate.getBrandIds()+"%");
				//criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getCustomAttributeItems())){
				criteria.andLike("customAttributeItems","%"+typeTemplate.getCustomAttributeItems()+"%");
				//criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}



        List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);



        //重新查询模板的数据存储到redis中  调用的位置

         List<TbTypeTemplate> templates = findAll();

         for (TbTypeTemplate template : templates) {
             String brandIds = template.getBrandIds();
             //品牌列表的存储
             List<Map> maps = JSON.parseArray(brandIds, Map.class);
             redisTemplate.boundHashOps("brandList").put(template.getId(),maps);

             //规格的列表的存储
             List<Map> specList = findSpecList(template.getId());
             redisTemplate.boundHashOps("specList").put(template.getId(),specList);

         }


         return pageInfo;


    }

    @Override
    public List<Map> findSpecList(Long id) {
	    //1.根据模板的ID 获取模板对象
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        //2.获取规格的列表数据 [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //3.转成JSON数组==List<Map>
        List<Map> maps = JSON.parseArray(specIds, Map.class);

        //4.循环遍历 获取规格的ID  查询规格的下的所有的选项列表  拼接 返回
        for (Map map : maps) {
            //map = {"id":27,"text":"网络"}
            Integer specId = (Integer) map.get("id");

            //select * from tb_specification_option where spec_id = 27
            TbSpecificationOption condition = new TbSpecificationOption();
            condition.setSpecId(Long.valueOf(specId));

            List<TbSpecificationOption> optionList = optionMapper.select(condition);

            map.put("options",optionList);
            //map = [{"id":27,"text":"网络",options:[{id:1,optionName:"移动4G"},{}] },{"id":32,"text":"机身内存"}]
        }

        return maps;
    }

}
