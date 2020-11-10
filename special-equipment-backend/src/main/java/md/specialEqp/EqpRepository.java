package md.specialEqp;


import com.querydsl.core.types.Predicate;
import md.cm.unit.Unit;
import org.fjsei.yewu.jpa.QuerydslNcExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.Nullable;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

//Repository就等于表库：这个Repository实际等价于针对各entity模型做的SQL解析执行的代理类，负责生成跟数据库类型相对应ＳＱＬ。
//对graphQL客户接口的Query操作可使用HINT_CACHEABLE；但是Mutation以及其他的要求严格一致性实时性情况，不能使用这样的函数，要新建独立函数搞。

//interface EqpRepository extends JpaRepository<Eqp, Long>, JpaSpecificationExecutor<Eqp>, QuerydslPredicateExecutor<Eqp>

public interface EqpRepository extends JpaRepository<Eqp, Long>, JpaSpecificationExecutor<Eqp>, QuerydslNcExecutor<Eqp> {

        //函数名字改了，那么后面参数Specification<Eqp> spec就不起作用，这样Specification将没用！
        //@Query("select t from Eqp t")
        //Page<Eqp> fromSpecification(@Nullable Specification<Eqp> spec,Pageable pageable);

        //目的提前join取数据,减少sql语句数,能提高效率。 通过@EntityGraph来指定EQP类中定义的NamedEntityGraph；可被实际证明不好使的，就不用了NamedEntityGraph。
        ///@EntityGraph(value="Eqp.task",type= EntityGraph.EntityGraphType.LOAD)
        /// @Lock(value = LockModeType.PESSIMISTIC_WRITE)       //悲观锁:自动往select from末尾加for update。
        Eqp findByCod(String cod);

        /// @EntityGraph(value="Eqp.task",type= EntityGraph.EntityGraphType.FETCH)
        @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
        List<Eqp>  findAll();
        //EntityGraph用恰当，速度提升快；join会放大记录数，若是查询结果集合记录个数不是很多，不使用@EntityGraph的有可能性会反而更快!。
        //使用@EntityGraph对查询结果集不大的情况没有性能优势，网页显示单次查询结果集都比较小的所以不需要fecth join，该场景关联对象LAZY查询N+1问题引起的性能损失也不大。
        ///@EntityGraph(value="Eqp.isps",type= EntityGraph.EntityGraphType.FETCH)         分页时加了更慢！同时使用了分页和FETCH。
        ///@EntityGraph( type= EntityGraph.EntityGraphType.FETCH,attributePaths={"task","task.isps","isps"} )
        @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
        Page<Eqp> findAll(@Nullable Specification<Eqp> spec, Pageable pageable);

        @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
        Page<Eqp> findAll(Pageable pageable);
        //可定义差异化的查询策略QueryHints。针对同样的HQL也可重复建多函数。
        /* 不同名称的接口函数能够差异化对待：
                @Query("select t from Dict t where t.name = ?1")
                @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
                Dict findDictByName(String name);
        */

        List<Eqp> findByMtU(Unit maintUnit);

        //@QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "false") } )
        Optional<Eqp> findById(Long id);

        @QueryHints(value ={ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value ="true") } )
        long count(@Nullable Specification<Eqp> spec);

        @QueryHints(value ={ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value ="true") } )
        List<Eqp> findAll(@Nullable Specification<Eqp> spec, Sort sort);
        //count(eqp)缓存时间在infinispan-配置文件里<expiration max-idle="180000" lifespan="600000"
        //本来可以无需在这里添加任何接口方法的。　为了二级缓存的提示就需添加注解而加入底下2行代码。
        @QueryHints(value ={ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value ="true") } )
        Page<Eqp> findAllNc(Predicate predicate, Pageable pageable);

        @QueryHints(value ={ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value ="true") },forCounting= true )
        Page<Eqp> findAll(Predicate predicate, Pageable pageable);
        //    Page<Eqp> findAll(Predicate predicate, Pageable pageable);
}



//通过动态EntityGraph触发;EntityGraph与JOIN FETCH是一样？  https://blog.csdn.net/dm_vincent/article/details/53366934
//Spring Data JPA支持JPA2.0的Criteria查询，相应的接口是JpaSpecificationExecutor。  https://www.cnblogs.com/sandea/p/7803731.html
// 构建组合的Predicate示例：Predicate p2=cb.equal();   Predicate p = cb.and(p3,cb.or(p1,p2));
//类似Object查询;统计ProjectionList projectionList = Projections.projectionList();    https://www.cnblogs.com/linjiaxin/p/6100129.html
/*:是Hibernate和JPA还是有些？差异 :
         User user = new User();
        user.setAge(new Integer(30));
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Example.create(user));
        List users = criteria.list();
        自动过滤掉空属性，根据已知User上已设定的属性，判定是否产生于where子句之中;
        Restrictions.sqlRestriction     Restrictions.sqlProjection
        Criteria    cr.setFetchMode(“students”, FetchMode.EAGER); = 表示预先抓取（Eager fetching）
*/

//认识“JPA实例查询”的局限性；id+ X对一关联表字段也会算比较条件；    https://www.cnblogs.com/rulian/p/6533109.html
//JpaRepository 派自 PagingAndSortingRepository 和ExampleQuery（Eqp）按“实例”进行查询;    ?PageRequest

/* * 解决 懒加载 JPA 典型的 N + 1 问题
        * 多层级 区域关系；     https://www.cnblogs.com/ealenxie/p/9800818.html
        *   @ManyToOne(fetch = FetchType.LAZY)
        @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
        javax.persistence.loadgraph：  在原有Entity的定义的基础上，定义还需要获取什么字段/关系
        javax.persistence.fetchgraph： 完全放弃原有Entity的定义，定义仅需要获取什么字段/关系
*/
