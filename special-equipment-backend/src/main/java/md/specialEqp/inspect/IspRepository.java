package md.specialEqp.inspect;

import md.system.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.lang.Nullable;


import javax.persistence.QueryHint;
import java.util.List;


//缺省情形，这个类可以是空的。
//定制数据库各个模型=表的SQL操作。


public interface IspRepository extends JpaRepository<Isp, Long>, JpaSpecificationExecutor<Isp> {


    List<Isp> findByIspMen(User ispmen);
    List<Isp> getByDev_IdOrderById(Long dev);

    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
    List<Isp> findAll(@Nullable Specification<Isp> spec);

    List<Isp> getByDev_IdAndTask_IdOrderByNextIspDate(Long dev, Long task);
}

