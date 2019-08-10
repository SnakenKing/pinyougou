package com.pinyougou;

import com.itheima.es.dao.ItemDao;
import com.itheima.model.TbItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou *
 * @since 1.0
 */
@ContextConfiguration("classpath:spring-es.xml")
@RunWith(SpringRunner.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemDao itemDao;


    //创建索引 创建映射
    @Test
    public void putMapping() {
        elasticsearchTemplate.createIndex(TbItem.class);
        elasticsearchTemplate.putMapping(TbItem.class);
    }

    //创建文档

    @Test
    public void addDocument() {
        for (long i = 0; i < 100; i++) {
            TbItem item = new TbItem();
            item.setId(i);
            item.setTitle("华为p300" + i);
            item.setCategory("手机");
            item.setBrand("华为");
            item.setSeller("华为旗舰店");
            item.setGoodsId(i);
            Map<String, String> map = new HashMap<>();
            map.put("网络制式", "联通4G");
            map.put("机身内存", "16G");
            item.setSpecMap(map);
            itemDao.save(item);
        }
    }

    @Test
    public void deleteById() {
        itemDao.deleteById(1000L);
    }

    @Test
    public void findById() {
        Optional<TbItem> optional = itemDao.findById(1000L);
        TbItem item = optional.get();
        System.out.println(item.getTitle());
    }

    @Test
    public void findAll() {
        Iterable<TbItem> all = itemDao.findAll();
        for (TbItem item : all) {
            System.out.println(item.getTitle());
        }
    }

    @Test
    public void findAllPage() {
        //第一个参数 代表当前的页码,但是 0 表示第一页
        Pageable pagealbe = PageRequest.of(0, 10);//当前页码1  每页显示10条
        Page<TbItem> all = itemDao.findAll(pagealbe);

        //获取总记录数
        System.out.println(all.getTotalElements());

        //获取总页数
        System.out.println(all.getTotalPages());

        //获取 当前页的记录

        List<TbItem> content = all.getContent();

        for (TbItem item : content) {
            System.out.println(item.getTitle());
        }
    }

    //查询 采用 elasticsearchTemplate核心类


    //模糊搜索

    // ? 代表任意的字符 占用一个字符空间
    // * 代表任意的字符 可有可无(不占有)

    @Test
    public void query() {
        //1.创建查询的对象 2设置查询的条件
        SearchQuery searchquery = new NativeSearchQuery(QueryBuilders.wildcardQuery("title", "华为?"));


        // 3执行查询

        //参数1  指定查询的对象(封装各种各样的条件)
        //参数2 表示查询的文档对应pojo类型

        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchquery, TbItem.class);

        //4.获取结果
        System.out.println("总个记录数为:" + tbItems.getTotalElements());

        System.out.println("总页数" + tbItems.getTotalPages());

        List<TbItem> content = tbItems.getContent();

        for (TbItem item : content) {
            //5.打印
            System.out.println(item.getTitle());

        }


    }

    //分词匹配

    //先分词 再查询  合并结果 返回
    @Test
    public void matchQuery() {

        //1.创建查询对象 设置查询的条件(分词匹配查询)

        //第一个参数 指定从哪一个字段搜索
        //第二个参数 搜索的值是什么
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("title", "华为啊啊啊啊啊啊啊"));

        //2.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);

        //4.获取结果
        System.out.println("总个记录数为:" + tbItems.getTotalElements());

        System.out.println("总页数" + tbItems.getTotalPages());

        List<TbItem> content = tbItems.getContent();

        for (TbItem item : content) {
            //5.打印
            System.out.println(item.getTitle());

        }


    }


    @Test
    public void zuheyuQuery() {
        //1.创建查询对象 设置查询的条件(分词匹配查询)

        //第一个参数 指定从哪一个字段搜索
        //第二个参数 搜索的值是什么
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("keyword", "华为"));

        //2.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);

        //4.获取结果
        System.out.println("总个记录数为:" + tbItems.getTotalElements());

        System.out.println("总页数" + tbItems.getTotalPages());

        List<TbItem> content = tbItems.getContent();

        for (TbItem item : content) {
            //5.打印
            System.out.println(item.getTitle());
        }
    }


    //过滤查询
    @Test
    public void filterquery() {


        //1.创建查询对象的 构建对象

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //2.设置查询条件 设置索引  类型  设置关键字查询条件
        queryBuilder.withIndices("pinyougou");
        queryBuilder.withTypes("item");

        queryBuilder.withQuery(QueryBuilders.matchQuery("keyword", "华为"));//分词匹配查询


        //3.设置过滤查询条件

        /*{
            "网络制式":"联通4G" ,
                "机身内存":"16G"
        }*/


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();//多条件组合查询

        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络制式.keyword", "联通3G"));//词条 不分词的
        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.机身内存.keyword", "16G"));//词条 不分词的

        queryBuilder.withFilter(boolQueryBuilder);

        //4.构建 查询对象
        NativeSearchQuery searchQuery = queryBuilder.build();

        //5.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);

        //6.获取结果
        System.out.println("总个记录数为:" + tbItems.getTotalElements());

        System.out.println("总页数" + tbItems.getTotalPages());

        List<TbItem> content = tbItems.getContent();

        for (TbItem item : content) {
            //5.打印
            System.out.println(item.getTitle());
        }



    }

}
