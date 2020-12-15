package md.cm.unit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface DivisionRepository extends JpaRepository<Division, Long>, JpaSpecificationExecutor<Division>, QuerydslPredicateExecutor<Division> {

    Division findTopByOldId(Long id);
}
