package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemDao;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service.impl *
 * @since 1.0
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemDao itemDao;

    //根据关键字  和过滤的条件执行分词匹配  和 过滤查询
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        Map<String, Object> resultMap = new HashMap<>();

        //1.获取传递过来的关键字
        String keywords = (String) searchMap.get("keywords");
        //2.创建一个查询对象的 构建对象
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        //3.设置查询的条件 可以不设置,默认从所有的索引中所有的类型中查询
        //searchQueryBuilder.withIndices("pinyougou");
        //searchQueryBuilder.withTypes("item");
        //searchQueryBuilder.withQuery(QueryBuilders.matchQuery("keyword", keywords));//主查询
        //参数1 指定要查询的关键字
        //参数2 指定要从哪一些字段上进行搜索
        searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords, "title", "brand", "seller", "category"));//主查询
        //3.1 设置聚合查询 (  terms 设置类别 category_grou设置别名  field("category") 设置聚合的字段)
        //size 设置分组查询之后的结果的条数
        searchQueryBuilder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));


        //3.2 设置高亮字段Title  设置高亮的 前缀 和后缀

        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title"));
        searchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));

        //4.设置过滤 //TODO

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //4.1 过滤查询  商品分类的过滤查询

        String category = (String) searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            //商品分类的过滤查询
            boolQueryBuilder.filter(QueryBuilders.termQuery("category", category));
        }
        //4.2 过滤查询  品牌的过滤查询
        String brand = (String) searchMap.get("brand");

        if (StringUtils.isNotBlank(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand", brand));
        }

        //4.3 过滤查询  规格的过滤查询
        Map<String, String> spec = (Map<String, String>) searchMap.get("spec");// spec:{"网络":"移动3G"}

        if (spec != null) {
            for (String key : spec.keySet()) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key + ".keyword", spec.get(key)));
            }
        }

        //4.4 过滤查询  价格的范围的过滤查询
        String price = (String) searchMap.get("price");//    0-500    3000-*

        if (StringUtils.isNotBlank(price)) {
            String[] split = price.split("-");

            if (!split[1].equals("*")) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
            } else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }
        }


        searchQueryBuilder.withFilter(boolQueryBuilder);

        //5.构建查询对象
        NativeSearchQuery searchQuery = searchQueryBuilder.build();


        //5.1 设置分页的条件

        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 40;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        searchQuery.setPageable(pageable);

        //5.2 添加排序  设置排序的字段(price )  和 排序的类型(DESC/ASC)
        String sortField = (String) searchMap.get("sortField");//price
        String sortType = (String) searchMap.get("sortType");//DESC

        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
            if (sortType.equals("DESC")) {
                searchQuery.addSort(new Sort(Sort.Direction.DESC, sortField));
            } else if (sortType.equals("ASC")) {
                searchQuery.addSort(new Sort(Sort.Direction.ASC, sortField));
            } else {
                //不排序
            }
        }


        //6.执行查询 ()
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {

                //1.获取当前页的记录集合

                List<T> content = new ArrayList<>();
                //2.获取分页的对象
                //3.获取结果集
                SearchHits hits = response.getHits();

                if (hits == null || hits.totalHits <= 0) {
                    return new AggregatedPageImpl<T>(content);
                }
                for (SearchHit hit : hits) {//每条一条记录
                    String sourceAsString = hit.getSourceAsString();//json字符串
                    TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);

                    //获取高亮的数据
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    //获取高亮字段为title的高亮的数据对象
                    HighlightField highlightField = highlightFields.get("title");
                    if (highlightField != null) {

                        Text[] fragments = highlightField.getFragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text fragment : fragments) {
                            stringBuffer.append(fragment.string());//高亮的数据
                        }
                        tbItem.setTitle(stringBuffer.toString());
                    }

                    content.add((T) tbItem);
                }
                return new AggregatedPageImpl<T>(content, pageable, hits.getTotalHits(), response.getAggregations(), response.getScrollId());
            }
        });

        //7.获取结果
        long totalElements = tbItems.getTotalElements();

        int totalPages = tbItems.getTotalPages();

        List<TbItem> content = tbItems.getContent();


        //7 获取指定分组名的分组查询的结果
        Aggregation category_group = tbItems.getAggregation("category_group");

        StringTerms stringTerms = (StringTerms) category_group;
        System.out.println(stringTerms);


        List<String> categoryList = new ArrayList<>();
        if (stringTerms != null) {

            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();

            for (StringTerms.Bucket bucket : buckets) {
                String keyAsString = bucket.getKeyAsString();//商品的分类的名称
                categoryList.add(keyAsString);
            }
        }


        //8 获取商品分类的对应的规格的列表 和品牌的列表 默认获取商品分类列表的的第一个元素对应的数据["手机","平板电视"]


        //如果 页面传递了商品分类 找该商品分类下的所有的品牌列表 和规格列表
        if (StringUtils.isNotBlank(category)) {
            Map map = searchBrandAndSpecList(category);// map  brandList [],  specList  []
            resultMap.putAll(map);//copy
        } else {
            if (categoryList != null && categoryList.size() > 0) {
                Map map = searchBrandAndSpecList(categoryList.get(0));// map  brandList [],  specList  []
                resultMap.putAll(map);//copy
            } else {
                //没有数据
                resultMap.put("brandList", new HashMap<>());
                resultMap.put("specList", new HashMap<>());
            }
        }


        //9.封装返回


        resultMap.put("total", totalElements);//总记录数
        resultMap.put("totalPages", totalPages);//总页数
        resultMap.put("rows", content);//当前页的记录
        resultMap.put("categoryList", categoryList);//商品分类的列表
       /*resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);*/
        return resultMap;
    }

    @Override
    public void updateIndex(List<TbItem> itemList) {
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();//{"网络":"移动","机身内存":"4G"}
            tbItem.setSpecMap(JSON.parseObject(spec,Map.class));
        }

        itemDao.saveAll(itemList);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        //delete from tb_item where goods_id in (12,3)
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termsQuery("goodsId",ids));
        elasticsearchTemplate.delete(deleteQuery, TbItem.class);
    }


    @Autowired
    private RedisTemplate redisTemplate;


    //根据分类名 从redis中获取品牌列表和规格的列表数据 返回
    private Map searchBrandAndSpecList(String category) {

        //1.先根据分类名获取模板的ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //2.根据模板的ID 获取品牌列表
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        //3.根据模板的ID 获取规格的列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        //4.封装 返回
        Map map = new HashMap();
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }


}
