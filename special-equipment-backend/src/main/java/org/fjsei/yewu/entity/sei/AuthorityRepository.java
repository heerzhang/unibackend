package org.fjsei.yewu.entity.sei;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface AuthorityRepository extends JpaRepository<Authority, Long>, JpaSpecificationExecutor<Authority> {

    //仅仅提供名称无需写代码，这个接口函数是由框架自动解决的，自动生成SQL到数据库查询的逻辑。

    // !! 注意 字段名字变化了，**.repository里头的函数名也要修改！否则，启动不了TomcatStarter;
    // !! 字段改名影响： graphql.tools.SchemaParser 配置文件\src\main\resources\graphql\**

    Authority findByName(AuthorityName name);      //不是String参数；

}

