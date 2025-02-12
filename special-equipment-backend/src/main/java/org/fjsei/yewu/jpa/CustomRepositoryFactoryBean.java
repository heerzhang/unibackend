package org.fjsei.yewu.jpa;
//有点麻烦，本文件实际上引用抄袭2个代码段。

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

import static org.springframework.data.querydsl.QuerydslUtils.QUERY_DSL_PRESENT;

//版本升級特別小心，要跟溯源來的隨源代碼修改，否則可能出問題。2.1.4升級2.2.6就報錯啦！
//修改的源代码来源是 org.springframework.data/spring-data-jpa-2.1.4.RELEASE-sources.jar!/org/springframework/data/jpa/repository/support/JpaRepositoryFactoryBean.java
//直接替换 org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean。
//没法继承 JpaRepositoryFactoryBean。
//这个例子http://codingdict.com/questions/36138　可以直接继承 JpaRepositoryFactoryBean
//这个类是从 @EnableJpaRepositories 那里发动引导的。


//引用抄袭代码段 - 1。

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 * @param <T> the type of the repository
 */


public class CustomRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {

    private @Nullable EntityManager entityManager;
    private EntityPathResolver entityPathResolver;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
    private JpaQueryMethodFactory queryMethodFactory;

    /**
     * Creates a new {@link org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     *
     *
     *   Class<? extends T>    : :    T实际就是 EqpRepository，等等的 xxxRepository
     */

    public CustomRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * The {@link EntityManager} to be used.
     *
     * @param entityManager the entityManager to set
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#setMappingContext(org.springframework.data.mapping.context.MappingContext)
     */
    @Override
    public void setMappingContext(MappingContext<?, ?> mappingContext) {
        super.setMappingContext(mappingContext);
    }

