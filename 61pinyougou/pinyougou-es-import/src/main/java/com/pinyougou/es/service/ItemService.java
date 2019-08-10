package com.pinyougou.es.service;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.es.service *
 * @since 1.0
 */
public interface ItemService {
    /**
     * 用于查询数据库的数据导入到es中
     */
    void importDataToEs();
}
