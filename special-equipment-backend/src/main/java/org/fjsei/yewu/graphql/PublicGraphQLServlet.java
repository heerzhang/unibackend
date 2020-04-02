package org.fjsei.yewu.graphql;

import graphql.kickstart.tools.*;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLConfiguration;
import graphql.servlet.GraphQLHttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.function.Consumer;

//配置额外增加的graphql 多个接口。

//[注意] 5.7版本com.coxautodev.graphql.tools包名改了，而6.0版本graphql.kickstart.tools包。
//这里schemaParser:来自xx-autoconfigure-tools/GraphQLJavaToolsAutoConfiguration是个Bean，并不是那个graphql.schema.idl.SchemaParser,名字相同而已！


@WebServlet(name = "PublicGraphQLServlet", urlPatterns = {"/public/*"}, loadOnStartup = 0, initParams = {
        @WebInitParam(name = "graphql", value = "/"),
} )
@ConditionalOnProperty(value = "unibackend.tools.public-enabled", havingValue = "true", matchIfMissing = true)
public class PublicGraphQLServlet extends GraphQLHttpServlet {
  @Autowired(required = false)
  @Qualifier("publicSchemaParser")
  SchemaParser schemaParser;

  @Override
  protected GraphQLConfiguration getConfiguration() {
    try {
        GraphQLSchema schema=schemaParser.makeExecutableSchema();
        GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry(schema.getCodeRegistry())
                .fieldVisibility(new MyGraphqlFieldVisibility(null))
                .build();
        Consumer<GraphQLSchema.Builder> builderConsumer = builder -> builder.codeRegistry(codeRegistry);
        return GraphQLConfiguration.with(schema.transform(builderConsumer)).build();
    } catch (Exception ex) {
      System.out.println("装载*.graphqls配置失败");
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
