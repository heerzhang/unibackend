package org.fjsei.yewu.model.geography;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CountyRepository extends JpaRepository<County, Long>, JpaSpecificationExecutor<County> {
}