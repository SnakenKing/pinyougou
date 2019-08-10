package com.itheima.thymeleaf.pojo;

import java.io.Serializable;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.thymeleaf.pojo *
 * @since 1.0
 */
public class User implements Serializable {
    private Long id;
    private String name;

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
