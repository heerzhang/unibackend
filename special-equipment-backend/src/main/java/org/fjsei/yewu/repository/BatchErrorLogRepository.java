package org.fjsei.yewu.repository;


import org.fjsei.yewu.jpa.QuerydslNcExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface BatchErrorLogRepository extends JpaRepository<BatchErrorLog, Long>, JpaSpecificationExecutor<BatchErrorLog>, QuerydslNcExecutor<BatchErrorLog> {

}
