package org.fjsei.yewu.graphql;

import graphql.kickstart.tools.*;
import com.oembedler.moon.graphql.boot.GraphQLServletProperties;
import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.tools.boot.*;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaDirectiveWiring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.DispatcherServlet;
import java.io.IOException;
import java.util.List;

//配置额外增加的graphql 多个接口。
//全关闭不现实"graphql.servlet.enabled"考虑都是=true。

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnProperty(value = "graphql.servlet.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({GraphQLJavaToolsAutoConfiguration.class, JacksonAutoConfiguration.class})
@EnableConfigurationProperties({GraphQLServletProperties.class,MyGraphQLToolsProperties.class})
@Slf4j
public class MyWebAutoConfiguration {
    //@Autowired  private MyGraphQLToolsProperties props;
    @Autowired(required = false)
    private SchemaParserDictionary dictionary;
    @Autowired(required = false)
    private GraphQLScalarType[] scalars;
    @Autowired(required = false)
    private List<SchemaDirective> directives;
    @Autowired(required = false)
    private List<SchemaDirectiveWiring> directiveWirings;

    //配置正常starter自带的哪一个缺省graphql; 配置unibackend:tools: starter-enabled
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "unibackend.tools.starter-enabled", havingValue = "true", matchIfMissing = true)
    public SchemaStringProvider schemaStringProvider() {
        return new ClasspathResourceSchemaStringProvider("graphql/**/*.graphqls");
    }
    //[都会用到的]抽取合并功能。
    private SchemaParser buildSchemaParser(
            List<GraphQLResolver<?>> resolvers,
            SchemaStringProvider schemaStringProvider,
            SchemaParserOptions.Builder optionsBuilder
    ) throws IOException {
        SchemaParserBuilder builder = dictionary != null ? new SchemaParserBuilder(dictionary) : new SchemaParserBuilder();

        List<String> schemaStrings = schemaStringProvider.schemaStrings();
        schemaStrings.forEach(builder::schemaString);
        //记录启动日志，跟踪报错，定位schema文本。
        StringBuffer allSchemaText=new StringBuffer("");
        schemaStrings.forEach(s -> {allSchemaText.append(s+"\n");});
        log.info("定位{}的schema整文本:{}", schemaStringProvider.getClass(),allSchemaText);

        if (scalars != null) {
            builder.scalars(scalars);
        }

        builder.options(optionsBuilder.build());

        if (directives != null) {
            directives.forEach(it -> builder.directive(it.getName(), it.getDirective()));
        }

        if (directiveWirings != null) {
            directiveWirings.forEach(builder::directiveWiring);
        }

        return builder
                .resolvers(resolvers)
                .build();
    }

    //配置正常starter自带的哪一个缺省graphql;
    //普通的配置，抄自和接替spring-boot-autoconfigure-tools/graphql/kickstart/tools/boot/GraphQLJavaToolsAutoConfiguration.java
    @Bean
    @ConditionalOnBean({GraphQLResolver.class})
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "unibackend.tools.starter-enabled", havingValue = "true", matchIfMissing = true)
    public SchemaParser schemaParser(
            List<GraphQLResolver<?>> resolvers,
            @Qualifier("schemaStringProvider")   SchemaStringProvider schemaStringProvider,
            SchemaParserOptions.Builder optionsBuilder
    ) throws IOException {
        return buildSchemaParser(resolvers, schemaStringProvider, optionsBuilder);
    }

    //配置正常starter自带的哪一个缺省graphql;
    // 新add??　原是　来自tools模块GraphQLJavaToolsAutoConfiguration。
    // @ConditionalOnMissingBean({GraphQLSchema.class, GraphQLSchemaProvider.class})
    // GraphQLSchema graphQLSchema(@Qualifier("publicSchemaParser") SchemaParser schemaParser)
    //解决冲突Parameter 0 of method graphQLSchema in graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration required a single bean, but 2 were found:多个。
    @Bean
    @ConditionalOnBean(SchemaParser.class)
    @ConditionalOnMissingBean({GraphQLSchema.class, GraphQLSchemaProvider.class})
    @ConditionalOnProperty(value = "unibackend.tools.starter-enabled", havingValue = "true", matchIfMissing = true)
    public GraphQLSchema graphQLSchema(@Qualifier("schemaParser") SchemaParser schemaParser) {
        return schemaParser.makeExecutableSchema();
    }

    /*
    下面是额外 graphql接口、 安全性考虑。
    */


    @Bean
    //@ConditionalOnMissingBean
    public SchemaStringProvider publicSchemaStringProvider() {
        return new ClasspathResourceSchemaStringProvider("publicReport/**/*.graphqls");
    }

    //针对unibackend.tools.**-enabled都=false情形，加入@Primary，降级处理，graphql主线程变身public的接口。
    //实际unibackend.tools.starter-enabled只能降级主线程graphql安全域；除非总开关"graphql.servlet.enabled"关闭。
    @Primary
    @Bean
    @ConditionalOnBean({GraphQLResolver.class})
    //@ConditionalOnMissingBean
    public SchemaParser publicSchemaParser(
            List<GraphQLResolver<?>> resolvers,
            @Qualifier("publicSchemaStringProvider")      SchemaStringProvider schemaStringProvider,
            SchemaParserOptions.Builder optionsBuilder
    ) throws IOException {
        return buildSchemaParser(resolvers, schemaStringProvider, optionsBuilder);
    }

    //名字相近的 graphql-java-tools/./com/coxautodev/graphql/tools/SchemaParser.kt;不同./graphql/kickstart/execution/config/GraphQLSchemaProvider
    //@Bean(name = "thirdSchemaStringProvider")
    @Bean
    public SchemaStringProvider thirdSchemaStringProvider() {
        return new ClasspathResourceSchemaStringProvider("thirdService/**/*.graphqls");
    }

    @Bean
    @ConditionalOnBean({GraphQLResolver.class})
    //@ConditionalOnMissingBean
    public SchemaParser thirdSchemaParser(
            List<GraphQLResolver<?>> resolvers,
            @Qualifier("thirdSchemaStringProvider")      SchemaStringProvider schemaStringProvider,
            SchemaParserOptions.Builder optionsBuilder
    ) throws IOException {
        return buildSchemaParser(resolvers, schemaStringProvider, optionsBuilder);
    }

   //@ConditionalOnProperty(value = "graphql.servlet.use-default-objectmapper", havingValue = "true", matchIfMissing = true)
   //public ObjectMapperProvider objectMapperProvider(ObjectMapper objectMapper)

}


//配置文件"graphql.tools.schema-location-pattern",缺省: "**\/*.graphqls" 的，它来自graphql-kickstart-spring-boot-autoconfigure-tools。
//标准配置文件"graphql.servlet.xx"  ，它来自 graphql-spring-boot-autoconfigure / GraphQLWebAutoConfiguration。
//supplier也是是用来创建对象的，但是不同于传统的创建对象语法：new();

/* 早期版本的配置：
    第一个entity/repository: 在对应的各自*.graphqls配置文件中， type Query / type Mutation不用extend;
    Bean的装配参数个数不固定，参数类型也不固定。
    @Bean
    @Transactional(readOnly = true)
    public XXQuery  query(AuthorRepository authorRepository, BookRepository bookRepository,) {
        return new XXQuery(authorRepository, bookRepository,);
    }
*/
