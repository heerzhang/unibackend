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
@Table(name = "TB_DICT_AREA" )
public class DictArea {
    @Id
    private Long id;
    private Long FAU_TYPE_PARENT_CODE;   //父区划的id;
    private String FAU_TYPE_NAME;      //区划名字
    private String FAU_TYPE_CODE;   //编码8
}

