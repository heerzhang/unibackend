package org.fjsei.yewu.datasource;

import org.fjsei.yewu.jpa.CustomRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
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


//旧平台　对接报检平台的库。


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactorySdn",
    //transactionManagerRef = "transactionManager",
    repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class,
    basePackages = {"org.fjsei.yewu.entity.sdn"})
public class SdnConfig {

  //  @Autowired
  //  private JpaVendorAdapter jpaVendorAdapter;

    @Autowired
    @Qualifier("sdnDataSource")
    private DataSource sdnDataSource;

    @Bean(name = "entityManagerSdn")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySdn(builder).getObject().createEntityManager();
    }

    @Resource
    private JpaProperties jpaProperties;

    private Map<String, Object> getVendorProperties() {
        return null; //jpaProperties.getHibernateProperties(new HibernateSettings());
    }

    @Bean(name = "entityManagerFactorySdn")
   // @DependsOn("transactionManager")  transactionManagerSdn
   // @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySdn(EntityManagerFactoryBuilder builder) {
        return builder
            .dataSource(sdnDataSource)
            .packages("org.fjsei.yewu.entity.sdn")
            .persistenceUnit("sdnPersistenceUnit")
      //      .properties(getVendorProperties())
            .build();
    }

    /*
    JPA使用的
    @Bean(name = "transactionManagerSdn")
    PlatformTransactionManager transactionManagerSdn(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySdn(builder).getObject());
    }
    */

}
