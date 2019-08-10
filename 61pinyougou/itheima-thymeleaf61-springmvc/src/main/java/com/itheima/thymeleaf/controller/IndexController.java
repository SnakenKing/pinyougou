package com.itheima.thymeleaf.controller;

import com.itheima.thymeleaf.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.thymeleaf.controller *
 * @since 1.0
 */
@Controller
@RequestMapping("/test")
public class IndexController {
    @RequestMapping("/index")
    public String show(Model model){
        model.addAttribute("hello","hello world");
        model.addAttribute("user",new User(1L,"蔡徐坤"));
        List<User> list = new ArrayList<>();
        list.add(new User(2L,"大表哥"));
        list.add(new User(3L,"大表哥111"));
        list.add(new User(4L,"小老弟111"));
        model.addAttribute("list",list);
        model.addAttribute("date",new Date());
        return "index";//
    }
}
