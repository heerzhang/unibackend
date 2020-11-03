package org.fjsei.yewu.entity.fjtj;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

//老旧平台的设备主表。

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_EQP_MGE" )
public class EqpMge {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //用JPA高级功能时会报错，JPA Repository规范要求的字段名：必须是小写头的，也不能用_号分割的。字段名尽量连续小写的变量。
    //最早EQP_COD，改eQPCOD，再次改eqpcod;
    @Column(name = "EQP_COD")
    private String eqpcod;
    private String OIDNO;
    private String EQP_TYPE;
    private String EQP_USECERT_COD;     //'使用证号'
    private String EQP_STATION_COD;     //'设备代码(设备国家代码)
    private String EQP_REG_COD;
    private String EQP_NAME;            //没用~ ！
    private String EQP_VART;        //'设备名称'=品种
   // private String EQP_VART_NAME;
    private String EQP_SORT;        //设备类别  EQP_SORT_NAME
  //  private String EQP_SORT_NAME;
    private String SUB_EQP_VART;
    private String EQP_MOD;      //型号'
    private char EQP_REG_STA;
    private char EQP_USE_STA;
    private char   IN_CAG;
    private String EQP_LEVEL;
    private String FACTORY_COD;     //出厂编号；
    private String EQP_INNER_COD;     //单位内部编号'
    @Temporal(TemporalType.DATE)
    private Date MAKE_DATE;   //制造日期
    private char  IF_INCPING;   //是否正在安装监检
    private String IF_MAJEQP;   //是否重要特种设备
    private Character IS_MOVEEQP;
    //private char   IS_MOVEEQP;
    //内设管理部门=很少的。   关联TB_UNT_DEPT；单位有多个内部管理人；实际用途非常简略=没意义。
    //内设分支机构，根据地区分辨的。     关联TB_UNT_SECUDEPT；    报告显示的。
    private Long USE_UNT_ID;      //使用单位ID  ,   USE_UNT_ADDR
    //使用单位类型：无内设；内设管理　算统一地域之内的的行政划分； 内设机构是按照地域码的分,分支Name多个，每个分支机构联系人也能多个，地址与区域码可重复。
    private Long SECUDEPT_ID;     //分支机构ID'　
    private Long MAKE_UNT_ID;     //制造单位ID
    private Long ALT_UNT_ID;      //改造单位ID　　= -1
    private Long MANT_UNT_ID;  //维保单位ID

    @Temporal(TemporalType.DATE)
    private Date ALT_DATE;    //改造日期'
    private Date  FIRSTUSE_DATE;    //设备投用日期
    private Date  COMPE_ACCP_DATE;  //竣工验收日期
    //private Date  END_USE_DATE;   //全null?=使用年限到期时间
    private Date  DESIGN_USE_OVERYEAR;      //DESIGN_USE_OVERYEAR设计使用年限 到期时间
    @Temporal(TemporalType.DATE)
    private Date NEXT_ISP_DATE1;  //下次检验日期1（在线、年度）
    @Temporal(TemporalType.DATE)
    private Date NEXT_ISP_DATE2;  //下次检验日期2(机电定检，内检，全面）'
    //部分重复的属性。
  //  private String USE_UNT_ADDR;    //作废没用的？ 全部=null
    //报告书用到的：
    //大部分都有的，但是可能很简单地址。配合区域码和负责任务的机关。
    private String  EQP_USE_ADDR;     // '使用地点' 可能是具体几号楼？可能是很简单的称谓,最具体地址，大地址的反而没说。
    //EQP_AREA_COD  EQP_LAT EQP_LONG ; 地理信息不算重要， 仅作标记。
    private String  EQP_AREA_COD;   //设备所在区域
    //非强制字段： 有BUILD_ID的设备 不算 很多比例。
    private Long BUILD_ID;    //'楼盘ID'；   标示出该设备当前归属哪一个楼盘下辖管控的，可以询问该楼盘就知晓的到。
    private String EQP_USE_OCCA;    //使用场合
    //安全人员是合并字段？ SAFE_MAN || USE_LKMEN,  ；
    //WX_SIGNATURE;
    //不能使用float 做类型， 可能是null;
    @Column( nullable = true)       //若不添加nullable，无法跑起来。
    private Float  EQP_PRICE;   //产品设备价(进口安全性能监检的设备价)(单位：元)
    private String  USE_MOBILE;     //设备联系手机
    //private char 遇到null不仅启动能报错,.findAll(pageable)也能报错。
    private Boolean  NOTELIGIBLE_FALG1;      //会有null情况需要判定
    //private Character NOTELIGIBLE_FALG1;
    //0 null 都算合格, 1是不合格
    private Boolean  NOTELIGIBLE_FALG2;
    private String   LAST_ISP_CONCLU1;      //'最后一次检验结论1'
    private String   LAST_ISP_CONCLU2;
    private Date  LAST_ISP_DATE1;
    private Date  LAST_ISP_DATE2;

    private Long REG_UNT_ID;  //监察注册机构ID //REG_UNT_NAME注册机构名称
    private Long PROP_UNT_ID;   //产权单位id
    private Long INST_UNT_ID;   //安装单位ID

    //MGE_DEPT_TYPE 1-分支机构还是2-安全管理部门;
    //.SECUDEPT_ID 关联TB_UNT_SECUDEPT   .SAFE_DEPT_ID  关联TB_UNT_DEPT
    //部门管理类型MGE_DEPT_TYPE若=2： ||'-'|| SECUDEPT_NAME ), TB_UNT_SECUDEPT关联  MGE_DEPT_TYPE若=1很少作废了 关联TB_UNT_DEPT实际数据太少作废

    //ISP_TYPE '检验范畴 1:机电，2：承压，3：综合'  <==业务类型？设备类型；
}

//和旧平台的对接实体表。
