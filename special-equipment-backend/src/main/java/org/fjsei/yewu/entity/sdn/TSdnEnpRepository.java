package org.fjsei.yewu.entity.sdn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TSdnEnpRepository extends JpaRepository<TSdnEnp,String> {

    //通过方法名称直接生成查询,可根据接口函数名字自动生成HQL的模式。
//  TSdnEnp findByUserId(String userId);
    //find…By, read…By, query…By, count…By和get…By等前缀,通过And和Or将不同实体属性组合在一起，构成组合查询。
    //List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);  AllIgnoreCase
    //特殊参数操作
  //  Page<User> findByLastname(String lastname, Pageable pageable);
  //  List<User> findByLastname(String lastname, Sort sort);
    //User findTopByOrderByAgeDesc(); Optional  Distinct
    //6. 流化查询结果 ,7 异步查询

    //这种方式在绝大多数场景都适用，但也有可能选择到错误的属性。避免这种歧义，可以在方法名中使用_来人工定义遍历点。
    //下划线 _ 当作保留字符，所以强烈建议按照标准java命名规范命名（不要在属性名中使用下划线，要使用驼峰规范）。
    TSdnEnp findByTEbmUser_userId(String userId);

    //这是自动函数名解析接口的模式，逻辑表达能力有限制而且参数个数和函数名字长度太长了！
    //("select a.user_id,b.status,b.user_type from T_SDN_ENP a,T_EBM_USER b WHERE a.user_id=b.user_id AND b.status=0 AND a.unt_name=? AND b.LOGIN_NAME=?");
    TSdnEnp findByUntNameAndTEbmUser_LoginName(String untName, String loginName);

    //";\n update T_SDN_ENP a set a.status=3 WHERE a.STATUS=0 AND a.unt_name=? AND exists(select 1 FROM T_EBM_USER b where a.user_id=b.user_id AND b.USER_TYPE=1 AND b.STATUS=0)"
   //@Query(value="update TSdnEnp　set status=3  where status=0 AND untName=:Name AND tEbmUser.userType=1 AND tEbmUser.status=0")  left join b.userId as a
    //@Modifying(clearAutomatically = true)
    //自写JPQL/HQL模板模式：多参数@Param注解;　但这不是原生SQL,Native sql的模式。
    @Modifying
    @Query("UPDATE TSdnEnp as a SET a.status=3 WHERE a.status=0 AND a.untName=:untName AND a.tEbmUser=(from TEbmUser as b where b.status=0 AND b.userType=1)")
    public int closeManagerAcountOfUnt(@Param("untName") String untName);

    //直接判定tSdnEnp.getTEbmUser().getStatus().equals("0")
    TSdnEnp findTopByUntNameAndTEbmUser_LoginNameAndStatus(String untName, String loginName, String status);
    TSdnEnp findTopByUntNameAndTEbmUser_LoginNameAndStatusNotIn(String untName, String loginName, String status);
}


