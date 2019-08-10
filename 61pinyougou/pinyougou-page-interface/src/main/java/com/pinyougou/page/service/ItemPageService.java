package com.pinyougou.page.service;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.page.service *
 * @since 1.0
 */
public interface ItemPageService {
    /**
     * 根据商品SPU的ID 从数据库中获取商品的数据 生成静态页
     * @param id
     */
    void genItemHtml(Long id);

    void delete(Long id);


}
