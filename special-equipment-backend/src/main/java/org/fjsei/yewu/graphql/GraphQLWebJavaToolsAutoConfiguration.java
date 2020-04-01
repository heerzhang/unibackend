package org.fjsei.yewu.graphql;

import graphql.kickstart.tools.*;
import com.oembedler.moon.graphql.boot.GraphQLServletProperties;
import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.tools.boot.*;
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.FieldDefinition;
import graphql.language.StringValue;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.visibility.DefaultGraphqlFieldVisibility;
import graphql.schema.visibility.GraphqlFieldVisibility;
import graphql.servlet.config.DefaultGraphQLSchemaServletProvider;
import graphql.servlet.config.GraphQLSchemaServletProvider;
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
    @Autowired(required = false)
    private SchemaParserDictionary dictionary;
    @Autowired(required = false)
    private GraphQLScalarType[] scalars;
    @Autowired(required = false)
    private List<SchemaDirective> directives;
    @Autowired(required = false)
    private List<SchemaDirectiveWiring> directiveWirings;

    //模型SDL缺省可见性判定角色：ROLE_USER，ROLE_OUTERSYS，若""空代表任意就算未登陆也允许; 每个安全域接口独立设置。
    @Value("${sei.visibility.role:''}")
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
    private SchemaParser buildSchemaParser(String defaultRole,
            List<GraphQLResolver<?>> resolvers,
            SchemaStringProvider schemaStringProvider,
            SchemaParserOptions.Builder optionsBuilder
    ) throws IOException {
        SchemaParserBuilder builder = dictionary != null ? new SchemaParserBuilder(dictionary) : new SchemaParserBuilder();
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
        //这里注入吧
        schemaParser.parseSchemaObjects().getCodeRegistryBuilder().fieldVisibility(myGraphqlFieldVisibility(defaultRole));
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
        return buildSchemaParser(visibilityDefaultRole.length()==0? null:visibilityDefaultRole,
                                  resolvers, schemaStringProvider, optionsBuilder);
    }

    //这个原始来源自/tools/boot/GraphQLJavaToolsAutoConfiguration.java
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
        //和其他的安全域模块接口平行的接入点。　倒数第二次机会。
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
        return buildSchemaParser(null,resolvers, schemaStringProvider, optionsBuilder);
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
        return buildSchemaParser("ROLE_OUTERSYS",resolvers, schemaStringProvider, optionsBuilder);
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

    //从*.graphqls文件注入角色权限控制机制，给外模型字段添加内省安全能力。
    //安全过滤自定义 directive @authr，因为角色权限而屏蔽给前端看的外模型的字段，返回null,考虑缺省角色以及可能特殊情况的字段。
    //这个是针对接口函数的一次性过滤字段，而不能针对单条数据记录来做细分上的过滤,不会因每一条数据记录都运行到这里。
    public GraphqlFieldVisibility myGraphqlFieldVisibility(String defaultRole) {
        return new DefaultGraphqlFieldVisibility() {
            @Override
            public List<GraphQLFieldDefinition> getFieldDefinitions(GraphQLFieldsContainer fieldsContainer) {
                //graphiQL的刷新时执行这里，获取每个模型对象的详细字段列表;　一般函数不会执行到这;
                return fieldsContainer.getFieldDefinitions();
            }
            //一次查询就来4次，中间运行AuthrDirective.onField的登记好大的钩子，+1次，这里过滤优先。
            //这里安全控制机制只能当作基础门槛，控制力度比较弱的。要强的就要SDL注解，Java注解，代码层次个别判定。
            @Override
            public GraphQLFieldDefinition getFieldDefinition(GraphQLFieldsContainer fieldsContainer, String fieldName) {
                GraphQLFieldDefinition field =fieldsContainer.getFieldDefinition(fieldName);
                //@authr注解的不受这里影响，两个机制都起作用；defaultRole是没有@authr注解的任何字段方法的权限要求。
                if(defaultRole==null)   //defaultRole=""代表随意都能访问缺省没有"@authr"注解的字段或方法。
                    return field;
                //执行到这时，还处于parseAndValidate堆栈，还没有获取数据呢，无法区分id。　多次调用最后ExecutionStrategy回已经取了子对象authorities数据没有User数据
                String fieldsContainerName =fieldsContainer.getName();
                if(fieldsContainerName.equals("Query")) {
                    if(fieldName.equals("auth") )
                        return field;
                }
                else if(fieldsContainerName.equals("Mutation")) {
                    //特殊接口可直通
                    if(fieldName.equals("authenticate") || fieldName.equals("logout") || fieldName.equals("newUser"))
                        return field;
                }
                //当前用户权限。
                Authentication auth= SecurityContextHolder.getContext().getAuthentication();
                if(auth!=null){
                    Boolean hasRole= auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(defaultRole) );
                    return hasRole? field:null;
                }
                else
                    return null;   //意味着屏蔽该字段或接口函数。
            }

        };
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
*/
