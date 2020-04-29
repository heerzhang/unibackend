package org.fjsei.yewu.service.core;


import md.specialEqp.EQP;
import org.fjsei.yewu.repository.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:12
 */

@Transactional
public interface RestService {

    EQP findByName(String name);
    Teacher getTeacher(String name);
    List<Teacher> getAllTeacher();

    public void addTeacher(Teacher topic);

    Page<EQP> findByName_Page(String name);
    Page<EQP> findByName_Example(String name);
}

