package org.fjsei.yewu;

import org.fjsei.yewu.graphql.MyGraphQLToolsProperties;
import org.fjsei.yewu.graphql.RangeDirective;
import org.fjsei.yewu.graphql.UppercaseDirective;
import graphql.kickstart.tools.boot.SchemaDirective;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        MyGraphQLToolsProperties.class
})
@SpringBootApplication
@ServletComponentScan
public class StartApplication {

  public static void main(String[] args) {
    SpringApplication.run(StartApplication.class, args);
  }

  @Bean
  public SchemaDirective myCustomDirective() {
    return new SchemaDirective("uppercase", new UppercaseDirective());
  }

  @Bean
  public SchemaDirective rangeDirective() {
    return new SchemaDirective("range", new RangeDirective());
  }

  /*配置Config方式; GraphQLWebAutoConfiguration会同名冲突。
  @Bean
  ServletRegistrationBean graphQLServletRegistrationBean(GraphQLInvocationInputFactory invocationInputFactory,
                                                         GraphQLQueryInvoker queryInvoker,GraphQLObjectMapper graphQLObjectMapper) {
    return new ServletRegistrationBean(new SimpleGraphQLHttpServlet(invocationInputFactory,queryInvoker, graphQLObjectMapper, null,true), "/d2gql");
  }
 */

}



//@某些注解比如lombok.Data是编译的用，IDEA配置Build>Compiler>Annotation Processors勾上。
//graphql-spring-boot-starter帶入缺省還有一個graphQLsevlet:  缺省endpoint配置/graphql / . 缺省配模型文件目錄的*/*.graphqls;
