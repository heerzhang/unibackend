package org.fjsei.yewu.service;


import org.fjsei.yewu.entity.sdn.Student;
import org.fjsei.yewu.entity.sei.Teacher;

import java.util.List;

/*
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:12
 */

//@Transactional
public interface JpaService {

    Student findByName(String name);
    Teacher getTeacher(String name);
    List<Teacher> getAllTeacher();

    public void addTeacher(Teacher topic);
}

