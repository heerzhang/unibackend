package org.fjsei.yewu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface TeacherDao extends JpaRepository<Teacher,Integer> {
    Teacher findByName(String name);
}



