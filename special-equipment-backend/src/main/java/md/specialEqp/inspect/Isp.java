package md.specialEqp.inspect;

import lombok.Getter;
import lombok.Setter;
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
//如果两台同时进行检验的，按收费总额的90%收(单一次派工出去的/单任务?同种设备数)； 多个Isp捆绑审核，捆绑进度条/复检呢，一起做;自由裁决权。

/**检验工作的流转审查，本次检验工作的关键记录(详细数据除外)，监察关心的关键字段。
 * 本次检验决定的结论/法定应当的下次检验日期。
 * 对接旧平台/外部检验系统，历史报告检验/历史记录。
 TB_ISP_MGE像个大杂烩？TB_ISP_DET参数，报告和流转,主要是记录状态的，DATA_PATH代表报告纸,Isp代表作业成果。
 * */

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
    //先有EQP,后来规划TASK了(1个task对1个eqp)，最后才为某task和某一个EQP去生成ISP{inspect}的;
    //一个检验ISP记录只有一个设备，一个设备EQP可有多个ISP检验。
    //检验单独生成，TASK和EQP多对1的； ISP比Task更进一步，更靠近事务处理中心。
    //单个ISP检验为了某个EQP和某个TASK而生成的。主要目的推动后续的报告，管理流程，等。
    //todo:若是ISP该从TASK挂接关系而来的，本来这里就不应该有EQP字段的，设备在TASK 哪去找，多余的字段?。Task是部门细分责任的。
    //我是多端我来维护关联关系，我的表有直接外键的存储。
    /** 单个Isp只能有单个Eqp;
     * todo: <BaseTask> 实际应该归并给Isp?
     * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dev_id")
    private Eqp dev;

    //单个检验记录规属于某一个TASK底下的某次;
    //一个任务单Task包含了多个的ISP检验记录。 　任务1：检验N；
    //我是多的方，我维护关系，我方表字段包含了对方表记录ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    //缺省 fetch= FetchType.LAZY  ；多对多的实际都派生第三张表，不会和实体表放在一起的；
    //这地方维护多对多关系，版本升级导致中间表ISP_ISP_MEN变成ISP_USERS；？需要自己指定表名,且字段名都也改了"ISPMEN_ID"　ISP_MEN_ID？
    @ManyToMany
    @JoinTable(name="ISP_ISP_MEN",joinColumns={@JoinColumn(name="ISP_ID")},inverseJoinColumns={@JoinColumn(name="ISP_MEN_ID")})
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<User> ispMen;       //= new HashSet<>()
    //审核人员就一个
    //@ManyToOne(fetch = FetchType.LAZY)       //？分开的sql语句;
    @ManyToOne
    @JoinColumn
    private User checkMen;

    private Date    nextIspDate;
    private String  conclusion;
    //缺省, fetch= FetchType.EAGER
    //有可能Report的实际数据库表还没有创建啊;
    //比旧平台多出个Report实体，旧系统是直接用Isp表。多个分项报告REP_TYPE;
    //todo: 1个主/组合封面/报告，+0个或多个分项报告,带了顺序链接，打印物理页数？。
    @OneToMany(mappedBy ="isp")
    private Set<Report>  reps;
    //private Set<BaseReport>  reps;
    //改成安全的基础接口类－报错hibernate.AnnotationException: Use of @OneToMany or @ManyToMany targeting an unmapped class


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

}



//变量名只能是字母a-z A-Z，数字0-9，下划线_组合，不能包含空格，数字不能放在变量名首位,不能用语言的保留字;
//分项报告【" + REPTYPE2 + "】已流转到单人审核，请审核后并流转到 责任工程师组合 再流转;

/*
*一个设备号可以有几个Task{1:1 Isp}同时进行中;
*有分项就有多子SubISPid/多REP_TYPE子报告{独立WF_TODO分项流转，单独结论}。报告模板规范当中已经明显看出是分开的子报告，纸质组合排版的。
*分项报告类型；分项单独编制的分项报告必须是103状态，主报告才可以流转到审核；
 */

