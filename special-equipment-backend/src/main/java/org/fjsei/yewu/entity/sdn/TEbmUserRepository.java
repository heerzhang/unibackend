package org.fjsei.yewu.entity.sdn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//可选择技术：
//4：DAO层接口实现JpaSpecificationExecutor<T>接口进行复杂查询，Criteria；CriteriaBuilder； :toPredicate;
//3:基于JPA Criteria 的动态查询CriteriaQuery,Predicate;弱类型;强类型,减少基于字符串JPQL的语义错误,在结果中搜索.
//2:“实例查询”QueryByExampleExecutor；ExampleMatcher的局限性和复杂性.
//1:“简单查询”=可根据接口函数名字自动生成HQL的模式。查询关键字Limit/or/NotLike/NotIn/Not/Containing/IgnoreCase。

public interface TEbmUserRepository extends JpaRepository<TEbmUser, String>, JpaSpecificationExecutor<TEbmUser> {

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
    //根据学生名字查出其老师信息
    @Query("SELECT teacher FROM ImTeacher teacher JOIN teacher.imStudent student WHERE student.name=:name")
    @Query("SELECT teacher FROM TEbmUser teacher JOIN TEbmUser.userId student WHERE student.name=:name")
    ImTeacher findByStuName(@Param("name") String name);
   // 根据老师名字查出其学生列表
    @Query("SELECT student FROM ImStudent student JOIN student.imTeacher teacher WHERE teacher.name = :name")
    Set<ImStudent> findByStudByTeaName(@Param("name") String name);
*/


//"update T_EBM_USER b set b.status=3 WHERE b.STATUS=0 AND b.USER_TYPE=1 AND b.user_id IN(select user_id FROM T_SDN_ENP a where a.unt_name=?)"
    //这里没法用tSdnEnp属性来查询，因为对方的id字段并非userId。
    @Modifying
    @Query(value="update TEbmUser b set b.status=3  where b.status=0 AND b.userType=1 AND b.userId IN (select userId from TSdnEnp as a where a.untName=:untName)")
    public int closeManagerAcountOfUnt(@Param("untName") String untName);

//"select a.user_id,b.LOGIN_NAME,a.LKMAN_MOBIL from T_SDN_ENP a,T_EBM_USER b WHERE a.user_id=b.user_id AND b.status=0 AND b.USER_TYPE=1 AND a.unt_name=?"
    boolean existsByStatusAndUserTypeAndTSdnEnp_UntName(String status, String userType, String untName);

}

