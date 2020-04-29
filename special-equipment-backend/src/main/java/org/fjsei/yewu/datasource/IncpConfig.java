package org.fjsei.yewu.datasource;

import org.fjsei.yewu.jpa.CustomRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;


//旧平台　对接的监察平台库。


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactoryIncp",
    transactionManagerRef = "transactionManager",
    repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class,
    basePackages = {"org.fjsei.yewu.entity.incp"})
public class IncpConfig {

    @Resource
    @Qualifier("incpDataSource")
    private DataSource incpDataSource;


    @Bean(name = "entityManagerIncp")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryIncp(builder).getObject().createEntityManager();
    }

    @Resource
    private JpaProperties jpaProperties;

    private Map<String, Object> getVendorProperties() {
        return null; //jpaProperties.getHibernateProperties(new HibernateSettings());
    }

    /**
     * 设置实体类所在位置
     */


    @Bean(name = "entityManagerFactoryIncp")
    //@DependsOn("transactionManager")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryIncp(EntityManagerFactoryBuilder builder) {
        return builder
            .dataSource(incpDataSource)
            .packages("org.fjsei.yewu.entity.incp")
            .persistenceUnit("incpPersistenceUnit")
        //    .properties(getVendorProperties())
            .build();
    }

    /*
    JPA使用的
    //
    @Bean(name = "transactionManagerBar")
    public PlatformTransactionManager transactionManagerFoo(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryFoo(builder).getObject());
    }
    */

}
