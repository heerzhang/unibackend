package org.fjsei.yewu.model.geography;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TownRepository extends JpaRepository<Town, Long>, JpaSpecificationExecutor<Town> {
}