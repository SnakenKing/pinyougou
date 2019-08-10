package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map; /**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service *
 * @since 1.0
 */
public interface ItemSearchService {
    /**
     *  返回的结果 map
     * @param searchMap 搜索的条件
     * @return
     */
    Map<String,Object> search(Map<String, Object> searchMap);

    /**
     * 将更新后的SKU的列表数据 保存到ES中
     * @param itemList
     */
    void updateIndex(List<TbItem> itemList);

    /**
     * 根据SPU 的ID 数组 删除ES中的文档的数据
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
