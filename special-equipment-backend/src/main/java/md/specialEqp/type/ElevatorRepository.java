package md.specialEqp.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

//派生继承子类 不需要再做JpaRepository，用这个也只能查询本子类以及更低层次之子类．

public interface ElevatorRepository extends JpaRepository<Elevator, Long>, JpaSpecificationExecutor<Elevator> {

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
    List<Elevator> findAll();
}

