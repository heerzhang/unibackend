package md.cm.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company>, QuerydslPredicateExecutor<Company> {


}
