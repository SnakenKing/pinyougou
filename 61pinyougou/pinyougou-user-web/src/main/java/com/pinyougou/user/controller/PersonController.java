package com.pinyougou.user.controller;

import com.pinyougou.user.pojo.Person;
import entity.Error;
import entity.Result;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.user.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/person")
public class PersonController {

    /**
     * @Valid 修饰pojo 带有校验的注解 生效校验
     * BindingResult result 存储错误信息的类
     * @param person
     * @param result
     * @return
     */
    @RequestMapping("/add")
    public Result add(@Valid @RequestBody Person person, BindingResult result){

        if(result.hasFieldErrors()){
            Result result1 = new Result(false,"有错");

            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                result1.getErrors().add(new Error(fieldError.getField(),fieldError.getDefaultMessage()));
            }

            //有错误
            return result1;
        }

        //添加用户的信息
        return new Result(true,"成功");
    }
}
