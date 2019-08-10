package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的认证类
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.service *
 * @since 1.0
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    /**
     *
     * @param username 页面传递过来的用户名
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.根据用户名获取数据中的数据 商家对象
        TbSeller tbSeller = sellerService.findOne(username);
        if(tbSeller==null){
            return null;
        }

        //判断是否已经被审核了,如果没有被审核 返回null

        if(!"1".equals(tbSeller.getStatus())){
            System.out.println("你还没有被审核===============");
            return null;
        }

        //2.获取数据库中商家对象有密码 获取密码
        String password = tbSeller.getPassword();

        //3.授权用户一个角色
        System.out.println("==================呵呵呵=================="+username);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));//授权角色

        return new User(username,password,list);//根据页面中的密码和数据库中查询的密码进行匹配,如果匹配就返回OK
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode1 = encoder.encode("123456");
        String encode2 = encoder.encode("123456");
        System.out.println(encode1);
        System.out.println(encode2);
    }
}
