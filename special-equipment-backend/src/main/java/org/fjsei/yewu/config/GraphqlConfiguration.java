package org.fjsei.yewu.config;

//import org.fjsei.yewu.exception.GraphQLErrorAdapter;

import org.fjsei.yewu.repository.AuthorRepository;
import org.fjsei.yewu.repository.BookRepository;
import org.fjsei.yewu.resolver.FirstMutationResolver;
import org.fjsei.yewu.resolver.FirstQueryResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;


//GraphQL的配置解析入口类。
//顺序，没有需要Autowired前置变量的那些@Bean率先执行；

@Configuration
public class GraphqlConfiguration {
        //static  MyGraphqlFieldVisibility myGraphqlFieldVisibility;
    /*@Autowired 注入死循环的错误。 顺序稍微改变就！dependencies of some of the beans in the application context form a cycle:
    @Autowired
    private GraphQLSchemaProvider graphQLSchemaProvider;        //实际注入后的是这个=DefaultGraphQLSchemaProvider
     */
    // @Autowired
    //private ExecutionStrategyProvider executionStrategyProvider;

    //   private GraphQLErrorHandler graphQLErrorHandler_orgi;       //DefaultGraphQLErrorHandler

    //没必要自定义GraphQLErrorHandler　@Bean；　这里有graphql-java-servlet/graphql/servlet/DefaultGraphQLErrorHandler.java提供了。
    /*
    @Bean
    public GraphQLErrorHandler errorHandler() {         //不是@Bean不会运行这里了。

     //   graphQLSchemaProvider.getSchema().getFieldVisibility().getFieldDefinition();
      //  myGraphqlFieldVisibility=new MyGraphqlFieldVisibility();

        return new GraphQLErrorHandler() {
            @Override
            public List<GraphQLError> processErrors(List<GraphQLError> errors) {
                List<GraphQLError> clientErrors = errors.stream()
                        .filter(this::isClientError)
                        .collect(Collectors.toList());

                List<GraphQLError> serverErrors = errors.stream()
                        .filter(e -> !isClientError(e))
                        .map(GraphQLErrorAdapter::new)
                        .collect(Collectors.toList());

                List<GraphQLError> e = new ArrayList<>();
                e.addAll(clientErrors);
                e.addAll(serverErrors);
                return e;
            }

            protected boolean isClientError(GraphQLError error) {
                return !(error instanceof ExceptionWhileDataFetching || error instanceof Throwable);
            }
        };
    }
    */


    //实体Author：
    //第一个entity/repository: 在对应的各自*/*.graphqls配置文件中， type Query / type Mutation不用extend;
    //Bean的装配参数个数不固定，参数类型也不固定。
    @Bean
    @Transactional(readOnly = true)
    public FirstQueryResolver query(AuthorRepository authorRepository, BookRepository bookRepository) {
        return new FirstQueryResolver(authorRepository, bookRepository);
    }

    @Bean
    public FirstMutationResolver mutation(AuthorRepository authorRepository, BookRepository bookRepository) {
        return new FirstMutationResolver(authorRepository, bookRepository);
    }

    //其余每个实体都实现一个implements GraphQLResolver< * > ,每个实体一个/对应*.graphqls？＝〉过时了。
    //其余是 扩展的Query,Mutation底下添加：

    /*
    @Bean
    @Transactional(readOnly = true)
    public BookResolver authorResolver(AuthorRepository authorRepository) {
       // myGraphqlFieldVisibility=new MyGraphqlFieldVisibility();

        return new BookResolver(authorRepository);
    }
    */

    //public PostResolver userResolver(MyRestServiceClient myRestServiceClient) { return new PostResolver(myRestServiceClient); }
}


