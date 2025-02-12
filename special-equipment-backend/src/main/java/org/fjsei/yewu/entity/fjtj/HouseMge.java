package org.fjsei.yewu.entity.fjtj;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_HOUSE_MGE",
        uniqueConstraints={@UniqueConstraint(columnNames={"BUILD_ID"})} )
public class HouseMge {
    @Id
    @Column(name = "BUILD_ID")
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    //@SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    //楼盘+管理小区场所；大厦标志名。
    private String BUILD_NAME;
    private String BUILD_ADDR;      //'楼盘位置'　可能为 '/'空的。
    private String INST_BUILD_TYPE;     //有9种分类代号
   // INST_BUILD_TYPE VARCHAR2(50),'楼盘性质。\,商品房、复建房、拆迁安置房、廉租房、回迁房、经济适用房、限价房,棚户区。'
   private String AREA_COD;    //    NUMBER(10),
   private String BUILD_STATE;   //'删除' , '在用'
}

//本表的记录个数算相对比较少了。
