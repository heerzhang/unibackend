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
@Table(name = "TB_UNT_DEPT" )
public class UntDept {
    @Id
    @Column(name = "ID", insertable=false, updatable=false)
    protected Long id;

    private String NAME;
    //名义地址
    private String DEPT_ADDR;    //基本上是为空的。   DEPT_ADDR  实际没有用
    private String  DEPT_AREA_COD;      // NUMBER(8),

    private String LKMEN;

    private String PHONE;
    private String MOBILE;      //优先使用

   /*   LKMEN          VARCHAR2(40),
      PHONE          VARCHAR2(60),
      MOBILE         VARCHAR2(40),
      DEPT_AREA_COD  NUMBER(8), 　 没啥用处？
    */
    private Long  UNT_ID;
}


