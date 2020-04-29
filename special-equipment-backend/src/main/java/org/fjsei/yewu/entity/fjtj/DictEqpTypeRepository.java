package org.fjsei.yewu.entity.fjtj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DictEqpTypeRepository extends JpaRepository<DictEqpType, Long>, JpaSpecificationExecutor<DictEqpType> {
    DictEqpType  findByIdCodEquals(String cod);
}

//这里主键类型Long是糊弄的， 是老旧设计非正规的数据库，运行不报错就行。