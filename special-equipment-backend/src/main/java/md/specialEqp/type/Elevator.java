package md.specialEqp.type;
//type包 还少个索道的大类型。
//9000客运索道F000安全附件7000压力管道元件 这三个type特种设备没有做独立参数表，减少派生实体类。
import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.*;

//子类不能再做@org.hibernate.annotations.Cache() 与 @Inheritance(strategy=)和@Version注解的。
//java中的接口之间可以实现多继承，也可以实现多实现。但是java中的类只能实现单extends继承


@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Elevator  extends Eqp {
    //3000电梯  TB_ELEV_PARA  电梯技术参数表
    /**IF_SPEC_EQP特种电梯；.IF_SPEC_EQP是否特殊设备*/
    private Boolean spec;
    /**IF_UNNORMAL是否非标电梯， ？非标 和 特种不一样概念？*/
    private Boolean nnor;
    /**合并字段IF_OLDBUILD  旧楼房加装, ? IF_OLDBUILD_INST旧楼加装;*/
    private Boolean oldb;
    /**ELEFLOORNUMBER电梯层数，*/
    private Short  flo;
    /**ELEHEIGHT3000提升高度*/
    private Float  hlf;
    /**SLIDWAY_USE_LENG3300人行道使用区段长度 人行道使用区段长度（自动人行道）(m)"SLIDWAY_USE_LENG"*/
    private Float  lesc;
    /**名义宽度(自动扶梯/自动人行道)(mm)"NOMI_WIDTH"*/
    private Float  wesc;
    /**RUNVELOCITY运行速度 ，米/秒 运行速度(m/s)"RUNVELOCITY"
     */
    private Float  vl;

    //IMPORT_TYPE进口类型,

    /**控制屏型号"CONSCRTYPE"*/
    private String  cpm;
    /**控制屏出厂编号"CONTSCRCODE"*/
    private String  cpi;
    /**曳引机型号"TRACANGTYPE"*/
    private String  tm;
    /**曳引机出厂编号"TRACANGLEAFACNUMBER"*/
    private String  tno;
    /**电动机(驱动主机)型号"ELEC_TYPE"*/
    private String  mtm;
    /**电动机(驱动主机)编号"ELEC_COD"*/
    private String  mtno;
    /**缓冲器形式"BUFFER_MODE"*/
    private String  buff;

    /**额定载荷(kg)"RATEDLOAD"  */
    private Float rtl;
    /**是否加装附加装置"IF_ADDDEVICE" 字符串*/
    private String aap;
    /**轿厢意外移动保护装置型号"CAR_PROTECT_TYPE"*/
    private String  prot;
    /**开门方式"DOOR_OPEN_TYPE"*/
    private String  doop;
    /**限速器型号"RESTSPEEDTYPE"*/
    private String  limm;
    /**控制方式"CONTROL_TYPE"*/
    private String  opm;
}


//不可改技术参数：控制屏出厂编号"CONTSCRCODE"控制屏型号"CONSCRTYPE"电梯层数"ELEFLOORNUMBER"电动机(驱动主机)型号"ELEC_TYPE"缓冲器形式"BUFFER_MODE"额定载荷(kg)"RATEDLOAD"是否加装附加装置"IF_ADDDEVICE"
// 人行道使用区段长度（自动人行道）(m)"SLIDWAY_USE_LENG"名义宽度(自动扶梯/自动人行道)(mm)"NOMI_WIDTH"轿厢意外移动保护装置型号"CAR_PROTECT_TYPE"电动机(驱动主机)编号"ELEC_COD"
// 运行速度(m/s)"RUNVELOCITY"曳引机型号"TRACANGTYPE"曳引机出厂编号"TRACANGLEAFACNUMBER"开门方式"DOOR_OPEN_TYPE"限速器型号"RESTSPEEDTYPE"控制方式"CONTROL_TYPE"


//@NoArgsConstructor缺少导致 No default constructor for entity:  : md.specialEqp.type.电梯
