package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/cart")
//@CrossOrigin(origins = {"http://localhost:9105"},allowCredentials = "true")
public class CartController {

    @Reference
    private CartService cartService;

    /**
     * 添加购物车
     *
     * @param itemId SKU的ID
     * @param num    购买的数量
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    //@CrossOrigin(origins = {"http://localhost:9105"},allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        //设置跨域的访问的头信息  (允许 9105的系统来访问我的资源)
        /*response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        //同意客户端(9105)携带cookie到服务端
        response.setHeader("Access-Control-Allow-Credentials", "true");*/
        try {
            //1.获取用户
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("登录的用户名:" + name); // anonymousUser
            if ("anonymousUser".equals(name)) {//匿名用户
                //2.判断用户是否已经登录 如果没有登录 操作cookie
                System.out.println("用户没登录");

                //2.1 先获取cookie中的(已有的)购物车
                String cookeCartListString = CookieUtil.getCookieValue(request, "cartList", true);
                List<Cart> cartList = new ArrayList<>();
                if (StringUtils.isNotBlank(cookeCartListString)) {
                    cartList = JSON.parseArray(cookeCartListString, Cart.class);
                }

                //2.2 向[已有的购物车] 中添加商品 返回一个[最新的购物车列表]. (这是一个业务的方法)
                List<Cart> newestList = cartService.addGoodsToCartList(cartList, itemId, num);
                //2.3 将最新的购物车列表存储回cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(newestList), 7 * 24 * 3600, true);

            } else {
                //3.判断用户是否已经登录 如果 登录 操作redis
                System.out.println("用户登录");

                //3.1 先获取redis中的(已有的)购物车
                List<Cart> cartList = cartService.getCartFromRedis(name);

                //3.2 向[已有的购物车] 中添加商品 返回一个[最新的购物车列表]. (这是一个业务的方法)
                List<Cart> newestList = cartService.addGoodsToCartList(cartList, itemId, num);

                //3.3 将最新的购物车列表存储回redis中

                cartService.saveCartToRedis(name, newestList);//key value

            }
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }

    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        //1.先获取到用户的信息
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(name)) {//匿名用户
            //2.判断用户是否已经登录 如果没有登录 从cookie获取购物的数据
            String cookeCartListString = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cartList = new ArrayList<>();
            if (StringUtils.isNotBlank(cookeCartListString)) {
                cartList = JSON.parseArray(cookeCartListString, Cart.class);
            }

            return cartList;
        } else {

            //3.判断用户是否已经登录 如果登录 从redis获取购物的数据
            List<Cart> redisCartList = cartService.getCartFromRedis(name);

            // 先获取到cookie中的购物车数据
            String cookeCartListString = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cartcookieList = new ArrayList<>();
            if (StringUtils.isNotBlank(cookeCartListString)) {
                cartcookieList = JSON.parseArray(cookeCartListString, Cart.class);
            }

            // 再获取到redis中的购物车数据



            // 将cookie中的购物车数据合并到redis中的购物车对象 中 返回最新的购物车列表
            List<Cart> newestList = cartService.merge(cartcookieList,redisCartList);


            //将最新的购物车数据存储回 redis中
            cartService.saveCartToRedis(name,newestList);

            //cookie清空

            CookieUtil.deleteCookie(request,response,"cartList");


            return newestList;

        }

    }


}
