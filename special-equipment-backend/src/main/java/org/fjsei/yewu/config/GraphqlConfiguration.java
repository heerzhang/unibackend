package org.fjsei.yewu.config;

import graphql.kickstart.tools.boot.SchemaDirective;
import org.fjsei.yewu.graphql.directive.AuthrDirective;
import org.fjsei.yewu.graphql.directive.RangeDirective;
import org.fjsei.yewu.graphql.directive.UppercaseDirective;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//自定义的GraphQL的配置。

@Configuration
public class GraphqlConfiguration {
    /*不需要了；Bean的装配参数个数不固定，参数类型也不固定。
    @Bean
    @Transactional(readOnly = true)
    public Query query(AuthorRepository authorRepository, BookRepository bookRepository) {
        return new Query(authorRepository, bookRepository);
    }
    */

    @Bean
    public SchemaDirective myRangeDirective() {
        return new SchemaDirective("range", new RangeDirective());
    }

  /*配置Config方式; GraphQLWebAutoConfiguration会同名冲突。
  @Bean
  ServletRegistrationBean graphQLServletRegistrationBean(GraphQLInvocationInputFactory invocationInputFactory,
                                                         GraphQLQueryInvoker queryInvoker,GraphQLObjectMapper graphQLObjectMapper) {
    return new ServletRegistrationBean(new SimpleGraphQLHttpServlet(invocationInputFactory,queryInvoker, graphQLObjectMapper, null,true), "/d2gql");
  }
  */

    @Bean
    public SchemaDirective myUppercaseDirective() {
        return new SchemaDirective("uppercase", new UppercaseDirective());
    }

    @Bean
    public SchemaDirective  myAuthrDirective() {
        return new SchemaDirective("authr", new AuthrDirective());
    }
}
