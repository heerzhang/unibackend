package org.fjsei.yewu.jpa;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.AbstractJPAQuery;

//替换QuerydslJpaPredicateExecutor功能 ； 目的是去掉findAll分页查询的Count(*)功能；同时cache等hints指示必须保留。


/**
 * Querydsl specific fragment for extending {@link SimpleJpaRepository} with an implementation for implementation for
 * {@link QuerydslPredicateExecutor}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author Jocelyn Ntakpe
 * @author Christoph Strobl
 * @author Jens Schauder
 */


//替换QuerydslJpaPredicateExecutor功能 extends QuerydslJpaPredicateExecutor<T>  QuerydslPredicateExecutor<T>
//public class QuerydslNcExecutorImpl<T> extends QuerydslJpaPredicateExecutor<T>  implements QuerydslPredicateExecutor<T>

public class QuerydslNcExecutorImpl<T> extends QuerydslJpaPredicateExecutor<T>  implements QuerydslNcExecutor<T> {
    //private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityPath<T> path;
    private final Querydsl querydsl;
   // private final EntityManager entityManager;
   // private final CrudMethodMetadata metadata;



    /**
     * Creates a new {@link org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor} from the given domain class and {@link EntityManager} and uses
     * the given {@link EntityPathResolver} to translate the domain class into an {@link EntityPath}.
     *
     * @param entityInformation must not be {@literal null}.
     * @param entityManager must not be {@literal null}.
     * @param resolver must not be {@literal null}.
     * @param metadata maybe {@literal null}.
     */

    public QuerydslNcExecutorImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager,
                                  EntityPathResolver resolver, @Nullable CrudMethodMetadata metadata) {
        super(entityInformation, entityManager, resolver ,metadata);


        this.path = resolver.createPath(entityInformation.getJavaType());
        this.querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
        // this.entityManager = entityManager;
    }


    /* 旧的
    public QuerydslNcExecutorImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager,
                                        EntityPathResolver resolver, @Nullable CrudMethodMetadata metadata) {

        this.entityInformation = entityInformation;
        this.metadata = metadata;
        this.path = resolver.createPath(entityInformation.getJavaType());
        this.querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
        this.entityManager = entityManager;
    }

    */


    /*　findAllNc
     * (non-Javadoc)
     * @see org.springframework.data.querydsl.QuerydslPredicateExecutor#findAll(com.querydsl.core.types.Predicate, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> findAllNc(Predicate predicate, Pageable pageable) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");

        //final JPQLQuery<?> countQuery = createCountQuery(predicate); ( JPQLQuery<?> )
        ////JPQLQuery<?>  jpqlQuery= createQuery(predicate);

        JPQLQuery<T> jpqlQuery=createQuery(predicate).select(path);
        //CustomRepositoryImpl.getMyQueryHints()
        //jpqlQuery1.setHint("","");

        JPQLQuery<T> query = querydsl.applyPagination(pageable, jpqlQuery);

        //return PageableExecutionUtils.getPage(query.fetch(), pageable, null);
        return CustomRepositoryImpl.PageableExecutionUtils.getPage(query.fetch(), pageable, null);
    }

}


