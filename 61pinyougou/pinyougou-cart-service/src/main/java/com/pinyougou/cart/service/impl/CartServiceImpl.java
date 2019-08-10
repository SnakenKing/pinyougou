package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service.impl *
 * @since 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.先根据itemId 从数据库中获取商品的数据
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);

        //2.获取商家的ID
        String sellerId = tbItem.getSellerId();

        Cart cart = searchBySellerId(cartList, sellerId);

        if (cart == null) {
            //3.判断商家的ID 是否在购物车列表中存在 如果 不存在 直接添加
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());

            List<TbOrderItem> orderItemList = new ArrayList<>();//存储商品的列表

            TbOrderItem orderItem = new TbOrderItem();//购物车中明细列表中的要添加的商品对象
            orderItem.setItemId(itemId);
            orderItem.setGoodsId(tbItem.getGoodsId());
            orderItem.setTitle(tbItem.getTitle());
            orderItem.setPrice(tbItem.getPrice());
            orderItem.setNum(num);//数量
            double v = tbItem.getPrice().doubleValue() * num;
            orderItem.setTotalFee(new BigDecimal(v));//小计
            orderItem.setPicPath(tbItem.getImage());//图片
            orderItem.setSellerId(sellerId);//商品所属的商家的ID

            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);

            cartList.add(cart);
        } else {
            //4.判断商家的ID 是否在购物车列表中存在 如果 存在   再判断 买的商品是否在 明细列表中存在

            TbOrderItem orderItem = searchOrderItemById(cart.getOrderItemList(), itemId);

            if (orderItem == null) {
                //4.1 如果 不存在  直接添加
                orderItem = new TbOrderItem();

                orderItem.setItemId(itemId);
                orderItem.setGoodsId(tbItem.getGoodsId());
                orderItem.setTitle(tbItem.getTitle());
                orderItem.setPrice(tbItem.getPrice());
                orderItem.setNum(num);//数量
                double v = tbItem.getPrice().doubleValue() * num;
                orderItem.setTotalFee(new BigDecimal(v));//小计
                orderItem.setPicPath(tbItem.getImage());//图片
                orderItem.setSellerId(sellerId);//商品所属的商家的ID


                cart.getOrderItemList().add(orderItem);

            } else {
                //4.2 如果 存在   数量相加

                orderItem.setNum(orderItem.getNum() + num);
                double v = orderItem.getPrice().doubleValue() * orderItem.getNum();//小计
                orderItem.setTotalFee(new BigDecimal(v));//小计

                //判断 如果 Num <=0 删除商品
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);//[]
                }

                //判断如果没在商家买东西 删除商家
                if(cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }

            }
        }

        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> getCartFromRedis(String name) {

        List<Cart> redisCartList = (List<Cart>) redisTemplate.boundHashOps("CART_REDIS_PREFIX").get(name);
        if (redisCartList == null) {
            redisCartList = new ArrayList<>();
        }
        return redisCartList;
    }

    @Override
    public void saveCartToRedis(String name, List<Cart> newestList) {
        //1.加入依赖  2. 配置 3.注入redisTemplate
        redisTemplate.boundHashOps("CART_REDIS_PREFIX").put(name, newestList);// bigkey  field  value
    }

    @Override
    public List<Cart> merge(List<Cart> cartcookieList, List<Cart> redisCartList) {

        //1.循环遍历cookie的购物车列表
        for (Cart cart : cartcookieList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //2.向已有的购物车添加一个商品
                redisCartList = addGoodsToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }

    /**
     * 从商家的明细列表中获取商品
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemById(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 从已有的购物车列表中 查询 商家的ID 对应的对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
