package org.fjsei.yewu.entity.incp;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


//可根据接口函数名字自动生成HQL的模式。

public interface JcBookRepository extends CrudRepository<JcBook, Long> {
    List<JcBook> findByAuthor(JcAuthor name);
}

