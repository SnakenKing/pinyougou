package com.pinyougou;

import com.pinyougou.es.service.ItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        //1.初始化spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-es.xml");
        //2.获取到spring容器中类实例
        ItemService itemService = context.getBean(ItemService.class);
        //3.调用类实例中的方法(业务逻辑:查询数据库 导入数据到ES服务器中)
        itemService.importDataToEs();

    }
}
