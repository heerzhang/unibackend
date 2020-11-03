package md.cm.unit;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit>, QuerydslPredicateExecutor<Unit> {

    Unit findUnitByPerson_Id(Long id);
    Unit findUnitByCompany_Id(Long id);

}
