package com.pinyougou.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.test *
 * @since 1.0
 */
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class CommonMapperTest {
    @Autowired
    private TbBrandMapper brandMapper;
    @Test
    public void insert(){
        TbBrand brand = new TbBrand();
        brand.setName("测试通用mapper");
        brand.setFirstChar("C");
        brandMapper.insert(brand);
    }
    @Test
    public void update(){
        TbBrand brand = new TbBrand();
        brand.setName("测试通用mapper11111");
        brand.setFirstChar("C");
        brand.setId(45L);
        brandMapper.updateByPrimaryKey(brand);
    }
    @Test
    public void dete(){
        brandMapper.deleteByPrimaryKey(45L);
    }
    @Test
    public void select(){
        //查询 根据主键查询
        TbBrand brand = brandMapper.selectByPrimaryKey(44L);
        System.out.println(brand.getFirstChar());
        //根据 = 号条件查询

        TbBrand recored = new TbBrand();//相当于where
        //recored.setId(44l);//相当于where id = 44
        recored.setName("锤子");//相当于where id = 44 and name ='58'
        List<TbBrand> select = brandMapper.select(recored);
        for (TbBrand tbBrand : select) {
            System.out.println(tbBrand.getFirstChar());
        }

        //根据 各种条件查询 包括等号 > < in
        Example exmaple = new Example(TbBrand.class);// tb_brand where
        Example.Criteria criteria = exmaple.createCriteria();

        //第一个参数指定要指定的条件的属性名
        //第二个参数指定的要查询的值
        criteria.andEqualTo("id",44L); // where id =44
        List<Long> ids =new ArrayList<>();
        ids.add(1L);
        ids.add(44L);
        criteria.andIn("id",ids);// where id = 44 and id in (1,2)





        List<TbBrand> brands = brandMapper.selectByExample(exmaple);

        for (TbBrand tbBrand : brands) {
            System.out.println("aaaaa"+tbBrand.getFirstChar());
        }

    }
    @Test
    public void selectPage(){
        //1.执行开始分页的方法  紧跟着第一个查询才会被分页
        PageHelper.startPage(1,10);
        //2.执行查询
        List<TbBrand> brands11 = brandMapper.selectAll();
        List<TbBrand> brands22 = brandMapper.selectAll();

        //3.获取结果设置分页的对象PageInfo 中
        PageInfo<TbBrand> pageInfo11 = new PageInfo<>(brands11);
        PageInfo<TbBrand> pageInfo22 = new PageInfo<>(brands22);


        System.out.println(brands11.size());
        System.out.println(brands22.size());
        System.out.println("=================");
        System.out.println(pageInfo11.getTotal());
        System.out.println(pageInfo11.getPages());
        System.out.println(pageInfo11.getList());


    }
}
