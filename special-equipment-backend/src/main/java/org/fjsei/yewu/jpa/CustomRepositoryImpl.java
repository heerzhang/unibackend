package org.fjsei.yewu.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.function.LongSupplier;

/*
替换JPA接口，定做;  利于配套graphQL，
针对这个 Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable)接口， 取消count()，count计数无法缓存，graphQL也不能一次封装返回count给客户端。
count可以走其它接口。
提高性能。
其它接口可不受到这个改造的影响。
by ：  herzhang ， 2019/03/02
*/


//从SimpleJpaRepository扩展，修改；

public class CustomRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements CustomRepository<T, ID>
{
    private final EntityManager entityManager = null;

    //让数据操作方法中可以使用entityManager
    //CustomRepositoryImpl构造函数，需要当前处理的领域模型和entitymanager作为构造函数参数
    public CustomRepositoryImpl(Class<T> domainClass, EntityManager entityManager){
        super(domainClass, entityManager);
    }


    //不会影响其他的接口。比如List<T> findAll(@Nullable Specification<T> spec, Sort sort);没有走这里的修改途径。


    //xxxRepository不需要更改 extends接口 也可以的， 也是一样能够走到这里的。
    //interface EqpRepository extends JpaRepository<Eqp, Long>, JpaSpecificationExecutor<Eqp>



    //不影响 cache, 不影响注解Hints;
    ///修改的:

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable)
     */
    public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable) {

        TypedQuery<T> query = getQuery(spec, pageable);
        return  pageable.isUnpaged() ? new PageImpl<T>(query.getResultList())
                : readPage(query, getDomainClass(), pageable, spec);
    }

    /**
     * Reads the given {@link TypedQuery} into a {@link Page} applying the given {@link Pageable} and
     * {@link Specification}.
     *
     * @param query must not be {@literal null}.
     * @param domainClass must not be {@literal null}.
     * @param spec can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    protected <S extends T> Page<S> readPage(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable,
                                             @Nullable Specification<S> spec) {

        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }


    ///每个page，都会查询count(ALL spec)； 去掉 多余费时间的count(*)； 且count没办法缓存，而且无法返回给graphQL客户。


        return PageableExecutionUtils.getPage(query.getResultList(), pageable,
                null);
    }


    //内部
    public static class PageableExecutionUtils {

        /**
         * Constructs a {@link Page} based on the given {@code content}, {@link Pageable} and {@link Supplier} applying
         * optimizations. The construction of {@link Page} omits a count query if the total can be determined based on the
         * result size and {@link Pageable}.
         *
         * @param content must not be {@literal null}.
         * @param pageable must not be {@literal null}.
         * @param totalSupplier must not be {@literal null}.
         * @return the {@link Page}.
         */
        public static  <T> Page<T> getPage(List<T> content, Pageable pageable, LongSupplier totalSupplier) {

            Assert.notNull(content, "Content must not be null!");
            Assert.notNull(pageable, "Pageable must not be null!");
      //      Assert.notNull(totalSupplier, "TotalSupplier must not be null!");

            if (pageable.isUnpaged() || pageable.getOffset() == 0) {

                if (pageable.isUnpaged() || pageable.getPageSize() > content.size()) {
                    return new PageImpl<>(content, pageable, content.size());
                }

                return new PageImpl<>(content, pageable, 0L);
            }

            if (content.size() != 0 && pageable.getPageSize() > content.size()) {
                return new PageImpl<>(content, pageable, pageable.getOffset() + content.size());
            }

            return new PageImpl<>(content, pageable, 0L);
        }
    }

}


