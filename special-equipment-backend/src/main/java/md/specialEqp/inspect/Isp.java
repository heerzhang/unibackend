package md.specialEqp.inspect;

import lombok.Getter;
import lombok.Setter;
import md.cm.unit.Unit;
import md.specialEqp.Eqp;
import md.specialEqp.Report;
import md.system.User;
import org.fjsei.yewu.filter.SimpleReport;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//一个设备ID+一个任务ID只能做一个ISP（正常的ispID;作废删除的记录资料另外再做信息追溯）
//唯一性索引UniqueConstraint不一定生效，必须确保现有表中数据都满足约束后，JPA才能成功建立唯一索引！。
//针对graphQL关联查询的安全问题，考虑把关对其他或低权限用户不可见的字段独立出来，JPA　三种继承映射策略。
//比如ISP extends ISPbase{ISPbase字段低保密性}，ISP可额外添加保密性要求高的其他字段，代码配置graphql返回对象类型正常都用ISPbase替换ISP。
//责任工程师审核：同时收费.Task->INVC明细金额最终确认人{收钱不合理退回}；CURR_NODE= [{id:'101',text:'报告编制'},{id:'102',text:'责任工程师审核'}；

/**业务记录， 监管或追溯历史的检验检测工作情况。
 * 初始化状态=其实也可看做 每个Eqp子任务。
 * 检验工作的流转审查，本次检验工作的关键记录(详细数据除外)，监察关心的关键字段。
 * 本次检验决定的结论/法定应当的下次检验日期。
 * 对接旧平台/外部检验系统，历史报告检验/历史记录。
 TB_ISP_MGE像个大杂烩？TB_ISP_DET参数，报告和流转,主要是记录状态的，DATA_PATH代表报告纸,Isp代表作业成果。
 * 分项报告|主报告的流转审核打印等人员状态日期：直接放到Report模型中；
 *制造监检考虑从Isp中恢复已有的数据来复用。制造监检没有关联设备，但有单位,业务类型，附带设备类别指示就是没有Eqp,出厂编号声明范围。
 * Isp初始化时机提前了，Task任务指定设备时刻就要生成新的Isp,所以回转余地较大，实体模型含义有所变化。
 * 制造监检和水质报告这样没有Eqp的如何还原报告证书的真伪？只能从报告号和证书序列号来追溯了,Isp.repNo来定位。
*/

@Getter
@Setter
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"dev_id", "task_id"})} )
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Fast")
public class Isp {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    /**OPE_TYPE
     * 业务类型 OPE_TYPE：统计？模板？ TB_DICT_OPETYPE似乎没内涵36个压缩到实际28个/委托法定=
     * 法定还是委托的 是 附加属性。
     * Task底下可能为一个Eqp只能派工唯一一个业务类型(一个法定的，一个委托的)，Task底下Eqp具有唯一性。
     * 同一个Task底下的Isp的业务类型OPE_TYPE必须相同，都准备派工给同一个责任人的。
     */
    private String bsType;

    /** 先有EQP,后来规划TASK了(1个task对1个eqp)，最后才为某task和某一个EQP去生成ISP{inspect}的;
    //一个检验ISP记录只有一个设备，一个设备EQP可有多个ISP检验。
    //检验单独生成，TASK和EQP多对1的； ISP比Task更进一步，更靠近事务处理中心。
    //单个ISP检验为了某个EQP和某个TASK而生成的。主要目的推动后续的报告，管理流程，等。
    //我是多端我来维护关联关系，我的表有直接外键的存储。
    //改成Equipment dev报错 @OneToOne or @ManyToOne on md.specialEqp.inspect.Isp.dev references an unknown entity:
    单个Isp只能有单个Eqp; 也可能没有挂接Eqp的。
     * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dev_id")
    private Eqp dev;

    /**任务很可能已经过期或清理掉，历史数据。
     * 单个检验记录规属于某一个TASK底下的某次; 单一个设备执行多次作业活动要跑很多趟的，很多人不同场次做的作业的，当成一次。
    一个任务单Task包含了多个的ISP检验记录。 　任务1：检验N；
     同一个Task底下每个Eqp不能重复保证唯一性，没有挂接Eqp的？只能最多一条Isp;
    //我是多的方，我维护关系，我方表字段包含了对方表记录ID
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    /**缺省 fetch= FetchType.LAZY  ；多对多的实际都派生第三张表，不会和实体表放在一起的；
    //这地方维护多对多关系，版本升级导致中间表ISP_ISP_MEN变成ISP_USERS；？需要自己指定表名,且字段名都也改了"ISPMEN_ID"　ISP_MEN_ID？
    业务类型不同，同一个设备和可能需要派出不同的人去做，主要抓责任人，让责任人灵活配置各种人员，以及分配工作成果和权限。
    */
    @ManyToMany
    @JoinTable(name="ISP_ISP_MEN",joinColumns={@JoinColumn(name="ISP_ID")},inverseJoinColumns={@JoinColumn(name="ISP_MEN_ID")})
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<User> ispMen;       //= new HashSet<>()
    //审核人员就一个
    //@ManyToOne(fetch = FetchType.LAZY)       //？分开的sql语句;
    @ManyToOne
    @JoinColumn
    private User checkMen;
    /**敲定下次检验日期xx1,xx2，管道只能针对本次牵涉到的单元。
     * */
    private Date    nextIspDate;
    /**总体结论，子报告有各自子结论
     * */
    private String  conclusion;

