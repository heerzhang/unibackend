package org.fjsei.yewu.graphql;

import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.tools.*;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.function.Consumer;

//参看：https://github.com/graphql-java-kickstart/graphql-spring-boot
//配置额外增加的graphql 多个接口。

//[注意] 5.7版本com.coxautodev.graphql.tools包名改了，而6.0版本graphql.kickstart.tools包。
//这里schemaParser:来自xx-autoconfigure-tools/GraphQLJavaToolsAutoConfiguration是个Bean，并不是那个graphql.schema.idl.SchemaParser,名字相同而已！

//调换了：starter包原本自带的那个mapping:反而改成了/public； 很多自动配置缺省的都是/graphql的endpoint。
@WebServlet(name = "MainGraphQLServlet", urlPatterns = {"/graphql/*"}, loadOnStartup =0)
@ConditionalOnProperty(value = "unibackend.tools.main-enabled", havingValue = "true", matchIfMissing = true)
public class MainGraphQLServlet extends GraphQLHttpServlet {
  @Autowired(required = false)
  @Qualifier("mainSchemaParser")
  SchemaParser schemaParser;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  protected GraphQLConfiguration getConfiguration() {
    try {
        GraphQLSchema schema=schemaParser.makeExecutableSchema();
        GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry(schema.getCodeRegistry())
                .fieldVisibility(new MyGraphqlFieldVisibility("ROLE_Ma"))
                .build();
        Consumer<GraphQLSchema.Builder> builderConsumer = builder -> builder.codeRegistry(codeRegistry);
        return GraphQLConfiguration.with(schema.transform(builderConsumer)).build();
    } catch (Exception ex) {
      //System.out.println("装载*.graphqls失败"+ex.toString());
        logger.error("装载*.graphql失败", ex);
      return null;
    }
  }
}


//安全控制部分改成 graphql.schema.GraphQLCodeRegistry 对接的接口，原来是MyGraphQLWebAutoConfiguration。
/* @Bean
public GraphQLSchema graphQLSchema(SchemaParser schemaParser) { 这函数入口时刻schemaParser就已经把所有模型文件加载完了。
  SchemaParserOptions options = SchemaParserOptions.newOptions()
          .TypeDefinitionFactory(new MyTypeDefinitionFactory())
          .build();
  GraphQLSchema schema = SchemaParser.newParser().file("schema.graphqls")
          .resolvers(new QueryResolver())
          .options(options)
          .build()
          .makeExecutableSchema();
*/

//@Component强横，添加了@Component导致：会直接替换schemaStringProvider类型的spring Bean;
