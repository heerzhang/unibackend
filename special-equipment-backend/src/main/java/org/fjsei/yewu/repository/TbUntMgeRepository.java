package org.fjsei.yewu.repository;

import org.fjsei.yewu.model.TbUntMge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//可选择技术：
//4：DAO层接口实现JpaSpecificationExecutor<T>接口进行复杂查询，Criteria；CriteriaBuilder； :toPredicate;
//3:基于JPA Criteria 的动态查询CriteriaQuery,Predicate;弱类型;强类型,减少基于字符串JPQL的语义错误,在结果中搜索.
//2:“实例查询”QueryByExampleExecutor；ExampleMatcher的局限性和复杂性.
//1:“简单查询”=可根据接口函数名字自动生成HQL的模式。查询关键字Limit/or/NotLike/NotIn/Not/Containing/IgnoreCase。

public interface TbUntMgeRepository extends JpaRepository<TbUntMge, String>, JpaSpecificationExecutor<TbUntMge> {

    //可根据接口函数名字自动生成HQL的模式。
    // Page<User> queryFirst10ByLastname(String lastname, Pageable pageable);
    // List<User> findTop10ByLastname(String lastname, Pageable pageable);
    // Long countByLastname(String lastname);
    //   void deleteByProject_Cus_id(Long id);
    //boolean existsById(ID primaryKey)
    //findByLastnameAndFirstname

/*
    在ImTeacher.java中添加

    @OneToMany(mappedBy = "imTeacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    private Set<ImStudent> imStudent = new HashSet<ImStudent>();
}
    //根据学生名字查出其老师信息
    @Query("SELECT teacher FROM ImTeacher teacher JOIN teacher.imStudent student WHERE student.name=:name")
    @Query("SELECT teacher FROM TEbmUser teacher JOIN TEbmUser.userId student WHERE student.name=:name")

    ImTeacher findByStuName(@Param("name") String name);

   // 根据老师名字查出其学生列表

    @Query("SELECT student FROM ImStudent student JOIN student.imTeacher teacher WHERE teacher.name = :name")

    Set<ImStudent> findByStudByTeaName(@Param("name") String name);

*/

    TbUntMge findByUntName(String untname);
}

