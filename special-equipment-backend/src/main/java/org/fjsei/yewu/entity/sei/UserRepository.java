package org.fjsei.yewu.entity.sei;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    //仅仅提供名称无需写代码，这个接口函数是由框架自动解决的，自动生成SQL到数据库查询的逻辑。

    //!!注意 字段名字变化了，函数名也要修改！否则，启动不了TomcatStarter;
    //字段改名影响： graphql.tools.SchemaParser 配置文件

    User findByUsernameAndPassword(String name, String password);
    User findByUsername(String username);
    List<User> findAllByUsernameLike(String username);
    //系统启动时刻，检查这些接口函数，并且自动生成HQL/SQL查询语句。

}


//JPA在互联网海量数据的环境，确有很多问题，最典型的比如对于数据分片，分表分库上支持的欠缺。JPA Provider(例如Hibernate)会生成效率低下的SQL;

