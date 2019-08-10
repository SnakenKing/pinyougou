package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List; /**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service *
 * @since 1.0
 */
public interface CartService {
    /**
     * 向已有的购物车中添加一个商品
     * @param cartList 已有的购物车
     * @param itemId  商品的ID
     * @param num  商品的购买的数量
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据用户名获取该用户的购物车的列表
     * @param name
     * @return
     */
    List<Cart> getCartFromRedis(String name);

    /**
     * 根据用户 存储该用户的购物车的列表
     * @param name
     * @param newestList
     */
    void saveCartToRedis(String name, List<Cart> newestList);


    /**
     * 合并购物车
     * @param cartcookieList cookie中的购物车
     * @param redisCartList redis总的购物车
     * @return
     */
    List<Cart> merge(List<Cart> cartcookieList, List<Cart> redisCartList);
}
