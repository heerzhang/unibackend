package org.fjsei.yewu.index.sei;


import md.specialEqp.EQP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EQPIndexRepository extends ElasticsearchRepository<EQP, Long> {
    //１最简单用普通函数名来解析的模式：

    //２类似这条路子：直接操作HQL或者SQL，底下存储引擎支持的原生语言。
    //也可以支持elasticsearch包带入的@Query语句模式：
    @Query("{\"match\": {\"cod\": {\"query\": \"?0\"}}}")
    Page<EQP> findByCod(String cod, Pageable pageable);


    //３Querydsl动态查询方式又是另外一条路，以SpringDataJPA + QueryDSL-JPA　联合用，jpaQueryFactory，QueryDslPredicateExecutor。
    //xxIndexRepository extends  + ,QuerydslPredicateExecutor 两个接口都实现。
    //可是Elasticsearch存储场景　当前 throw new IllegalArgumentException("QueryDsl Support has not been implemented yet.");


    //４更早还用的动态查询模式是：Specification　+　JpaSpecificationExecutor。

    //５最后一种路子：Hibernate提供的CriteriaQuery 。

    //６定做的路子NativeSearchQueryBuilder
}

