package org.fjsei.yewu.entity.fjtj;
import md.cm.geography.Address;
import org.fjsei.yewu.jpa.QuerydslNcExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DictAreaRepository extends JpaRepository<DictArea, Long>, JpaSpecificationExecutor<DictArea>, QuerydslNcExecutor<DictArea> {
    //DictEqpType  findByIdCodEquals(String cod);
}

