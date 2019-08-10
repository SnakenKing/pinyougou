package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.manager.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/getName")
    public String getName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return name;
    }
}
