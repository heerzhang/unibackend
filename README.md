# unibackend
行业后端
org/fjsei/yewu/config/MyGraphQLWebAutoConfiguration.java GraphqlFieldVisibility 
org/fjsei/yewu/config/GraphqlConfiguration.java

//第一个entity/repository: 在对应的各自*/*.graphqls配置文件中， type Query / type Mutation不用extend;
//Bean的装配参数个数不固定，参数类型也不固定。
@Bean
@Transactional(readOnly = true)
public Query query(AuthorRepository authorRepository, BookRepository bookRepository) {
    return new Query(authorRepository, bookRepository);
}
