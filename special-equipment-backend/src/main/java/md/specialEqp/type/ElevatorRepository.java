package md.specialEqp.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ElevatorRepository extends JpaRepository<Elevator, Long>, JpaSpecificationExecutor<Elevator> {


}

