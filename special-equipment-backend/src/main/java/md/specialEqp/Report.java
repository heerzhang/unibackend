package md.specialEqp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.computer.File;
import md.specialEqp.inspect.Isp;
import org.fjsei.yewu.filter.SimpleReport;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/*
主报告，分项报告 REP_TYPE, 证书或组合第一页的提纲形式{点击进入很多子报告}。非结构化json转成文档。
实体类不可搞 interface： ？可能死循环, 返回结果集只能使用接口/不能做实体转换。
 @Id采用GenerationType.SEQUENCE,共用sequenceName要确保旧数据失效清除周期一致，ID若要循环到最大极限值回到1起点后了若还有小数字ID就麻烦了。
 mySQL修改Id自增起点：  select next_val as id_val from SEQUENCE_COMMON  for update；   update SEQUENCE_COMMON set next_val= ?  where next_val=?
 Hibernate提供@GenericGenerator(strategy = "uuid")不能用，Long与String不兼容；只好麻烦点，旧数据维护要看id设置找底层数据库支持修改next_val:initialValue。
 @Lob字段小心：可能造成见建表失败，mysql和Oracle还表现不一致。
*/

/** 原始记录+报告的数据，快照信息。
 * 分项报告或主报告{目录页报告内嵌在主报告上}，每种REP_TYPE都不同的。
 * 流转审核打印等人员状态日期。
 * 实际对应旧平台TB_TASK_TO_ISPPROJ{派工关联和关键信息,MAIN_FLAG主报告标识1：主报告;0分项报告，REP_TYPE检验项目}
 * 每个主子报告都有，TB_ISP_SUBPROJ实际等同于TB_ISP_MGE主表的！，分项子报告/主=分离继承/组合显示，把流转独立。
 * 流程实际该放这里：WF_TODO/TB_ISP_SUBPROJ.FLOW_IMPCOD/WF_FLOW_IMP.FLOW_IMPID加流程；
 * 旧平台TB_ISP_DET实际该放这里的检验时刻参数。
 * 设备eqp；协议prot；任务task；账务记录actr；业务记录isp；报告report; 管道单元plunit。
 * task: eqp =1: n/0;  无关联设备的业务；
 * prot: task=1: n;  单个 task 业务类型单一，检验大部门归属单一个。
 * actr: task=n: n;  单个 task还可以分裂开多条的账务记录。
 * isp: eqp =n: 1;  单个task发起的isp一次只能分别为每一个eqp分配有单独的isp，也就是isp只能做1个eqp或无关联eqp;
 * isp: report=1: n; 报告/分项报告/证书；report也可独立流转审批，isp有总的审批。
 * plunit可独立多选选择；单1个 task 业务可挂接多个管道eqp，每一条管道还可以分别选择其中部分的plunit。
 * 管道单元设备表pipeEqpunit会登记3个业务(监督检验、全面检验，在线检验)上一次的Isp用以过滤管道单元选择；isp关联eqp但是对于plunit不做关联；
 * 管道单元未终结Isp: 当前管道单元最新已生成task关联关系表[任务锁], 任务锁主人=task,直接在pipeEqpunit实体增设'当前task'=任务锁；
 * 有了某个plunit的业务opetype，就锁定新增加任务的源头，任务完成才能对管道单元解除新增任务锁。
 * report批准了[再也不能改报告了] -> 账务结清确认[催收] -> isp终结确认[发送凭证快递]  -> 任务完成确认[单task多个eqp都完成了]->管道单元任务锁解除 。
 * report形式可多样，web网页内容链接的报告，上传文件存储pdf/excl/doc/图片报告，允许isp没有关联实际report仅提供报告号和来源地文字说明或外部html链接;
 * report :上传文件File关联 =1:N。 把isp作为监察视察的入口对象。
 * 数据和文件的淘汰删除时机：从关联度底的实体开始清理，重要性程度，过期时间多长。
 */

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Report  implements SimpleReport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //OPE_TYPE配合BUSI_TYPE法定1/=2委托业务的；来敲定的报告类型REP_TYPE
    //而检验范畴ISP_TYPE可以省略掉：机电 承压类->只是给科室分配/发票会计用，挑选列表大的分类大归类的/统计上分家。
    private String type;
    private String  no;

    private Date upLoadDate;
    private String path;
    //TODO: 多次ISP汇集做一份报告Report?  管道单元有多对多，管道单元还有内部单元编码？管道单元忽视ISP的标识?
    //单次ISP如果多个报告，每个报告单独打印，单独编制特定编号的报告，单独链接；主报告1+N。
    @ManyToOne
    @JoinColumn
    private Isp isp;   //1个检验可以有很多份子报告，报告类型可以不同的。

    private double  numTest;    //测试表达式

    //测试：高保密性质的2个扩展字段；
    private String  sign;
    private String  detail;
    private String  modeltype;
    private String  modelversion;
    //CLOB字段 会导致Hibernate无法自动创建该表？ 手动修改 建初始化表。
    //原始记录内容-JSON；在前端录入和修改的部分。Oracle是这样@Column( columnDefinition="CLOB")
    //字段长度，mysql8.0　数据库要修改定义成 clob
    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column( columnDefinition="TEXT (64000)")
    private String  data;
    //该部分数据-JSON，在编制后提交审核时就能固定化了。可直接复制合并到data，存snapshot仅是接口对接便利的过渡工具。
    //纯粹是后端提供给检验报告的，编制报告的那一时间的相关设备状态数据。接口对接复制完成后就可清空了。
    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column( columnDefinition="TEXT (64000)")
    private String  snapshot;
    //TEXT,MEDIUMTEXT,LONGTEXT三种不同类型，BLOB和TEXT大量删除操作性能有影响。建议定期使用OPTIMEIZE TABLE功能对表碎片整理。

    /**关联的 单线图等文件。
     * */
    @OneToMany(mappedBy="report" ,fetch = FetchType.LAZY)
    private Set<File> files;

    public  Report(String type, String no, Isp isp){
        this.type=type;
        this.no=no;
        this.isp=isp;
        //初始化
        data="{}";
    }
    //重载是依靠参数类型以及个数和顺序来确定的。
    public  Report(String path, Isp isp, String no){
        this.path=path;
        this.no=no;
        this.isp=isp;
        data="{}";
    }
    //graphql选择集　安全信息控制方式： 单个字段的。
    //@PostAuthorize对实体类不起作用，对行动类有效？    @PostAuthorize("hasRole('ADMIN')") !;
    /*public Date getUpLoadDate() {
        long rights = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().filter(authority ->
                authority.equals(new SimpleGrantedAuthority("ROLE_cmnAdmin"))
                || authority.equals(new SimpleGrantedAuthority("ROLE_Ma"))  ).count();

        if(rights>0)
            return upLoadDate;
        else
            return null;      //这样就切断了graphQL选择集，前端无法查询该字段也无法嵌套。
        //没有登录的客人也有程序角色："ROLE_ANONYMOUS"；
    }
    */
    //JPA实体从数据库装入的实例化过程，根本就没有运行这个setter()/getter();
    /*@PreAuthorize("hasRole('ADMIN')")
    public void setUpLoadDate(Date upLoadDate) {

          this.upLoadDate = upLoadDate;
    }*/
    //内省执行这个函数之前，在graphql/tools/MethodFieldResolver.kt:70里面就已能看到了本条记录的所有相关数据了{含同级的字段}。
    public Isp isp(Long id){
        return this.isp;
    }
}


