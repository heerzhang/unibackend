package org.fjsei.yewu.entity.fjtj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UntMgeRepository extends JpaRepository<UntMge, Long>, JpaSpecificationExecutor<UntMge> {
}