package org.fjsei.yewu.jpa;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;


//@NoRepositoryBean指明当前接口不是我们领域类的接口（如PersonRepository）

@NoRepositoryBean
public interface QuerydslNcExecutor<T> extends QuerydslPredicateExecutor<T> {
    //findAllNc

    Page<T> findAll(Predicate predicate, Pageable pageable);

}
