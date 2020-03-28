package org.fjsei.yewu.entity.fjtj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//实体和Repository表定义的类可以随意地从一个数据库直接文件移动过去，每个数据库有独立的目录包文件夹。
//@Autowired注入同名的但是不同数据库HrUserinfoRepository，麻烦，需要加上全程限定前缀目录包。

public interface HrUserinfoRepository extends JpaRepository<HrUserinfo, String>, JpaSpecificationExecutor<HrUserinfo> {
    HrUserinfo findByUserIdEquals(String sId);
}

