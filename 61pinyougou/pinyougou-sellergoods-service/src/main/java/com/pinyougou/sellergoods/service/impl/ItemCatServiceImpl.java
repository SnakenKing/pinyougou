package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;

import com.pinyougou.sellergoods.service.ItemCatService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat> implements ItemCatService {


    private TbItemCatMapper itemCatMapper;

    @Autowired
    public ItemCatServiceImpl(TbItemCatMapper itemCatMapper) {
        super(itemCatMapper, TbItemCat.class);
        this.itemCatMapper = itemCatMapper;
    }


    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbItemCat> all = itemCatMapper.selectAll();
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat itemCat) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (StringUtils.isNotBlank(itemCat.getName())) {
                criteria.andLike("name", "%" + itemCat.getName() + "%");
                //criteria.andNameLike("%"+itemCat.getName()+"%");
            }

        }
        List<TbItemCat> all = itemCatMapper.selectByExample(example);
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        //select * from tb_item_cat where parent_id =?


        //重新查询数据到redis中

        List<TbItemCat> all = findAll();

        for (TbItemCat itemCat : all) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
        }
        TbItemCat condition = new TbItemCat();
        condition.setParentId(parentId);
        return itemCatMapper.select(condition);
    }

}
