package com.pinyougou.test;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
//import com.pinyougou.pojo.TbBrandExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 逆向工程 只能针对单表的操作。
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.test *
 * @since 1.0
 */
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisTest {

    @Autowired
    private TbBrandMapper brandMapper;


    /*@Test
    public void findAll(){
        List<TbBrand> all = brandMapper.findAll();
        for (TbBrand brand : all) {
            System.out.println(brand.getName()+":"+brand.getId()+";"+brand.getFirstChar());
        }
    }*/

    @Test
    public void insert() {
        TbBrand tbbrand = new TbBrand();
        tbbrand.setName("黑马61");
        tbbrand.setFirstChar("H");
        brandMapper.insert(tbbrand);
    }

    @Test
    public void update() {
        //更新后的数据
        TbBrand tbbrand = new TbBrand();
        tbbrand.setId(45L);

        tbbrand.setName("黑马6111111232121");
//        tbbrand.setFirstChar("H");
        brandMapper.updateByPrimaryKeySelective(tbbrand);
    }

    @Test
    public void delete() {
        brandMapper.deleteByPrimaryKey(45L);
    }

   /* @Test
    public void select() {
        //根据主键查询
        TbBrand brand = brandMapper.selectByPrimaryKey(44l);
        System.out.println(brand.getName());
        //根据 条件查询

        //select * from tb_brand where name="联想"
        TbBrandExample exmaple = new TbBrandExample();



        TbBrandExample.Criteria criteria = exmaple.createCriteria();

        //select * from tb_brand where name="联想"
        criteria.andNameEqualTo("联想xxxx");
        //select * from tb_brand where name="联想" and id = 1
        criteria.andIdEqualTo(1L);


        List<TbBrand> brands = brandMapper.selectByExample(exmaple);
        System.out.println(brands.size());

    }*/

}
