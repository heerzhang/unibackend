package org.fjsei.yewu.entity.fjtj;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_UNT_SECUDEPT" )
public class UntSecudept {
    @Id
    @Column(name = "ID", insertable=false, updatable=false)
    protected Long id;

    private String NAME;
    //对外标识地址
    private String SECUDEPT_ADDR;
    private String  SECUDEPT_AREA_COD;

    private String LKMEN;

    private String PHONE;
    private String MOBILE;      //优先使用

   /* 　LKMEN             VARCHAR2(40),
        PHONE             VARCHAR2(60),
        MOBILE            VARCHAR2(40),
        //地址Division#Address.$Adminunit.areacode 代替这个SECUDEPT_AREA_COD。
        SECUDEPT_AREA_COD VARCHAR2(12),　当前使用单位的分支机构不在监察机构管辖的区域范围内！JC_JCUNT_AREA  SUBSTR(AREA_COD,1,4)
        监察机构管辖的区域范围？in某个省，|| in某个地级市，|| in某些县级区级，|| in某些乡镇。
    */
   private Long  UNT_ID;
}