    /**
     * Configures the {@link EntityPathResolver} to be used. Will expect a canonical bean to be present but fallback to
     * {@link SimpleEntityPathResolver#INSTANCE} in case none is available.
     *
     * @param resolver must not be {@literal null}.
     */
    @Autowired
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
    }

    /**
     * Configures the {@link JpaQueryMethodFactory} to be used. Will expect a canonical bean to be present but will
     * fallback to {@link org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory} in case none is
     * available.
     *
     * @param factory may be {@literal null}.
     */
    @Autowired
    public void setQueryMethodFactory(@Nullable JpaQueryMethodFactory factory) {

        if (factory != null) {
            this.queryMethodFactory = factory;
        }
    }
    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport#doCreateRepositoryFactory()
     */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        Assert.state(entityManager != null, "EntityManager must not be null!");

        return createRepositoryFactory(entityManager);
    }

    /** 关键部分！   这个才是被替换的删除的;
      版本升级，有多处增加 ？queryMethodFactory
     */
    /**
     * Returns a {@link RepositoryFactorySupport}.
     */
  /*  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        jpaRepositoryFactory.setEntityPathResolver(entityPathResolver);
        jpaRepositoryFactory.setEscapeCharacter(escapeCharacter);

        if (queryMethodFactory != null) {
            jpaRepositoryFactory.setQueryMethodFactory(queryMethodFactory);
        }

        return jpaRepositoryFactory;
    }
    */

   //修改的 代码：核心部分：
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        CustomRepositoryFactory customRepositoryFactory=new CustomRepositoryFactory(entityManager);
        //最早版，就屏蔽这行了：jpaRepositoryFactory.setEntityPathResolver(entityPathResolver);　//customRepositoryFactory 对 jpaRepositoryFactory
        customRepositoryFactory.setEscapeCharacter(escapeCharacter);
        if (queryMethodFactory != null) {
            customRepositoryFactory.setQueryMethodFactory(queryMethodFactory);
        }
        return customRepositoryFactory;
    }



    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {

        Assert.state(entityManager != null, "EntityManager must not be null!");

        super.afterPropertiesSet();
    }
    //2.2.6版本升級時 增加的
    public void setEscapeCharacter(char escapeCharacter) {

        this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
    }


    //引用抄袭代码段 - 1 ［文件结束！］。      上面和底下的分别属于拷贝2个不同文件的来源。


    //关键部分
    //新增加　附加的　静态类CustomRepositoryFactory：内部还抄袭代码段-2;
    private static class CustomRepositoryFactory<T, I extends Serializable>
            extends JpaRepositoryFactory {

        private final EntityManager em;
        //private final EntityManager entityManager;
        private EntityPathResolver entityPathResolver;
        //注意 CrudMethodMetadataPostProcessor 是自己修改的，版本升级特别注意，和原始来源差别。
        private final CrudMethodMetadataPostProcessor crudMethodMetadataPostProcessor;
        //无法访问 都不让使用；private final org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor crudMethodMetadataPostProcessor;

        public CustomRepositoryFactory(EntityManager em) {
            super(em);
            this.em = em;
            this.crudMethodMetadataPostProcessor = new CrudMethodMetadataPostProcessor();
            this.entityPathResolver = SimpleEntityPathResolver.INSTANCE;
            //this.queryMethodFactory = new DefaultJpaQueryMethodFactory(extractor);
            addRepositoryProxyPostProcessor(crudMethodMetadataPostProcessor);
        }

        //设置具体的实现类是BaseRepositoryImpl

        ///@Override
        @SuppressWarnings("unchecked")
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager em) {
            return new CustomRepositoryImpl<T, I>((Class<T>) information.getDomainType(), em);
        }

        @SuppressWarnings("unchecked")
        protected Object getTargetRepository(RepositoryMetadata metadata) {
            //JpaRepositoryImplementation<?, ?> repository= super.getTargetRepository((RepositoryInformation)metadata);
            //metadata.getCrudMethods().getFindAllMethod().orElse(null).
           //看看他的metadata,  repository

            return new CustomRepositoryImpl<T, I>(
                    (Class<T>) metadata.getDomainType(), em);
        }


        //设置具体的实现类的class
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return CustomRepositoryImpl.class;
        }

        /*　从父类抄袭修改看；　特别注意版本升级　影响；   抄袭代码段 - 2 ；
       　底下抄袭来自 spring-data-jpa-2.3.4.RELEASE-sources.jar!/org/springframework/data/jpa/repository/support/JpaRepositoryFactory.java:234
         * (non-Javadoc)
         * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getRepositoryFragments(org.springframework.data.repository.core.RepositoryMetadata)
        抄袭代码段 - 2：
        紧跟着底下的这个函数getRepositoryFragments实现代码，　仅仅一个函数，一行有修改。

         默认实现方法是可以crudMethodMetadataPostProcessor.getCrudMethodMetadata()，所以能够二级缓存hints注解能够生效缓存机制。
         默认实现：加上@QueryHints(value ={后，就连count(eqp)也能被缓存了，该条件下的count()保留10分钟。
         但我这里没办法做到吗？　　  直接去掉多余的count(eqp)／彻底！；
         */

        //引用抄袭代码段 - 2 [开始]。

        //针对QueryDsl的接口QuerydslPredicateExecutor使用findAll会count()且无cache.hints情况才引入的。
        @Override
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
            //父类是内部不对外开放的，只能首先调用执行，再来抄袭对象。
            //RepositoryComposition.RepositoryFragments superFragments=super.getRepositoryFragments(metadata);
            //初始化才运行这里。
            RepositoryComposition.RepositoryFragments fragments = RepositoryComposition.RepositoryFragments.empty();

            boolean isQueryDslRepository = QUERY_DSL_PRESENT
                    && QuerydslPredicateExecutor.class.isAssignableFrom(metadata.getRepositoryInterface());

            if (isQueryDslRepository) {
                //RepositoryInformation information =null;//getRepositoryInformation(metadata, fragments);
                //RepositoryInformation information = getRepositoryInformation(metadata, composition);
               //Object repositoryImplementation =super.getTargetRepository(information);
               //DefaultRepositoryInformation some= (DefaultRepositoryInformation)repositoryImplementation;

                if (metadata.isReactiveRepository()) {
                    throw new InvalidDataAccessApiUsageException(
                            "Cannot combine Querydsl and reactive repository support in a single interface");
                }

                JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

                //没办法 CrudMethodMetadataPostProcessor都不让使用；　crudMethodMetadataPostProcessor.getCrudMethodMetadata()无法获取！
                //CustomRepositoryImpl repository =(CustomRepositoryImpl)getTargetRepository(metadata);
                //CrudMethodMetadata crudMethodMetadata=repository.getRepositoryMethodMetadata();
                //repository.getMyQueryHints();

                //QuerydslNcExecutorImpl.class后面跟着的实际是他的构造参数/反射机制？
                //最后参数crudMethodMetadataPostProcessor.getCrudMethodMetadata()代表了DefaultQueryHints.of(entityInformation, metadata)锁@Graph+query hints;

                //另外一种解决途径：：派生QuerydslNcExecutorImpl ，另外做定义接口方法。替换QuerydslJpaPredicateExecutor功能 ；去掉分页查询的Count(*)功能。
                //Object querydslFragment = getTargetRepositoryViaReflection(QuerydslNcExecutorImpl.class, entityInformation,

                //无法访问spring-data-jpa-2.3.4.RELEASE-sources.jar!/org/springframework/data/jpa/repository/support/DefaultQueryHints.java
                //报错：QuerydslJpaPredicateExecutor.class No property findAllNc found for type Eqp!

                //只能自己只能定做QuerydslNcExecutorImpl，用来代替缺省的QuerydslJpaPredicateExecutor，直接用缺省无法引进我自定义接口findAllNc();
                //[　注意！]　这么多，实际上只改了这一行的。其它代码都和原来一致。
                Object querydslFragment = getTargetRepositoryViaReflection(QuerydslNcExecutorImpl.class, entityInformation,
                        em, entityPathResolver, crudMethodMetadataPostProcessor.getCrudMethodMetadata());

                //上面最后那个参数=null 就不能用缓存hints等注释 @标签功能;
                //下面这句引用父类生成的，加了以后， count() 也会有用上了cache缓存机制。
                 //去掉看 报错：QuerydslJpaPredicateExecutor.class No property findAllNc found for type Eqp!
                 // fragments= fragments.append( superFragments );
                //上面把 旧的父类生成的 也一起添加进去。

                fragments = fragments.append(RepositoryFragment.implemented(querydslFragment));
            }

            return fragments;
        }

        //引用抄袭代码段 - 2 ［文件结束！］。

    }

}


