package org.fjsei.yewu.datasource;

import org.fjsei.yewu.jpa.CustomRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
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


//解决了问题：@ManyToMany或@OneToMany的Many多的那一方，一定用Set容器来存放，而不能用List集合。

//旧平台的主库(测试时接入测试平台，但正式转换后是接入旧平台生产环境的正式库)

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryFjtj",
        transactionManagerRef = "transactionManager",
        repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class,
        basePackages = {"org.fjsei.yewu.entity.fjtj"})
public class FjtjConfig {

    @Resource
    @Qualifier("fjtjDataSource")
    private DataSource fjtjDataSource;

    @Primary
    @Bean(name = "entityManagerFjtj")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryFjtj(builder).getObject().createEntityManager();
    }

    @Resource
    private JpaProperties jpaProperties;

    private Map<String, Object> getVendorProperties() {
        return null; //jpaProperties.getHibernateProperties(new HibernateSettings());
    }

    /**
     * 设置实体类所在位置
     */

    //@Primary 注意只能一个数据源注解@Primary的。
    @Bean(name = "entityManagerFactoryFjtj")
    //@DependsOn("transactionManager")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryFjtj(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(fjtjDataSource)
                .packages("org.fjsei.yewu.entity.fjtj")
                .persistenceUnit("fjtjPersistenceUnit")
                .build();

        //必须在@EnableJpaRepositories里头注解  "org.fjsei.yewu.entity.fjtj"
    }

}
