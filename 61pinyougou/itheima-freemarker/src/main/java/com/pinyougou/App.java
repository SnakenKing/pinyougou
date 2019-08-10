package com.pinyougou;

import com.itheima.pojo.Person;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    // 模板     +   数据集   = html
    public static void main(String[] args) throws Exception {
        //1.创建一个configuration 对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //2.设置模板的字符编码 设置模板的所在的 [目录]
        configuration.setDefaultEncoding("utf-8");
        configuration.setDirectoryForTemplateLoading(new File("C:\\Users\\Administrator\\ideaChanggou\\61pinyougou\\itheima-freemarker\\src\\main\\resources\\template"));

        //3.创建一个模板文件  官方推荐以.ftl结尾

        //4.加载模板文件 参数 是相对路径
        Template template = configuration.getTemplate("template.ftl");

        //5.创建数据集(map)

        Map model = new HashMap();
        model.put("name","world");//类似于rquest.setAttribute(key,value);

        //设置集合数据
        List<Person> list = new ArrayList<>();
        list.add(new Person(1000L,"周杰伦"));
        list.add(new Person(1001L,"大表哥"));
        list.add(new Person(1002L,"蔡徐坤"));
        model.put("list",list);

        model.put("date",new Date());

        model.put("keynull","世上本有路的,走的人多了就没路了");




        //6.创建输出流对象静态的页文件
        FileWriter writer = new FileWriter(new File("C:\\Users\\Administrator\\ideaChanggou\\61pinyougou\\itheima-freemarker\\src\\main\\resources\\output\\12345.html"));

        //7.执行生成静态页的动作

        template.process(model,writer);

        //8.流关闭
        writer.close();

    }
}