    /**母报告的 报告号，合格证编号。 证书和报告上一般都会有Eqp定位方法。
     * 没挂接Eqp的报告证书的如何还原真伪？只能从no,作为外部接入查询的关键字。
     * 有挂接设备的，一般走设备定位入口，延伸查询历史的Isp/报告。
     * 水质报告这样的可以在Unit.task找到关联Isp，若Task过去时间太久了已经清理掉，就只能依赖报告号。
     * 制造监检这样的第三方想查询历史上的合格证证明，估计就得直接用合格证编号在Isp匹配了。
     * @Id 某种规则生成的关键字,全平台Key;
     * 制造监检合格证编号是证明书自身的编码，而不是出厂产品序列号。
     */
    private String  no;

    /**工作成果输出：主报告，母+子形式的。
     * 只能一个入口, 多张证书这样平行关系不支持，改为多张 子证书。
     * 封面报告,json直接链接其它分项报告;
     * 单向关联关系，底下的Set<Report>  reps才是所有的子报告。
     * 冗余字段-快捷关联模式 ! ! ! 一个集合中特别指出当前最关注的那一条,免于数组[]嵌套。
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    private Report  report;

    /**做业务的成果输出。 检验检测都会有结论证明等。
     一个业务可能生成的 多个[母子]报告/合格证/手写证明{特别形式的文件/说明}， 主要目的是单独流转分项报告；
     * 缺省, fetch= FetchType.EAGER
     //有可能Report的实际数据库表还没有创建啊;
     //比旧平台多出个Report实体，旧系统是直接用Isp表。多个分项报告REP_TYPE;
     todo: 1个主/组合封面/报告，+0个或多个分项报告,带了顺序链接，打印物理页数？。
     前端页面在呈现Report报告内容json里面估计会提供关联Unit的,到底是发证书发报告给哪个单位的。
     包含封面报告 report;
     */
    @OneToMany(mappedBy ="isp")
    private Set<Report>  reps;

    //private Set<BaseReport>  reps;
    //改成安全的基础接口类－报错hibernate.AnnotationException: Use of @OneToMany or @ManyToMany targeting an unmapped class

    /**服务对象(使用单位)
     上级的Task也会设置关联单位的，【特别注意】两个冗余字段的更新同步。 Isp是长期储存，Task是短期储存。
     水质报告制造监检这样的，不依赖Task，也不依赖精确地输入报告号。还是能从服务对象(使用单位)搜索的，但是数据条数较多。
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit servu;

    //graphQL内省都是查询的getxxx;
    public Set<SimpleReport>  getReps() {
        Set<SimpleReport>  parents = new HashSet<SimpleReport>();
        if(reps!=null)
            parents.addAll(reps);
        return  parents;    //到了这里实际数据还是Report，并没有变化，变的只是输出或前端可看见的范围。
    }

    //IF_OTHERREREP  '已报检';
    //LAST_GET_DATE 更新发证日期
    //FS_EQP_COD 起重机械附属装置设备信息 TB_CRANE_UNIT;
    //COR_DATE 整改反馈日期 如果复检派工的时间小于等待整改反馈期，则取原来的报告号TO_CHAR(A.COR_DATE, 'yyyy-mm-dd') >= TO_CHAR(SYSDATE, 'yyyy-mm-dd')\n")
    //  '超期未整改'  机电类安装监检报告，首检报告，检验结论为不合格的，因报告中没有记录整改反馈日期，
    //EFF_DATE下次定检日期; INP_BEG_DATE监检开始日期
    //IF_BATCHMAKE 制造监检 按批出具的压力容器数量TB_ISP_INSPSTOP_MGE.EQP_NUM|TB_ISP_MGE.EQP_NUM;
    //EQP_NUM  制造 压力容器数量
    //CURR_NODE,OPE_TYPE,ISP_TYPE机电1,BUSI_TYPE法定1；
    //ASSINV_FALG 0,1,2,3发票关联状态非法 发票关联及审核标志 0未关联1关联未审核2关联已审核;如果有关联检验合同，且为制造监检 则无需关联收费清单;
    //TASKFEE_FALG 收费审核标志 -1=未查看过收费清单,
    //ISP_CONCLU
    //F_GET_TASKFEEBYTASKID(A.TASK_ID, 0) PRICE_CNT 报告是否有收费清单标志 报告收费清单状态,F_GET_TASKFEEBYTASKID(A.TASK_ID, 1) PRICE_TOTAL 报告收费清单金额;
    //LIMIT_UPLOAD_TYPE 作废字段？承压类如果是不合格数据，必须选择问题类型

}



//变量名只能是字母a-z A-Z，数字0-9，下划线_组合，不能包含空格，数字不能放在变量名首位,不能用语言的保留字;
//分项报告【" + REPTYPE2 + "】已流转到单人审核，请审核后并流转到 责任工程师组合 再流转;

/*
*一个设备号可以有几个Task{1:1 Isp}同时进行中;
*有分项就有多子SubISPid/多个独立REP_TYPE子报告{独立WF_TODO分项流转，单独结论}。报告模板规范当中已经明显看出是分开的子报告，纸质组合排版的。
*分项报告类型；分项单独编制的分项报告必须是103状态，主报告才可以流转到审核；
* 分项报告 子报告 IF_HAVESUBREP； TB_ISP_SUBPROJ{=Isp}  TB_TASK_TO_ISPPROJ{派工关联和关键信息,MAIN_FLAG主报告标识1：主报告;0分项报告，REP_TYPE检验项目}
 */

