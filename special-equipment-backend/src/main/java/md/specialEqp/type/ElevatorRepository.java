package md.specialEqp.type;

import md.specialEqp.EQP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;


public interface ElevatorRepository extends JpaRepository<Elevator, Long>, JpaSpecificationExecutor<Elevator> {

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
    List<Elevator> findAll();
}

