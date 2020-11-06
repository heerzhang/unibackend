package org.fjsei.yewu.jpa;

/*
 * Copyright 2008-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package org.springframework.data.jpa.repository.support;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

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

// extends QuerydslJpaPredicateExecutor<T>  QuerydslPredicateExecutor<T>
//@Repository
public class QuerydslNcPredicateExecutor<T> extends QuerydslJpaPredicateExecutor<T>  implements QuerydslNcExecutor<T> {
  //  private final JpaEntityInformation<T, ?> entityInformation;
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

    public QuerydslNcPredicateExecutor(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager,
                                       EntityPathResolver resolver, @Nullable CrudMethodMetadata metadata) {
        super(entityInformation, entityManager, resolver ,metadata);


        this.path = resolver.createPath(entityInformation.getJavaType());
        this.querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
     //   this.entityManager = entityManager;
    }


    /* 旧的
    public QuerydslNcPredicateExecutor(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager,
                                        EntityPathResolver resolver, @Nullable CrudMethodMetadata metadata) {

        this.entityInformation = entityInformation;
        this.metadata = metadata;
        this.path = resolver.createPath(entityInformation.getJavaType());
        this.querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
        this.entityManager = entityManager;
    }

    */


    /*
     * (non-Javadoc)
     * @see org.springframework.data.querydsl.QuerydslPredicateExecutor#findAll(com.querydsl.core.types.Predicate, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> findAllNc(Predicate predicate, Pageable pageable) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");

        //final JPQLQuery<?> countQuery = createCountQuery(predicate);
        JPQLQuery<T> query = querydsl.applyPagination(pageable, createQuery(predicate).select(path));

        //return PageableExecutionUtils.getPage(query.fetch(), pageable, null);
        return CustomRepositoryImpl.PageableExecutionUtils.getPage(query.fetch(), pageable, null);
    }

}

