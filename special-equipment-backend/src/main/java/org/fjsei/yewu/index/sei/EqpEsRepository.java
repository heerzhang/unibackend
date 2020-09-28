package org.fjsei.yewu.index.sei;


import md.specialEqp.EQP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.util.Streamable;

import java.util.stream.Stream;

//一份数据写入es会产生多份数据(8份/嵌套6份)用于不同查询方式，比原数据占更多磁盘空间。很需要紧凑POJO模型，避免多余字段。
//实体关系也要去范式或变换，专门给ES新建单独的Bean来映射存储。
//@Document()注解的实体类的继承子类也是可以存储给ES的，子类类型字段也都会存储的。缺省所有字段都会存储；
//@Transactional对ElasticsearchRepository没作用，JPA保存异常回滚的时候ES无法自动取消回滚该条数据。
//不建议JpaPersonRepository和MongoDBPersonRepository及ElasticsearchRepository用共同的POJO模型类来做注解，必须各搞各的，合并在同一个Domain Class不好。

public interface EqpEsRepository extends ElasticsearchRepository<EqpEs, Long> {
    //１最简单用普通函数名来解析的模式：

    //２类似这条路子：直接操作HQL或者SQL，底下存储引擎支持的原生语言。
    //也可以支持elasticsearch包带入的@Query(原生DSL语句)模式：?：任意字符 *：0个或任意多个字符
    //Dsl方式POST index/_search发送JSON格式如 "query": {"wildcard": {"shopInfoName.keyword": { "value": "*自营*" } } }
    @Query("{\"match\": {\"cod\": {\"query\": \"?0\"}}}")
    Page<EQP> findByCod(String cod, Pageable pageable);


    //３用Querydsl动态查询方式又是另外一条路，以SpringDataJPA + QueryDSL-JPA　联合用，jpaQueryFactory，QueryDslPredicateExecutor。
    //QuerydslPredicateExecutor<EQP>　+　Predicate；
    //xxIndexRepository extends  + ,QuerydslPredicateExecutor 两个接口都实现。
    //可是Elasticsearch存储场景　当前 throw new IllegalArgumentException("QueryDsl Support has not been implemented yet.");


    //４更早还用的动态查询模式是：Specification　+　JpaSpecificationExecutor。

    //５最后一种路子：Hibernate提供的CriteriaQuery 。

    //６用ES底层API路子 NativeSearchQueryBuilder BoolQueryBuilder., SearchQuery；
    //Elasticsearch有一个滚动API,返回流stream = elasticsearchTemplate.searchForStream(searchQuery；stream.next()。
    //DSL多条件搜索FunctionScoreQueryBuilder SearchQuery ; 例子https://www.cnblogs.com/ysq0908/p/12316858.html
    //用ElasticsearchRestTemplate接口; ElasticsearchOperations.index　或.queryForObject(GetQuery.来直接set/get。
    //SearchQuery multiMatchQuery.must MatchQueryBuilder.operator wildcardQuery rangeQuery .withPageable  Template.queryForList;
    //BoolQueryBuilder.should(QueryBuilders.matchPhraseQuery("name", parm))  '。>'等特殊字符,例子https://elasticsearch.cn/question/5494

    Slice<EqpEs> findByCodLike(String cod, Pageable pageable);
    Streamable<EqpEs> findByCodContaining(String cod);

    Stream<EQP> readAllByCodIsNotNull();
    @Query("select e from EQP e")
    Stream<EQP> streamAllPaged(Pageable pageable);
    //Stream务必要用close()关闭。
}



/*
Keyword类型：用于索引结构化内容（如email，主机名，状态码，邮政码）的字段,通常用于过滤、排序、聚合。
　３种思路；第１种：　定义Nested类型/且不支持@ManyToMany。
 实际开发中我们也可以不使用Object,Nested和Join类型来处理具有关联关系的数据, 也可以第２种：直接把数据库表和ES索引建立一对一关系,多个ES索引,
 然后通过ES查询出数据后, 需要再一次在应用中处理关联关系. 或第３种：直接把具有关联关系的数据表合并建立唯一单个ES索引, 这种处理是最简单的。
Elasticsearch 7.x
　不再需要type。新的索引文档API是PUT {index}/_doc/{id} (需要指定明确的id时)和POST {index}/_doc (自动生成id时)，include_type_name 参数再索引创建、索引模板、映射API中默认值为false。
Elasticsearch 8.x在请求里指定type将不被支持，include_type_name 参数被移除；
@Document(indexName="xx")注解只需要做一次，关联字段派出实体beans就不需要注解@Document(indexName="xx")，都算是存入同一个index内的。
GeoDistanceOrder用于按地理距离对搜索操作的结果进行排序，GeoPoint声明类型；
有基本身份验证和SSL传输的安全Elasticsearch集群支持。
try (Stream<EQP> stream = repository.readAllByCodNotNull()) {
  stream.forEach(…);
}　Stream务必要用close()关闭或使用try-with-resources块来闭
异步操作CompletableFuture<EQP> 和　reactive API不是同门的，完全不能混为一谈。
触发？使用@DomainEvents可以返，每次调用Spring Data存储库中的save(…)方法时，就会调用这些方法。
即使用ES引擎，也能发生深度分页问题。index.max_result_window,默认是10000条数据;超过es报错：拒绝返回结果了。
１ scroll：适合批量导出数据，scroll_id；会占用大量的资源，会生成历史快照。２ search after：推荐用 _uid 作全局唯一值，用业务层 id 也可。
@org.springframework.data.annotation.Transient　针对ElasticsearchRepository生效
@javax.persistence.Transient　针对JpaRepository生效; 这两个注解不一样；
ES不支持事务Elasticsearch does not support ACID transactions. Changes to individual documents are ACIDic, but not changes involving multiple documents.
ES不支持通过外键的复杂的多表关联操作;写入的数据，最快1s中能被检索到。MongoDB (4.0版本+)支持ACID;
MongoDB 4.0 事务功能有一些限制，但事务资源占用超过一定阈值时，会自动 abort 来释放资源。规则限制:
    事务的生命周期不能超过 transactionLifetimeLimitSeconds （默认60s），该配置可在线修改;
    事务修改的文档数不能超过 1000 ，不可修改;
    事务修改产生的 oplog 不能超过 16mb，这个主要是 MongoDB 文档大小的限制， oplog 也是一个普通的文档，也必须遵守这个约束。
MongoDB是非关系型数据库不支持join表关联别指望了，spring-data-mongodb。 支持副本集多文档事务，4.2 版本支持分片集群事务，附加限制如下：
　当一个事务写入多个碎片时，并不是所有外部读取操作都需要等待提交事务的结果在碎片中可见。例如，如果提交了一个事务，而write 1在shard a上可见，而write 2在shard B上尚不可见，则外部read at read关注点“local”可以读取write 1的结果而不看到write 2。
直接用BeanUtils.copyProperties(eQP,eqpEs)　若有字段嵌套对象类型不一样的就会报错。
　JSON.parseObject(JSON.toJSONString(eQP), EqpEs.class)　遇到某字段的属于嵌套对象类型并且该字段对象类型还变化的情况也可顺利转换。
循环关联导致的ES存储死循环+事务死锁。嵌套的JPA关联对象直接发送给Elasticsearch是全量都存储的，所以很耗空间，必须控制和减少字段。
elasticsearch   autocomplete   自动提词   自动补全
*/

