package org.fjsei.yewu;

import md.specialEqp.EqpRepository;
import md.specialEqp.inspect.IspRepository;
import md.specialEqp.inspect.TaskRepository;
import org.fjsei.yewu.graphql.MyGraphQLToolsProperties;
import org.fjsei.yewu.property.FileStorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/*
主程序、启动类; 相当于API网关。
集成ehcache二级缓存 https://blog.csdn.net/yiduyangyi/article/details/54176453
*/
///@EnableCaching  属于spring自带cache机制。

@EnableCaching
@EnableConfigurationProperties({
        MyGraphQLToolsProperties.class, FileStorageProperties.class
})
@EnableTransactionManagement
@SpringBootApplication
@ServletComponentScan
public class StartApplication {

  public static void main(String[] args) {
    SpringApplication.run(StartApplication.class, args);
  }


  //jpa懒加载报异常：session失效,配置文件中加jpa.properties.open-in-view: true;
  @Bean
  public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
    return new OpenEntityManagerInViewFilter();
  }

  @PersistenceContext(unitName = "entityManagerFactorySei")
  private EntityManager emSei;

  /*
  启动后的可执行的定制任务类。Override  run();
  用@Order 注解来定义执行顺序。
      */
  @Bean
  @Transactional
  public CommandLineRunner demo(EqpRepository eqpRepository, TaskRepository  taskRepository, IspRepository ispRepository)
  {
    if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
    return (args) -> {
      //Author author = new Author();
      System.out.println("例行任务()运行");
    };
  }

}


//@某些注解比如lombok.Data是编译的用，IDEA配置Build>Compiler>Annotation Processors勾上。
//graphql-spring-boot-starter帶入缺省還有一個graphQLsevlet:  缺省endpoint配置/graphql / . 缺省配模型文件目錄的*/*.graphqls;
//安全控制核心在：org/fjsei/yewu/config/MyGraphQLWebAutoConfiguration.java ，没有jwt认证，就无法访问后端资源。
//数据库H2没有启动时，可能无法启动本服务器。
//hibernate-ehcache不支持3代的，所以出现告警WARN  org.hibernate.orm.deprecation - HHH020100:  新版本ehcache3不能用,要配套ehcache2；
//注入配置参数对象的模式@EnableConfigurationProperties， 否则启动时找不到bean；
//SpringBoot-2.1.1使用https 需要  https://www.cnblogs.com/yvanchen1992/p/10111534.html
/*Actuator:在运行时改变日志等级     https://www.jianshu.com/p/d5943e303a1f
前后端非分离架构Spring MVC当中的M存数据K/V即Model(模型)，V=模板引擎生成HTML，C=@RequestMapping("/users")。
Spring Web Flow构建于Spring MVC之上，跨多HTTP请求，具有状态，处理事务数据。　@EnableSpringDataWebSupport；
HATEOAS是REST架构约束，client不用事先知道服务步骤，不用硬编码URI；服务器还可以在不破坏和客户端交互情况下，更改URI；属Spring MVC时。
*/
