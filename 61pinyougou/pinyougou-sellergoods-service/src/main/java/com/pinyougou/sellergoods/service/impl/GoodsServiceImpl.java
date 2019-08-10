package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.sellergoods.service.GoodsService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods> implements GoodsService {


    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper descMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbBrandMapper tbBrandMapper;


    @Autowired
    public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper, TbGoods.class);
        this.goodsMapper = goodsMapper;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete",false);// where is_delete=0

        if (goods != null) {
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                criteria.andEqualTo("sellerId", goods.getSellerId());//  qiandu123
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
                //criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
                //criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsMarketable())) {
                criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
                //criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
            }
            if (StringUtils.isNotBlank(goods.getCaption())) {
                criteria.andLike("caption", "%" + goods.getCaption() + "%");
                //criteria.andCaptionLike("%"+goods.getCaption()+"%");
            }
            if (StringUtils.isNotBlank(goods.getSmallPic())) {
                criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
                //criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsEnableSpec())) {
                criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
                //criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
            }

        }
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void add(Goods goods) {
        //1.获取SPU的数据 添加数据
        TbGoods goodsGoods = goods.getGoods();
        goodsGoods.setAuditStatus("0");//未审核
        goodsGoods.setIsDelete(false);//
        System.out.println("之前=====" + goodsGoods.getId());
        goodsMapper.insert(goodsGoods);

        System.out.println("之后=====" + goodsGoods.getId());
        //2.获取SPU对应的描述数据  添加数据
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(goodsGoods.getId());//设置主键
        descMapper.insert(goodsDesc);


        //启用规格 有规格数据
        saveItems(goods, goodsGoods, goodsDesc);


    }

    private void saveItems(Goods goods, TbGoods goodsGoods, TbGoodsDesc goodsDesc) {
        if ("1".equals(goodsGoods.getIsEnableSpec())) {
            //3.获取SKU的列表  添加数据
            //TODO
            List<TbItem> itemList = goods.getItemList();
            for (TbItem tbItem : itemList) {

                //设置title spu名 + 规格选项名称  + ....

                String spec = tbItem.getSpec();// {"网络":"移动3G","机身内存":"16G"}
                Map<String, String> map = JSON.parseObject(spec, Map.class);

                String title = goodsGoods.getGoodsName();//
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                tbItem.setTitle(title);


                //设置图片
                String itemImages = goodsDesc.getItemImages();//[{"color":"红色","url":"http://192.168.25.133/group1/M00/00/06/wKgZhV0XQO2AG4iRAANdC6JX9KA321.jpg"}]
                List<Map> maps = JSON.parseArray(itemImages, Map.class);

                tbItem.setImage(maps.get(0).get("url").toString());

                //设置分类的id和名称
                Long category3Id = goodsGoods.getCategory3Id();
                TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(category3Id);
                tbItem.setCategoryid(category3Id);
                tbItem.setCategory(itemCat.getName());

                //设置时间
                tbItem.setCreateTime(new Date());
                tbItem.setUpdateTime(tbItem.getCreateTime());

                tbItem.setGoodsId(goodsGoods.getId());///设置外键

                //设置卖家和店铺名

                TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goodsGoods.getSellerId());

                tbItem.setSellerId(tbSeller.getSellerId());
                tbItem.setSeller(tbSeller.getNickName());//店铺名

                //设置品牌的名称
                TbBrand brand = tbBrandMapper.selectByPrimaryKey(goodsGoods.getBrandId());

                tbItem.setBrand(brand.getName());

                itemMapper.insert(tbItem);
            }
        } else {
            //不启用规格
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goodsGoods.getGoodsName());
            tbItem.setPrice(goodsGoods.getPrice());
            tbItem.setNum(999);

            String itemImages = goodsDesc.getItemImages();//[{"color":"红色","url":"http://192.168.25.133/group1/M00/00/06/wKgZhV0XQO2AG4iRAANdC6JX9KA321.jpg"}]
            List<Map> maps = JSON.parseArray(itemImages, Map.class);

            tbItem.setImage(maps.get(0).get("url").toString());

            //设置分类的id和名称
            Long category3Id = goodsGoods.getCategory3Id();
            TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(category3Id);
            tbItem.setCategoryid(category3Id);
            tbItem.setCategory(itemCat.getName());


            tbItem.setStatus("1");//正常的商品

            //设置时间
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(tbItem.getCreateTime());

            tbItem.setIsDefault("1");//默认显示
            tbItem.setGoodsId(goodsGoods.getId());

            //设置卖家和店铺名

            TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goodsGoods.getSellerId());

            tbItem.setSellerId(tbSeller.getSellerId());
            tbItem.setSeller(tbSeller.getNickName());//店铺名

            //设置品牌的名称
            TbBrand brand = tbBrandMapper.selectByPrimaryKey(goodsGoods.getBrandId());

            tbItem.setBrand(brand.getName());
            tbItem.setSpec("{}");//设置空字符串

            itemMapper.insert(tbItem);
        }
    }

    @Override
    public Goods findOne(Long id) {
        //1.根据SPU的ID 获取SPU的信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //2.根据ID 获取SPU的描述的信息
        TbGoodsDesc tbGoodsDesc = descMapper.selectByPrimaryKey(id);
        //3.获取SPU下的所有的SKU的列表
        //select * from tb_item where goods_id = ?
        TbItem condition = new TbItem();
        condition.setGoodsId(id);
        List<TbItem> itemList = itemMapper.select(condition);

        //4.组合对象放回
        Goods goods = new Goods();
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(itemList);
        return goods;
    }

    @Override
    public void update(Goods goods) {


        //CTR + ALT + M
        //1.更新SPU
        TbGoods goods1 = goods.getGoods();
        goods1.setAuditStatus("0");//设置未审核的状态
        goodsMapper.updateByPrimaryKey(goods1);
        //2.更新SPU的描述
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        descMapper.updateByPrimaryKey(goodsDesc);

        //3.更新SKU的列表    先删除原来的所有的SKU的列表 再进行添加

        //delete from tb_item where goods_id =1
        TbItem condition = new TbItem();
        condition.setGoodsId(goods1.getId());
        itemMapper.delete(condition);
        //再添加
        saveItems(goods,goods1,goodsDesc);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {

       /* for (Long id : ids) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKeySelective(tbGoods);// update tb_goods set audit_status=? where id=?
        }*/

        //update tb_goods set audit_status=? where id in (1,2,3)

        TbGoods tbgoods = new TbGoods();//要更新的值
        tbgoods.setAuditStatus(status); // set audit_status=?

        Example condition = new Example(TbGoods.class);//更新的where条件
        Example.Criteria criteria = condition.createCriteria();
        criteria.andIn("id",Arrays.asList(ids)); // where id in (1,2,3)


        goodsMapper.updateByExampleSelective(tbgoods,condition);
    }

    @Override
    public List<TbItem> findTbItemListByIds(Long[] ids) {

        //select * from tb_item where goods_id in (1,2,3) and status=1


        Example exmaple = new Example(TbItem.class);
        Example.Criteria criteria = exmaple.createCriteria();
        criteria.andIn("goodsId",Arrays.asList(ids)).andEqualTo("status","1");
        List<TbItem> itemList = itemMapper.selectByExample(exmaple);
        return itemList;
    }


    //重写   更新状态的值
    @Override
    public void delete(Object[] ids) {
        //update tb_goods set is_delete=1 where id in (123)
        TbGoods tbgoods = new TbGoods();//要更新的值
        tbgoods.setIsDelete(true);//set is_delete=1

        Example condition = new Example(TbGoods.class);//更新的where条件
        Example.Criteria criteria = condition.createCriteria();
        criteria.andIn("id",Arrays.asList(ids)); // where id in (1,2,3)
        goodsMapper.updateByExampleSelective(tbgoods,condition);
    }
}
