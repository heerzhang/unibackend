package org.fjsei.yewu.graphql;

import graphql.kickstart.servlet.config.DefaultGraphQLSchemaServletProvider;
import graphql.kickstart.servlet.config.GraphQLSchemaServletProvider;
import graphql.kickstart.spring.web.boot.GraphQLServletProperties;
import graphql.kickstart.tools.*;

import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.tools.boot.*;
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.FieldDefinition;
import graphql.language.StringValue;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.visibility.DefaultGraphqlFieldVisibility;
import graphql.schema.visibility.GraphqlFieldVisibility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.DispatcherServlet;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

//配置额外增加的graphql 多个接口。
//合并参照这两个来源： graphql-spring-boot-autoconfigure/com/oembedler/moon/graphql/boot/GraphQLWebAutoConfiguration.java
//同时参照： graphql-kickstart-spring-boot-autoconfigure-tools/graphql/kickstart/tools/boot/GraphQLJavaToolsAutoConfiguration.java
//全关闭不现实"graphql.servlet.enabled"考虑都是=true。

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnProperty(value = "graphql.servlet.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({GraphQLJavaToolsAutoConfiguration.class, JacksonAutoConfiguration.class})
@EnableConfigurationProperties({GraphQLServletProperties.class,MyGraphQLToolsProperties.class})
@Slf4j
public class GraphQLWebJavaToolsAutoConfiguration {
    //@Autowired  private MyGraphQLToolsProperties props;
    //@Autowired(required = false)    private SchemaParserDictionary dictionary;
    @Autowired(required = false)
    private GraphQLScalarType[] scalars;
    @Autowired(required = false)
    private List<SchemaDirective> directives;
    @Autowired(required = false)
    private List<SchemaDirectiveWiring> directiveWirings;

    //模型SDL缺省可见性判定角色：ROLE_USER，ROLE_OUTERSYS，若""空代表任意就算未登陆也允许; 每个安全域接口独立设置。
    @Value("${sei.visibility.role:null}")
    private String visibilityDefaultRole;

    //配置正常starter自带的哪一个缺省graphql; 配置unibackend:tools: starter-enabled
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "unibackend.tools.starter-enabled", havingValue = "true", matchIfMissing = true)
    public SchemaStringProvider schemaStringProvider() {
        return new ClasspathResourceSchemaStringProvider("graphql/**/*.graphqls");
    }

    //这个原始来源自/tools/boot/GraphQLJavaToolsAutoConfiguration.java
    //[都会用到的]抽取合并功能。
    private SchemaParser buildSchemaParser( List<GraphQLResolver<?>> resolvers,
            SchemaStringProvider schemaStringProvider,
            SchemaParserOptions.Builder optionsBuilder
    ) throws IOException {
        SchemaParserBuilder builder = new SchemaParserBuilder();
        //SchemaParserBuilder builder = dictionary != null ? new SchemaParserBuilder(dictionary) : new SchemaParserBuilder();
         //builder.访问不到codeRegistry
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

        SchemaParser schemaParser = builder
                                    .resolvers(resolvers)
                                    .build();
        //add 可见性过滤；
        //这里注入不行？, 语法分析器，时机不对
        //schemaParser.parseSchemaObjects().getCodeRegistryBuilder().fieldVisibility(myGraphqlFieldVisibility()).build().;
        return schemaParser;
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
        //和其他的安全域模块接口平行的接入点。　开始初始化的机会。
        return buildSchemaParser(resolvers, schemaStringProvider, optionsBuilder);
    }

    //这个原始来源自/tools/boot/GraphQLJavaToolsAutoConfiguration.java
    //配置正常starter自带的哪一个缺省graphql;  其他的额外graphql接口、安全域模块在各自主文件xxGraphQLServlet配置。
    // 新add??　原是　来自tools模块GraphQLJavaToolsAutoConfiguration。
    // @ConditionalOnMissingBean({GraphQLSchema.class, GraphQLSchemaProvider.class})
    // GraphQLSchema graphQLSchema(@Qualifier("mainSchemaParser") SchemaParser schemaParser)
    //解决冲突Parameter 0 of method graphQLSchema in graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration required a single bean, but 2 were found:多个。
    @Bean
    @ConditionalOnBean(SchemaParser.class)
    @ConditionalOnMissingBean({GraphQLSchema.class, GraphQLSchemaProvider.class})
    @ConditionalOnProperty(value = "unibackend.tools.starter-enabled", havingValue = "true", matchIfMissing = true)
    public GraphQLSchema graphQLSchema(@Qualifier("schemaParser") SchemaParser schemaParser) {
        //和其他的安全域模块接口平行的接入点。　倒数第二次机会。
        GraphQLSchema   schema=schemaParser.makeExecutableSchema();
        //若在buildSchemaParser阶段就将myGraphqlFieldVisibility加入，没经过makeExecutableSchema，后果是subscription方法有reflectasm报反射数据入口失败。
        //新版本API使用方法有毛病，导致字段方法竟然没挂接到处理函数。
        GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry(schema.getCodeRegistry())
                              .fieldVisibility(new MyGraphqlFieldVisibility(visibilityDefaultRole.length()==0? null:visibilityDefaultRole))
                                    .build();
        Consumer<GraphQLSchema.Builder> builderConsumer = builder -> builder.codeRegistry(codeRegistry);
        return schema.transform(builderConsumer);
    }

    /*
    下面是额外 graphql接口、 安全性考虑。
    */


    @Bean
    //@ConditionalOnMissingBean
    public SchemaStringProvider mainSchemaStringProvider() {
        return new ClasspathResourceSchemaStringProvider("model/**/*.graphqls");
    }

    //针对unibackend.tools.**-enabled都=false情形，加入@Primary，降级处理，graphql主线程变身public的接口。
    //实际unibackend.tools.starter-enabled只能降级主线程graphql安全域；除非总开关"graphql.servlet.enabled"关闭。
    @Primary
    @Bean
    @ConditionalOnBean({GraphQLResolver.class})
    //@ConditionalOnMissingBean
    public SchemaParser mainSchemaParser(
            List<GraphQLResolver<?>> resolvers,
            @Qualifier("mainSchemaStringProvider")      SchemaStringProvider schemaStringProvider,
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

    //为了支持GraphqlFieldVisibility需要加，要定做：
    //仅仅针对graphql主线程的配置，其他安全域接口模块需要额外在servlet的getConfiguration()里面配置。
    //这个时刻已经初始化完成。
    @Bean
    @ConditionalOnMissingBean
    public GraphQLSchemaServletProvider graphQLSchemaProvider(GraphQLSchema schema) {
        return new DefaultGraphQLSchemaServletProvider(schema);
    }

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
核心data fetcher，graphql.schema.PropertyDataFetcher 针对 map{K/V} 和 POJO类的模式 都能支持；
graphQL模型定义type M implements X 那么必须在接口方法中引用该type至少到一次，否则报错Object type 'EqpEs' implements a known interface, but no class could be found for that type name。
*/

