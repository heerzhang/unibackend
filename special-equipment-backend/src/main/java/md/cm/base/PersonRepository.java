package md.cm.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person>, QuerydslPredicateExecutor<Person> {


}
