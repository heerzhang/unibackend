package org.fjsei.yewu.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;


///public interface EQPRepository extends JpaRepository<EQP, Long>, JpaSpecificationExecutor<EQP> {
///public interface CustomRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>
///        extends PagingAndSortingRepository<T, ID>
//@NoRepositoryBean指明当前接口不是我们领域类的接口（如PersonRepository）

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>
{


}
