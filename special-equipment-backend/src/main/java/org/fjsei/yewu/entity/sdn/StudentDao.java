package org.fjsei.yewu.entity.sdn;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:02
 **/
public interface StudentDao extends JpaRepository<Student,Integer> {

    //可根据接口函数名字自动生成HQL的模式。
    Student findByName(String name);


}


