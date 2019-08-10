package com.itheima.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Map;

/**
 * 搜索商品的POJO
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.model *
 * @since 1.0
 */

/**
 * 1.创建索引
 * 2.创建类型
 * 3.创建文档的时候创建一个文档的唯一标识
 * 4.创建映射(设置字段:是否分词 是否索引 是否存储 数据类型是什么 分词器使用什么)
 *
 * @document
 *
 * @field
 */

@Document(indexName = "pinyougou",type = "item")
public class TbItem implements Serializable {

    /**
     * 商品id，同时也是商品编号
     */

    @Id//文档的唯一标识
    @Field(type = FieldType.Long)//field 类型java的修饰的类型自动创建ES中数据类型
    private Long id;

    /**
     * 商品标题
     */

    @Field(type = FieldType.Text,index = true,searchAnalyzer = "ik_smart",analyzer = "ik_smart",copyTo = "keyword")
    private String title;



    @Field(type =FieldType.Long )
    private Long goodsId;



    /**
     * 冗余字段 存放三级分类名称  关键字 只能按照确切的词来搜索
     */

    @Field(type =FieldType.Keyword,copyTo = "keyword")  //不分词
    private String category;

    //设置为对象域数据类型 查询
    @Field(index = true,type=FieldType.Object)
    private Map<String,String> specMap;

    public Map<String, String> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, String> specMap) {
        this.specMap = specMap;
    }

    /**
     * 冗余字段 存放品牌名称
     */
    @Field(type =FieldType.Keyword,copyTo = "keyword")//不分词的
    private String brand;

    /**
     * 冗余字段，用于存放 商家的店铺名称
     */
    @Field(type =FieldType.Keyword,copyTo = "keyword")
    private String seller;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}
