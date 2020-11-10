package org.fjsei.yewu.jpa;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.QueryHint;


//@NoRepositoryBean指明当前接口不是我们领域类的接口（如PersonRepository）

@NoRepositoryBean
public interface QuerydslNcExecutor<T> extends QuerydslPredicateExecutor<T> {

    //不需要做count(*)统计，比缺省的findAll()提高性能百倍。
    Page<T> findAllNc(Predicate predicate, Pageable pageable);
    //无法直接利用findAll这个接口名称的，只能另外自定义findAllNc。

}

