package md.specialEqp;

import md.specialEqp.inspect.ISP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

//JPA的核心，数据库装入,到对应的实体类去。

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    List<Report> findByIsp(ISP isp);


}

