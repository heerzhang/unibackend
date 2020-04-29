package org.fjsei.yewu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:02
 **/

//hez 2
@Repository
@Transactional
public interface TeacherDao extends JpaRepository<Teacher,Integer> {

    Teacher findByName(String name);
    /*Teacher getTeacher(String name) {
        getOne(new Integer(1));
    } */
}



