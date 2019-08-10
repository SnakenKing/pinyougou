package com.itheima.pojo;

import java.io.Serializable;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.pojo *
 * @since 1.0
 */
public class Person implements Serializable {
    private Long id;
    private String name;

    public Person() {

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

    public Person(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
