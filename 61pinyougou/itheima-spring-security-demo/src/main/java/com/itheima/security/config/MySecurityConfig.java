package com.itheima.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 配置类 代替 springsecurity.xml
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.security.config *
 * @since 1.0
 */
@EnableWebSecurity// 开启 Security的自动配置
public class MySecurityConfig extends WebSecurityConfigurerAdapter{


    //1.授权
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // ROLE_不要写,自动的进行拼接
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
    }

    //2.认证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
       //自定义配置
            //1. 拦截的请求
            //2. 自定义登录页面


        //1. 拦截的请求
        http.authorizeRequests()
                .antMatchers("/login.html","/error.html").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated();

        //2. 自定义登录页面
        http.formLogin()
                .loginProcessingUrl("/login")
                .loginPage("/login.html")
                .defaultSuccessUrl("/index.jsp",true)
                .failureUrl("/error.html");

        //3.禁用CSRF
        http.csrf().disable();//
    }
}
