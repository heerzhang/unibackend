package md.specialEqp.inspect;
//任务单，检验的业务起始点。

import lombok.Getter;
import lombok.Setter;
import md.cm.unit.Unit;
import md.specialEqp.Eqp;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

//旧平台1个TASK映射1个ISP{可以多分项SubISPid}；Isp流转后出报告内容。
//这里放置每次作业时刻定夺动态的参数。
//如果两台同时进行检验的，按收费总额的90%收(单一次派工出去的/单任务?同种设备数) 开发票INVC 一对多 Task; 如何确认证明呢/处理进度若不一样/那个环节谁可以。
//TODO: Isp Task 关系哦 1:N? 1:1?  多分项报告 WF_TODO;
//部门分配给单OFFICE_ID;科室分配{单个=组长？PROJ_USER_ID},=> 派工;责任人PROJ_USER_ID,任务负责人==.E_PROJ_USER_ID'任务负责人==责任工程师==负责人;编制人属于检验人之一。
//派工人ASG_USER_ID:可以是?编制人或责任人;ISP_USER编制/检验人JY_MEN，审核PROJ_USER_ID CHEK_USER_ID：等级较高=科室主任；批准APPR_USER_ID：业务上算最高级别=中层干部。
//部门分配科室分配派工人竟然全是编制人自己？奇葩。 编制人{写报告的人}属于检验人员之中主要人员。

/**生成检验需求的作业工单，预设定收费参数。
 * 开发票相关。合同和协议申报相关。
 * 聚合型任务参数字段：若每个子任务实际不一致的就不要设置，看实际情况。
*/

@Getter
@Setter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Fast")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    /**聚合型任务，关联多个独立子任务。聚合型任务
     * 基本型任务=旧平台任务 multi=false，无关联更多Task, 若是聚合型任务，可自关联多个Task。
     * 统计过滤可分别针对基本型/聚合型
    */
    private Boolean  multi; //自关联=null? Set<BaseTask>

    //todo: 1个任务只有1个设备,没有关联设备，虚拟设备（煤气罐系列化的设备号01..09排除05}），管道特别[选择几个单元为了报告/计费]。
    /**Eqp 聚合型任务，关联多个独立子任务。
     * 旧平台任务 multi=false，@ManyToOne，每个基本型任务单个设备
     * 若是聚合型任务，@ManyToMany。 也可以省略掉关系，简洁通过关联多个Task做延伸关联。
     */
    //Task单独报告的可没有设备关联；
    //我是关系维护方,必须在我这save()，　缺省.LAZY;
    @ManyToMany
    @JoinTable(name="TASK_DEVS", joinColumns={@JoinColumn(name="TASK_ID")}, inverseJoinColumns={@JoinColumn(name="DEVS_ID")}
                //indexes={@Index(name="DEVS_ID_idx",columnList="DEVS_ID")}
            )
    private List<Eqp> devs;
    //管道检查　监察的处理较为特殊，可分解开成多个任务单, 1 Eqp配合 多 pipeUnit两个实体分解，需要选定详细的多个管道单元。
    //todo:同一批次多台设备的收费优惠政策。如何证明：查验审核。 归属哪一个泛型类任务，同属一个父任务『关联子任务』？
    //todo：子设备列表-管道单元的选择列表。管道特别：每个单元下次定检日期都不一样(可分开多批次的任务)。

    //对方ISP去维护这个关联关系。
    //一个任务单Task包含了多个的ISP检验记录。 　任务1：检验N；
    //默认fetch= FetchType.LAZY; 而mappedBy代表对方维护关系  EAGER
    /**单个基本型任务涉及单一个设备{管道}， Isp业务流转/次检验工作，
     * todo: 关系模型？ <BaseTask> 实际应该归并给Isp?
     * 单个Isp只能有单个Eqp;
     * 可能不同的业务作业类型OPE_TYPE? ,不同OPE_TYPE有各自的报告结论证明? 只会有唯独一个OPE_TYPE，所以Isp唯一:@OneToOne
     *  */
    @OneToMany(mappedBy = "task" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<Isp>  isps;

    private String dep;  //类型弱化? ,应该是部门表的ID!

    //ORACLE: 表名USER不能用，字段date不能用。
    /**任务日期多设备必须相同一致，否则多台设备如何算一个批次计费*/
    @Column(name="TASK_DATE")
    private Date date;
    private String status;
    private String  fee;

    //[两个归并字段]只是前端显示区别；TB_TASK_MGE.IF_HOLD_TEST'4000是否载荷试验'？
    private Boolean test;   //.IF_WORKEQP_TEST '5000厂车是否工作装置测试'
    private Boolean verif;  //.IF_AQ_TEST '4000是否安全监控管理系统试验验证'

    private Float cost;  //TB_TASK_MGE.INST_PRICE单台工程施工费（单位:万元）

    //todo:在化学介质、易燃介质等毒性危害和爆炸危险环境下进行检验作业或接触生产性粉尘作业危害程度在II级（含II级）及II级以上环境下进行检验作业的加收30%；
    //设置加急，发起人？验证时间截至期限。
    //加急检验（三个工作日取检验报告）加收20%; 条款？        =? 影响Isp流转催促/审核等环节了。

    //廉租住房、公共租赁住房、经济适用住房和棚户区改造安置住房等保障性安居工程项目免收行政事业性收费和政府性基金=扯呼; 临时起的帽子，属性? 例外申请途径/批准配置。

    //是否使用年限到期了
    //如何断定设置的/批量初始化、检验触发的？【日期+预期寿命】 是否老旧电梯：IF_OLD_DT 是否老旧电梯评估 IF_OLDDED_DT_PG
    //是否老旧电梯：IF_OLD_DT 是否老旧电梯评估 IF_OLDDED_DT_PG_q 检验历史上做了，延长EXTEND_USE_YEAR?或判定不合格/改造大修;

    //类型： 常规任务
    //超出整改反馈期后复检任务自动终结；
    //BUSI_TYPE 法定1/委托业务性质 BUSI_TYPE=2委托的；仅用于区分报告的格式内容以及收费，
    //ISP_TYPE{检验范畴1机电2承压3}， 仅用于区分报告的链接菜单入口{列表框的大一层级分类}大类科室分配和收费会计中心；

    //TB_TASK_MGE.INST_PRICE 单台工程施工费（万元）

    /**CONS_UNIT "施工单位"土建施工单位 检验的报告才用到的；
     * 编制检验报告，要选择配套的施工单位。
     * BUILD_UNT_ID 业务申请中的施工单位 (监检)安装|改造|维修的融合称谓，SDN用的BUILD_UNT_ID，监察施工告知用；
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit constu;
}


/*
如果是EAGER，那么表示取出这条数据时，它关联的数据也同时取出放入内存中;用EAGER时，因为在内存里，所以在session外也可以取。
如果是LAZY，那么取出这条数据时，它关联的数据并不取出来，在同一个session中，什么时候要用，就什么时候取(再次访问数据库)。
但在session外就不能再取了,failed to lazily initialize a collection报错；因为操作实际被多场次的数据库session分开。
查询一个graphQL按策略执行器（因为性能要求）＝＝〉并发分解的和异步的策略＝＝〉导致分配到了多个数据库session了。
*/

/*
下次检验日 {预设定大小保养时间},检验等级规模。检测记录，检测规定时间。
if等级1/3/的，耐压试验6年{2.5年}一次。https://wenku.baidu.com/view/369ebf72760bf78a6529647d27284b73f3423615.html
*/
