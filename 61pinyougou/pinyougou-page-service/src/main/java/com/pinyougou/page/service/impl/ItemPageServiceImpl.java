package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.page.service.impl *
 * @since 1.0
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //注入
    @Value("${PageDir}")
    private String pageDir;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    //数据集 + 模板 =html
    @Override
    public void genItemHtml(Long id) {
        //1.先从数据库根据商品SPU的ID 获取SPU的数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

        //2,获取SPU对应描述数据
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

        //3. 调用私有的方法 使用freemarker来生成静态页  数据集 + 模板 =html
        genHTML("item.ftl", tbGoods, tbGoodsDesc);
    }

    @Override
    public void delete(Long id) {
        try {
            FileUtils.forceDelete(new File(pageDir+id+".html"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //生成静态页
    private void genHTML(String templateName, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        FileWriter writer=null;
        try {
            //1.创建configruation对象

            //2.设置字符编码 和 设置模板文件所在的目录

            Configuration configuration = freeMarkerConfigurer.getConfiguration();


            //3.创建数据集map
            Map model = new HashMap();
            model.put("tbGoods",tbGoods);
            model.put("tbGoodsDesc",tbGoodsDesc);

            //查询spu对应的所有的SKU的列表是数据  输出到HTML中作为JS对象存在

            //select * from tb_item where goods_id =1 and status=1 order by is_default desc;


            Example exmaple = new Example(TbItem.class);
            Example.Criteria criteria = exmaple.createCriteria();
            criteria.andEqualTo("goodsId",tbGoods.getId());
            criteria.andEqualTo("status",1);


            exmaple.setOrderByClause("is_default desc");//排序
            List<TbItem> skuList = itemMapper.selectByExample(exmaple);
            model.put("skuList",skuList);

            //根据商品SPU的里面的商品分类的ID 获取商品分类的值

            TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            model.put("itemCat1",itemCat1.getName());
            model.put("itemCat2",itemCat2.getName());
            model.put("itemCat3",itemCat3.getName());



            //4.加载模板对象 template

            Template template = configuration.getTemplate(templateName);

            //5.创建输出流
            writer = new FileWriter(new File(pageDir+tbGoods.getId()+".html"));

            //6.进行生成静态页的动作
            template.process(model,writer);
            //7.关闭流
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
