package org.fjsei.yewu.entity.fjtj;

import org.fjsei.yewu.jpa.QuerydslNcExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UntDeptRepository extends JpaRepository<UntDept, Long>, JpaSpecificationExecutor<UntDept>, QuerydslNcExecutor<UntDept> {

}