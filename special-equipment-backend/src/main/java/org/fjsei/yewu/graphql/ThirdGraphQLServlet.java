package org.fjsei.yewu.graphql;

import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.function.Consumer;

//配置额外增加的graphql 多个接口。

//@Configuration是不能加的，多个同名bean冲突;
//若@Configuration加上，导致starter里面GraphQLHttpServlet也会采用我这里的配置，致graphql接口也加载third模型目录.graphqls了，直接替换掉了starter。

@WebServlet(name = "ThirdGraphQLServlet", urlPatterns = {"/third/*"}, loadOnStartup= 0)
@ConditionalOnProperty(value = "unibackend.tools.third-enabled", havingValue = "true", matchIfMissing = true)
public class ThirdGraphQLServlet extends GraphQLHttpServlet {
  //@Autowired 针对特殊"graphql.servlet.enabled"=false导致MyWebAutoConfiguration全部关闭了；加(required = false)
  @Autowired(required = false)
  @Qualifier("thirdSchemaParser")
  SchemaParser schemaParser;

  @Override
  protected GraphQLConfiguration getConfiguration() {
    try {
      GraphQLSchema schema=schemaParser.makeExecutableSchema();
      //复合型的ROLE_字符串区分安全域接口。每个接口默认ROLE_xx_ 唯一相互都不同的代码 来区分。
      GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry(schema.getCodeRegistry())
              .fieldVisibility(new MyGraphqlFieldVisibility("ROLE_Th"))
              .build();
      Consumer<GraphQLSchema.Builder> builderConsumer = builder -> builder.codeRegistry(codeRegistry);
      return GraphQLConfiguration.with(schema.transform(builderConsumer)).build();
    } catch (Exception ex) {
      System.out.println("装载*.graphqls配置失败");
      return null;
    }
  }
}


//缺省graphql接口==stater包就自带那一个不用添加的。其他接口安全域/endpoint加。 再啓動一個安全域/endpoint；一对一的配置。

/*
没必要再做 注册，效果等价。
@Configuration
public class GraphQLServletConfiguration {
    @Bean
    public ServletRegistrationBean thirdGraphServletBean() {
        ServletRegistrationBean servletRegistrationBean= new ServletRegistrationBean(new ThirdGraphQLServlet(), "/");
*/

//ServletContext是Context（也就是Application级的）。ServletContext的功能要强大很多，并不只是保存一下配置参数，否则就叫ServletContextConfig了
