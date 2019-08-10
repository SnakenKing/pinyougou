package com.pinyougou.manager.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.manager.config *
 * @since 1.0
 */
@EnableWebSecurity//开启自动的配置
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //1.拦截请求
        //2.自定义登录页面
        //请求的拦截
        http.authorizeRequests()
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/login.html").permitAll()
                .antMatchers("/**").hasRole("ADMIN");


        //自定义登录页面
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login.html?error");

        //csrf禁用
        http.csrf().disable();

        //设置 iframe 选项 为同源访问策略
        http.headers().frameOptions().sameOrigin();

        //退出的配置项
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

    }
}
