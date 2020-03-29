package org.fjsei.yewu.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 跨实例SQL查询==数据库网关是DMS刚推出的新特性,阿里云DMS发布 https://www.sohu.com/a/299468414_612370
 云数据库，分布式数据库服务，Amazon的Aurora，阿里云的PolarDB等 https://www.cnblogs.com/cchust/p/11366175.html
 底层的Jdbc数据源配置类;这个也必须配置，否则错误非常隐蔽与深处。
 **/

@Configuration
public class JdbcDataSourceConfig {
    //只能一个 是有@Primary 注释的 数据源。
    @Primary
    @Bean(name = "dataSourcePropertiesSei")
    @Qualifier("dataSourcePropertiesSei")
    @ConfigurationProperties(prefix="app.datasource.sei")
    public DataSourceProperties dataSourcePropertiesSei() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "seiDataSource")
    @Qualifier("seiDataSource")
    @ConfigurationProperties(prefix="app.datasource.sei")
    public DataSource seiDataSource(@Qualifier("dataSourcePropertiesSei") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /*
     每个数据库都要 固定配置一个，对应的table映射的/repository目录也固定。
     */

    @Bean(name = "dataSourcePropertiesSdn")
    @Qualifier("dataSourcePropertiesSdn")
    @ConfigurationProperties(prefix="app.datasource.sdn")
    public DataSourceProperties dataSourcePropertiesSdn() {
        return new DataSourceProperties();
    }

    @Bean(name = "sdnDataSource")
    @Qualifier("sdnDataSource")
    @ConfigurationProperties(prefix="app.datasource.sdn")
    public DataSource sdnDataSource(@Qualifier("dataSourcePropertiesSdn") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /* 旧的库； 監察的。
     */

    @Bean(name = "dataSourcePropertiesIncp")
    @Qualifier("dataSourcePropertiesIncp")
    @ConfigurationProperties(prefix="app.datasource.incp")
    public DataSourceProperties dataSourcePropertiesIncp() {
        return new DataSourceProperties();
    }

    @Bean(name = "incpDataSource")
    @Qualifier("incpDataSource")
    @ConfigurationProperties(prefix="app.datasource.incp")
    public DataSource incpDataSource(@Qualifier("dataSourcePropertiesIncp") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /* 旧的平台运行库;  app.datasource.fjtj是配置文件的某项。
     */

    @Bean(name = "dataSourcePropertiesFjtj")
    @Qualifier("dataSourcePropertiesFjtj")
    @ConfigurationProperties(prefix="app.datasource.fjtj")
    public DataSourceProperties dataSourcePropertiesFjtj() {
        return new DataSourceProperties();
    }

    @Bean(name = "fjtjDataSource")
    @Qualifier("fjtjDataSource")
    @ConfigurationProperties(prefix="app.datasource.fjtj")
    public DataSource fjtjDataSource(@Qualifier("dataSourcePropertiesFjtj") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    //下面这种 就不一定必须的了。

    /*
    可能用，使用底层Jdbc情况
    */

    @Bean(name = "seiJdbcTemplate")
    @Qualifier("seiJdbcTemplate")
    public JdbcTemplate seiJdbcTemplate(@Qualifier("seiDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "sdnJdbcTemplate")
    @Qualifier("sdnJdbcTemplate")
    public JdbcTemplate sdnJdbcTemplate(@Qualifier("sdnDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "incpJdbcTemplate")
    @Qualifier("incpJdbcTemplate")
    public JdbcTemplate incpJdbcTemplate(@Qualifier("incpDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}


