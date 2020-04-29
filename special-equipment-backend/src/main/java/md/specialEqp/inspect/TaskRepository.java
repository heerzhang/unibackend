package md.specialEqp.inspect;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;


public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByDepAndStatus(String dep, String status);

    //定制SQL；    找到所有  关联到的dev.id=？的任务。
    @Query(value="from Task b  inner join b.devs a WHERE a.id=:eqpId")
    Set<Task> findByDevsContaining_Id(@Param("eqpId") Long eqpId);

    List<Task> findByDep(String dep);
    List<Task> findByStatus(String status);
    long count(@Nullable Specification<Task> spec);
}


