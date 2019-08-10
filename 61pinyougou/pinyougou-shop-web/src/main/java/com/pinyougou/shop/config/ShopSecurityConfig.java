package com.pinyougou.shop.config;

import com.pinyougou.shop.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.config *
 * @since 1.0
 */
@EnableWebSecurity//开启自动配置
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //在内存中的用户和密码
//        auth.inMemoryAuthentication().withUser("seller").password("{noop}123456").roles("SELLER");
        //使用自定义认证类的方式进行认证  设置加密器
      auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //1.拦截请求
        http.authorizeRequests()
                .antMatchers("/css/**",
                        "/js/**",
                        "/img/**",
                        "/plugins/**",
                        "/*.html","/seller/add.shtml").permitAll()
                .antMatchers("/**").hasRole("SELLER");
        //2.自定义登录页面
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/shoplogin.html?error");

        //3.设置禁用 csrf
        http.csrf().disable();

        //4.设置iframe 为同源访问策略
        http.headers().frameOptions().sameOrigin();
    }
}
