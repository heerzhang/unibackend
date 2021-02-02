package md.specialEqp.type;
//type包 还少个索道的大类型。
//9000客运索道F000安全附件7000压力管道元件 这三个type特种设备没有做独立参数表，减少派生实体类。
import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.*;
import java.util.Date;

//子类不能再做@org.hibernate.annotations.Cache() 与 @Inheritance(strategy=)和@Version注解的。
//java中的接口之间可以实现多继承，也可以实现多实现。但是java中的类只能实现单extends继承

/** 3000电梯  TB_ELEV_PARA  电梯技术参数表,电梯才会有维保单位的，其它类设备都没有。
 * 计费需要的参数，修改需要严格审批的参数，常见的要求立刻统计的参数{OLAP非实时统计参数非常规统计参数都可除外}。
 * 参数放在那里：看是否需要后端服务器java普通的快捷访问而定，若放在非结构化JSON数据中，前端访问没问题，后端很难操作。
 * 老旧电梯评估？算特别业务类型{监督检验是用户发起的，定期一般法定时间有周期的，委托是自愿；这个有点强制但是非定期的非用户发起是监管主动发现的}=临时统计抽取处理标记。
 */

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Elevator  extends Eqp {
    /**IF_SPEC_EQP特种电梯； IF_SPEC_EQP 是否特殊设备
     * 特种电梯，加收30%; 总共才10台啊，而且旧平台还是放在Eqp表
     * 其它种类都没用到该字段 Eqp.IF_SPEC_EQP 是否特殊设备
     */
    private Boolean spec;
    /**IF_UNNORMAL是否非标电梯， ？非标 和 特种不一样概念？技术上的非标，特种电梯是使用范围上的。
     * 总共才7台啊， 是 否 / null， 默认=不是非标的电梯。
     */
    private Boolean nnor;
    /**是否属于旧楼加装电梯 合并字段 ？。IF_OLDBUILD ？ 旧楼房加装,   Eqp.IF_OLDBUILD_INST旧楼加装;
     * 计费优惠依据 ，数量不多
     * 不同概念！ 是否老旧电梯：IF_OLD_DT，是否老旧电梯评估 IF_OLDDED_DT_PG_q
     * */
    private Boolean oldb;

    /**ELEFLOORNUMBER电梯层数，计费*/
    private Short  flo;

    /**RUNVELOCITY运行速度 ，米/秒 运行速度(m/s)"RUNVELOCITY"
     */
    private Float  vl;

    /**ELEHEIGHT3000提升高度*/
    private Float  hlf;
    /**SLIDWAY_USE_LENG 3300人行道使用区段长度，计费有用
     * 人行道使用区段长度（自动人行道）单位是(m)"SLIDWAY_USE_LENG"*/
    private Float  lesc;
    /**名义宽度(自动扶梯/自动人行道)(mm)"NOMI_WIDTH", 有可能被当成收费依据;
     *旧平台类型是字符串： "/" ，有些是 米 做单位的。实际可能扩充定义：电梯最大可开启的面宽方向宽度(能塞得进来)。
     * */
    private Float  wesc;

    /**控制屏型号"CONSCRTYPE" CON_SCREEN_TYPE
     * 有可能被当成过滤常规统计依据;
     * */
    private String  cpm;
    /**控制屏出厂编号"CONTSCRCODE"*/
    //private String  cpi;
    /**曳引机型号"TRACANGTYPE"
     * 有可能被当成过滤常规统计依据;
     * */
    private String  tm;
    /**曳引机出厂编号"TRACANGLEAFACNUMBER" 有重复的*/
    //private String  tno;
    /**电动机(驱动主机)型号"ELEC_TYPE"*/
    private String  mtm;
    /**电动机(驱动主机)编号"ELEC_COD"*/
    //private String  mtno;
    /**缓冲器形式"BUFFER_MODE"
     * 19个取值。 前端Enum{后端不管，当成字符串，前端控制输入列表，初始化数据要校对Enum处理}
     * */
    private String  buff;

    /**额定载荷(kg)"RATEDLOAD"
     * 整数，行业惯例单位是 kg;
     * */
    private Integer rtl;

    /**加装的附加装置，是否加装附加装置"IF_ADDDEVICE"
     * 新 增加的字段, "/" 否，"自动平层装置" "IC卡和能量反馈装置"
     * */
    private String aap;
    /**轿厢意外移动保护装置型号"CAR_PROTECT_TYPE"*/
    private String  prot;
    /**开门方式"DOOR_OPEN_TYPE"
     * 常用7种，+不常用48种; 前端Enum方式处理;
     * */
    private String  doop;
    /**限速器型号"RESTSPEEDTYPE"
     * "DS-6SS1B(轿厢侧)；DS-6SS1B(对重侧)"
     * */
    private String  limm;
    /**控制方式"CONTROL_TYPE"
     * 常用7种，+不常用46种; 前端Enum方式处理;
     * */
    private String  opm;

    /**最后一次制动实验时间LAST_BRAKE_TASK_DATE  IF_BRAKE_TASK, --是否制动实验
     * 最后一次制动试验日期,  是否制动试验IF_BRAKE_q= LAST_BRAKE_TASK_DATE is null
     * 老旧电梯评估会修改
     * */
    @Temporal(TemporalType.DATE)
    private Date lbkd;
    /**下次制动实验时间NEXT_BRAKE_TASK_DATE  电梯才有的字段
     * 老旧电梯评估会修改 IF_OLDDED_DT_PG ,是否老旧电梯：IF_OLD_DT作废?, 日期+预期寿命,电梯不看寿命只认定检结论的;
     * 定检时刻 确认老旧电梯评估的实际需求。
     * */
    @Temporal(TemporalType.DATE)
    private Date nbkd;

}


