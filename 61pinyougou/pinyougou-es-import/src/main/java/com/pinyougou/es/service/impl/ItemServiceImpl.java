package com.pinyougou.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ItemService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.es.service.impl *
 * @since 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private ItemDao itemDao;

    @Override
    public void importDataToEs() {
        //1.查询符合条件的数据库中的item表的中数据
        TbItem condition = new TbItem();
        condition.setStatus("1");//正常的商品
        List<TbItem> itemList = itemMapper.select(condition);
        //2.调用itemDao的方法 保存到ES服务器中


        /**
         *
         * {
         "网络制式":"移动8G" ,
         "机身内存":"540T"
         }
         *
         */
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();// {"机身内存":"16G","网络":"联通3G"}
            Map map = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(map);
        }
        itemDao.saveAll(itemList);
    }
}
