package org.fjsei.yewu.datasource;

import org.fjsei.yewu.jpa.CustomRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/*
 阿里云DMS（数据管理）推出了跨数据库实例查询服务，一条SQL完成跨数据库实例Join查询?
*/

//启动失败hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags看https://blog.csdn.net/danchaofan0534/article/details/53873207
//1.将List变为Set　  .fetch=FetchType.LAZY　3.@Fetch(FetchMode.SUBSELECT)   4.不是JPA规范@IndexColumn唯一性索引;
//一对多或多对多的多方数据存容器类如Set、List、Map;
//解决了问题：@ManyToMany或@OneToMany的Many多的那一方，一定用Set容器来存放，而不能用List集合。


//我的主库,  basePackages子目录可以

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactorySei",
    transactionManagerRef = "transactionManager",
    repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class,
    basePackages = {"org.fjsei.yewu.entity.sei","org.fjsei.yewu.repository","org.fjsei.yewu.model","md"})
public class SeiConfig {

    @Resource
    @Qualifier("seiDataSource")
    private DataSource seiDataSource;

    @Primary
    @Bean(name = "entityManagerSei")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySei(builder).getObject().createEntityManager();
    }

    @Resource
    private JpaProperties jpaProperties;

    private Map<String, Object> getVendorProperties() {
        return null; //jpaProperties.getHibernateProperties(new HibernateSettings());
       // return jpaProperties.getHibernateProperties(new HibernateSettings());
    }

    /**
     * 设置实体类所在位置
     */

    @Primary
    @Bean(name = "entityManagerFactorySei")
    //@DependsOn("transactionManager")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySei(EntityManagerFactoryBuilder builder) {
        return builder
            .dataSource(seiDataSource)
            .packages("org.fjsei.yewu.entity.sei","org.fjsei.yewu.repository","org.fjsei.yewu.model","md")
            .persistenceUnit("seiPersistenceUnit")
         //.properties(getVendorProperties())　　实际环境Oracle连接特别得慢！　本地H2测试库连接很快。
            .build();

        //必须在@EnableJpaRepositories里头注解  "org.fjsei.yewu.entity.sei"
    }


    /*
    JPA使用的
    //@Primary
    @Bean(name = "transactionManagerBar")
    public PlatformTransactionManager transactionManagerSei(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySei(builder).getObject());
    }
    */

}



//大库只有一个的。   basePackages = {"org.fjsei.yewu.entity.sei","org.fjsei.yewu.repository","org.fjsei.yewu.model","md"})
//模型包包路径两配置     .packages("org.fjsei.yewu.entity.sei","org.fjsei.yewu.repository","org.fjsei.yewu.model","md")
