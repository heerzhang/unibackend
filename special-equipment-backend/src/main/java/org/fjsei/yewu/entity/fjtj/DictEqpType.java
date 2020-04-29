package org.fjsei.yewu.entity.fjtj;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_DICT_EQPTYPE" )
public class DictEqpType {
    @Id
    @Column(name = "CLASS_COD")
    private String idCod;       //设备型，标识号
    private String CLASS_NAME;      //'型类 名称'
}
//JPA没有主键会报错！ No identifier specified for entity:   联合主键