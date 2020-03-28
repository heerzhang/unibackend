package org.fjsei.yewu.entity.sei;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {




}

