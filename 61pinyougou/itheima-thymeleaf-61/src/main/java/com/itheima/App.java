package com.itheima;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileWriter;

/**
 * Hello world!
 */
public class App {
    //通过java代码输出一个静态的页面 显示hello world
    // 数据  +  模板文件 ===output html
    public static void main(String[] args)  throws Exception{
        //1.创建一个模板视图解析器
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        //2.创建一个模板引擎（设置解析器）
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        //3.创上下文（用于设置数据的）
        Context context = new Context();
        context.setVariable("hello","hello world");//rquest.setAttribute()
        //4.创建模板文件（后缀可以是.html 推荐使用html作为模板文件）
        //5.创建输出流 指定生成的文件
        FileWriter writer   = new FileWriter(new File("C:\\Users\\Administrator\\ideaChanggou\\61pinyougou\\itheima-thymeleaf-61\\src\\main\\resources\\output\\1234.html"));
        //6.处理数据到输出文件中
        engine.process("template",context,writer);
        //7.关闭
        writer.close();
    }
}