//不可改技术参数：控制屏出厂编号"CONTSCRCODE"控制屏型号"CONSCRTYPE"电梯层数"ELEFLOORNUMBER"电动机(驱动主机)型号"ELEC_TYPE"缓冲器形式"BUFFER_MODE"额定载荷(kg)"RATEDLOAD"是否加装附加装置"IF_ADDDEVICE"
// 人行道使用区段长度（自动人行道）(m)"SLIDWAY_USE_LENG"名义宽度(自动扶梯/自动人行道)(mm)"NOMI_WIDTH"轿厢意外移动保护装置型号"CAR_PROTECT_TYPE"电动机(驱动主机)编号"ELEC_COD"
// 运行速度(m/s)"RUNVELOCITY"曳引机型号"TRACANGTYPE"曳引机出厂编号"TRACANGLEAFACNUMBER"开门方式"DOOR_OPEN_TYPE"限速器型号"RESTSPEEDTYPE"控制方式"CONTROL_TYPE"
//@NoArgsConstructor缺少导致 No default constructor for entity:  : md.specialEqp.type.电梯
/*
* 电梯才有的字段 Task.IF_BRAKE_TASK, --是否制动实验, 任务根据年限决定是否制动；
北京市政策：对于投入使用年限不满10年的电梯(公众聚集场所使用的电梯除外)和杂物电梯，经具有一定规模的维保单位申请，检验检测方式调整为1年定期检验后，
* 次年转变为自行检测。为了确保安全可靠，在自检92项定期检验外，再增加30项自检项目，其中有24项为严于国家标准的企业标准。再由检验机构对自行检测结果
* 实施不低于5%的抽查比例。
* 使用单位委托电梯检验机构或型式试验机构对老旧电梯进行安全评估，确定电梯更新改造大修方案和继续使用条件。
* 福建、杭州、深圳等地电梯安全管理办法都要求，电梯使用年限达到15年时，应进行安全风险评估，根据评估结论确定继续使用电梯的条件或对电梯进行修理、改造、更新。
* 老旧电梯安全评估报告会给出具体的维修意见，但报告只是可行性建议，没有强制力，主动权还是掌握在小区业主、物业手里。不过，对于不按照意见执行的使用单位，
* 省特检院将通过缩短年检周期、增加日常维保频率保证电梯正常运行，而对于年检不合格的电梯，将发具有强制力的整改通知书，整改不合格，则只能关停。
* 电梯锅炉不能在监察首检设备录入新增。
*/

