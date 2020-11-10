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
    //@Override
    @QueryHints(value ={ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value ="true") } )
    Page<T> findAllNc(Predicate predicate, Pageable pageable);

}


