package org.fjsei.yewu.entity.sei;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface FileRepository extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {


    //File findByAddress(String address);

}

