package org.fjsei.yewu.entity.incp;

import java.io.Serializable;

/**
 * @program: spring-boot-example
 * @description: 用户类
 * @author:
 * @create: 2018-05-02 09:59
 **/
public class User implements Serializable{

    private int id;
    private String name;
    private int age;
    private String address;

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", age=" + age +
            ", address='" + address + '\'' +
            '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
