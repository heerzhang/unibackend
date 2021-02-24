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
@SuperBuilder(toBuilder=true)
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
     * 人行道使用区段长度（自动人行道）单位是(m)"SLIDWAY_USE_LENG"
     * Float 误差！ 精度很低 6位数都无法确保精确的,5位数可保。
     * */
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
     * 旧平台是: '是否加装附加装置'：
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
    //按照品种；1常用都输入的程度，2不常输入的则是再按照汉字顺序 排序一起。 常规统计过滤，计费，后端的控制 不会用到这些参数；
    /*CAR_UPLIMIT_MV 轿厢上行限速器机械动作速度(m/s)： 类似这样参数在注册时给人观摩的，出报告可能用到，检验员查询资料可能用，就如图纸，设备部件参数。
    DIP_ANGLE 倾斜角度(自动扶梯/自动人行道)：
    SAFECLAMTYPE 安全钳型号： 几百种， ？出问题才需要抽取做过滤和排查安全的--业务需求？。OLAP方式,突发特殊定制的作业。
    SAFECLAMNUM 安全钳编号：
    FB_SUBSTANCE 爆炸物质(防爆电梯)： 周围存在的气体。
    COMPENTYPE 补偿方式
    FLOORDOORTYPE 层门型号
    BOTTOMDEPTH 底坑深度(m)
    ELECTROPOWER 电动机功率
    ELEC_STYLE 电动机类型， 重新定义了吗；实际数据填写成了 重复定义字段?:电动机(驱动主机)型号"ELEC_TYPE"
    ELEC_REV 电动机转速："960" "1201-1462" "1000/250" "127.3" "960/1150"，不用规整分解变成多个属性/复合字段！业务需求驱动？不要求做过滤比较大小的=仅仅就是给人看的备注形式。
    ELEDOORNUMBER 电梯门数
    ELESTADENUMBER 电梯站数
 -  ELEWALKDISTANCE 电梯走行距离(m) ？极度少条，弃用？
    TOPHEIGHT 顶层高度(m)
    TOP_PATTERNS 顶升形式(液压电梯)
    COUNORBTYPE 对重导轨型式
    COUP_ORB_DIST 对重轨距
    COUP_NUM  COUNTERAMOUNT 对重块数量 ,明明数字非得做成文本形式字段;"26块" "29+2" "高度：1.070m" "厚34薄9"
    COUP_LIMIT_COD 对重限速器编号  不是唯一的序号 "SG1508352；SG1508345"。
    COUP_LIMIT_TYPE 对重限速器型号
 -   RATINGVOLTAGE 额定电流(A) ？极度少条 ,规整意图的 =新添加的字段?
    RATED_CURRENT 额定电流(A)： "26.5" ，少数有复合形式的字段 "24/20" "18/17"
 -   RATINGCURRENT 额定电压(V) ？极度少条
    RATED_PEOPLE 额定载人 额定载人数 ："2000" "1050" "13" "21"  混淆变成 额定载荷(kg)？已经有独立字段。 人数还是kg啊?
 -   PREVENT_SETTLEMENT 防沉降组合 ？极度少条
 -   LADINCANGLE 扶梯倾斜角 ？极度少条
 -   WORK_LEVL 工作级别 ？极度少条
  -  MANAGEMODE 管理方式
    BUFFERNUMBER 缓冲器编号 '1408607;1408610'
    BUFFERTYPE 缓冲器型号
    BUFFER_MAKE_UNT 缓冲器制造单位 ?文本就可以; 2个名字拼凑:"镇江朝阳机电科技有限公司;上海优意工业设备有限公司" "ACLA-WERKEGMBH" "沈阳东阳聚氨酯有限公司" "江阴市聚丰电梯配件有限公司；杭州沪宁电梯部件股份有限公司" "德国纽伦堡"
    CAR_HIGH 轿厢高(杂物电梯)(m) 实际大多是mm单位
    CAR_WIDTH 轿厢宽(杂物电梯)(m) 实际大多是mm单位
    CAR_DEEP 轿厢深(杂物电梯)(m) 实际大多是mm单位
    CAR_ORB_DIST 轿厢轨距 mm;
    CAR_UPLIMIT_EV 轿厢上行限速器电气动作速度(m/s); 两个取值复合？采用文本类型 "1.20-1.45" "1.21/1.45" "0.65"
    CAR_UPLIMIT_MV 轿厢上行限速器机械动作速度(m/s) "2.22-2.27"
    CAR_DOWNLIMIT_EV 轿厢下行限速器电气动作速度(m/s)
    CAR_DOWNLIMIT_MV 轿厢下行限速器机械动作速度(m/s) "(119m/min)1.98"  ? 这4个字段应该是历史遗留取值问题。
    CAR_PROTECT_COD 轿厢意外移动保护装置编号 可重复序列号 "/"代表没有意义吗？【上级装置没有】， 空缺="" ？="不明"
    CAR_PROTECT_TYPE 轿厢意外移动保护装置型号
    CAR_DECORATE_STA 轿厢装修状态
  -  SAFE_DOOR 井道安全门(液压电梯)： "无" "是" "有"
  -  DOOR_OPEN_DIRCT 开门方向(杂物电梯) ,取值雷同 ‘开门方式’
    LOCK_TYPE 门锁型号(液压电梯)： 不见得都是液压电梯？
    FB_AREALEVEL 区域防爆等级(防爆电梯）：
    DRIV_APPROACH 驱动方式(杂物电梯)：
    UP_PROTECT_MODE 上行保护装置形式： 遗留数据有的带编码
    UP_PROTECT_MODEANDTYPE 上行保护装置形式/型号： 上行保护装置型号； 两个字段合并输入的。
    UP_PROTECT_COD 上行超速保护装置编号： "SG1336798;↵↵SG1336805"
  -  UP_PROTECT_TYPE 上行超速保护装置型号：  ？雷同 上行保护装置型号
    UP_RATED_V 上行额定速度(液压电梯)(m/s)：
  -  DESIGNCRITERION 设计规范： "GB7588-2003"
    IF_SHIP 是否船舶电梯： 总共就4台
    IF_PUB_TRAN 是否公共交通型： "非公共交通型"
    IF_CAR 是否汽车电梯：

    * */
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

