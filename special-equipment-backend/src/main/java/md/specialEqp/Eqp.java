package md.specialEqp;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.cm.unit.Division;
import md.cm.unit.Unit;
import md.specialEqp.inspect.Isp;
import md.specialEqp.inspect.Task;
import md.cm.geography.Address;
import org.hibernate.annotations.CacheConcurrencyStrategy;
//import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//同名冲突！@Cache不是来自javax.persistence.*的，所以添加org.hibernate.annotations.Cache在其上方。或直接@org.hibernate.annotations.Cache上了。

//云数据库网关服务 支持跨DB实例SQL关联查询; 但是无法确保事务，数据一致性毛病？

//懒加载导致了N+1问题(按照后面逻辑代码需求再去那可能的会执行N条SQL)，假设不用懒加载的EAGER话就会强行加载可能根本就不会用到的大量的关联数据(不是更浪费?)。
//EntityGraph是和LAZY相反的？，总体写死掉策略搞lazy，动态的个性化查询用EntityGraph来提示{深度定制的,细化,仅针对个别使用到的字段的}，俩个机制的目标完全冲突。
//Lazy字段才需要搞@NamedEntityGraph的，嵌套Lazy字段/下一级Lazy属性字段用@NamedSubgraph。目的提前join取数据,减少sql语句数,能提高效率。不是FetchType.LAZY的就没必要@EntityGraph。
//举例@NamedEntityGraph(name="Eqp.task",attributeNodes={@NamedAttributeNode("task"),@NamedAttributeNode("isps")}) 关联不密切的关联对象一次性join=会产生爆炸记录数；
//EntityGraph不可随便加;就为了多出一个isps关联对象left outer join ？,对多出join约束性少引起笛卡儿积级别记录个数爆炸，本来只有290条变成12588条了。
//EntityGraph存在理由:提示JPA去屏蔽LAZY，用JOIN FETCH一次关联表全查，减少SQL语句(规避了1+N 问题)，从而提高速度；但也失去懒加载优点。https://blog.csdn.net/dm_vincent/article/details/53366934
//对于@NamedEntityGraphs({ @NamedEntityGraph每条定义尽量精简，不要太多字段，必须每一条/每一个接口都要测试对比/打印调试hibernate SQL。
//字段名称用了保留字导致表EQP无法自动建立！ 需手动创建最简单eqp表。 字段类型要用java包装类
//继承实体类做法：String=eqp.getDTYPE没有啊，无法在SQL获得派生类类型做过滤条件，不能在数据库直接过滤派生类类型。ElevatorRepository可只搜索本派生类，不灵活啊。


/**特种设备检验有8大类{目前实际7个}。
监察特有功能:许可证(单位/人员{监察/检验检测/无损检测/评审/协管/质保体系人员}/设备)；工作成效统计(作业人/监察人/考试机构{班级}/检验检测机构/检验人员/生产单位,整改率)，到期预警，
 证书到期预警；重要问题闭环，现场检查安排(指令书)；告知受理；检验相关审核(单位{安装改造维修/制造/设计/充装/检验机构/乡镇机构}变更/关键字段{使用单位}/设备状态变更/维保认领)。
 机构(评审/考试/培训)变更审核，作业人员聘用单位变更；设备<省外注册/迁出/流动设备/使用登记/首检录入/办证后管道新增确认>；许可{申请书}办证流程；考试审核，案件，诚信，维保准入。
不属于8大类的比如R000常压容器; 直接就是Eqp基类/没有独立参数表。
 */

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Medium")
public class Eqp implements Equipment{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    //JPA中，使用@Version来做乐观锁的事务控制。对比的,悲观锁限制并发所以很少被用的。
    //乐观锁同步用，［注意］外部系统修改本实体数据就要改它时间一起commit事务。@Version防第二类更新丢失；
    @Version
    private int  version;   //之前Timestamp类型ES过不了; 旧表H2还用timestamp

    /**EQP_USE_STA 状态码; 合并标记
     * 不能用保留字。private char  use ?　：若用了保留字导致表EQP无法自动建立！
     * 该条设备记录已被设置成了删除态不再有效，就等待以后维护程序去清理这些被历史淘汰的数据了。
     *     @NotNull
     * 合并删除标记   private Boolean valid=true;
    */
    @Enumerated
    private UseState_Enum   ust;
    /**EQP_REG_STA 注册状态
     * 不能用private char   在H2无法建，Character占2字节
     */
    @Enumerated
    private RegState_Enum   reg;
    //@PropertyDef(label="监察识别码")    数据库建表注释文字。
    //@Column(length =128, unique = true)

    /**OIDNO每一个省份监察机构自己产生的易识别码。 {@link Collectors}.*/
    @Field
    @Column(length =40)
    private String oid;

    /**EQP_COD设备号? 本平台自己产生的或照抄老旧平台产生的。
     * 对接旧平台的，新平台没意义
     * */
    @Field
    @Size(min = 4, max = 32)
    private String cod;

    //光用继承实体类不好解决问题，还是要附加冗余的类别属性；特种设备分类代码 层次码4个字符/大写字母 ；可仅用前1位、前2位或前3位代码；
    /**设备种类 EQP_TYPE{首1个字符} ,
     * 用实体类java类型区别，只是在服务器内存中区分操作的, if(eQP instanceof Elevator)
     *  ((Elevator) eQP).setxxx;，难道要把数据集全部搜到内存中来; 要直接要在数据库DB来设置SQL层次过滤？
     * 继承派生缺点，Hibernate语句生成超长。就算派生继承，还得要再搞个字段表示继承类的类型方便SQL过滤。
     * */
    @Column(nullable = false)
    private String type;
    /**设备类别代码 EQP_SORT{首2个字符} ,
     * 62个
     * 可为空
     * */
    private String sort;
    /**设备品种代码 EQP_VART{首3个字符}
     * 142个
     * 可为空
     * */
    private String vart;
    /**SUB_EQP_VART 子设备品种？{4个字符}用于做报告选择模板/收费计算参数。
     * 22个
     * 可为空 9999
     * */
    private String subv;

    /**目录外IN_CAG 目录属性 1:目录内，2：目录外 目录外的{针对设备}不一定不能是法定的{针对业务操作}性质
     * */
    private Boolean   ocat;
    /**EQP_STATION_COD 设备代码(设备国家代码)*/
    private String sno;
    /**EQP_USECERT_COD 使用证号
     * 有可能更换号码，换证？新号
     * */
    private String cert;
    /**EQP_REG_COD 监察注册代码 设备注册号 eqp_reg_sta不同的就可重复
     * eqp_reg_cod=NEW_EQP_REG_COD 注册代码 和eqp_reg_sta相关;
     * 注册码还会有变动和新的
     * */
    private String rcod;
    /**EQP_LEVEL 设备等级 ,可融合合并旧平台的CLASS_COD 产品分类代码
     * [合并字段]游乐AMUS_TYPE游乐设施等级类型;PIPELINE_LEVEL，管道独立的?总的级别，但底下所属单元可有自己级别。
     * 设计上的级别; 目前随意，todo://根据type看怎么规范-提升使用价值。
     * 关键的等级有些已直接体现到设备{容器}类别上了。
     * 游乐设施的，同步AMUS_TYPE设备级别
     */
    private String level;
    /**FACTORY_COD  出厂编号
     * 若管道的 ，实际是工程描述、比较长。
     * */
    private String fno;
    /**EQP_NAME 设备名称，给外行看的*/
    private String name;

    /**EQP_INNER_COD 单位内部编号place No
     * 附加上后更加能精确定位某个地理空间的位置
     * */
    private String plno;
    /**EQP_MOD 设备型号, 有没有型号外部编码规范，可能随意填，监察关心!
     * */
    private String  model;
    /**IF_INCPING 是否正在安装监检{检验业务状态/时间长、监察关心注册}, 相关IF_NOREG_LEGAR非注册法定设备（未启用）
     * 改造大修 安装监检或改造监检 EQP_REG_STA=0？ 检验竟然设置注册状态？
     * 监检中了，定检就跳过吗（普通规则失效）,管道装置大；
     * */
    private Boolean  cping;
    /**IF_MAJEQP 是否重要特种设备
     * 监察初始化设置的关照级别。
     */
    private Boolean  vital;

    /**FIRSTUSE_DATE 设备投用日期  ，管道EQP_FINMAKE_DATE
     * 投用有磨损，应该比安装日期更关注。
     * */
    @Temporal(TemporalType.DATE)
    private Date used;

    //private Date accd; COMPE_ACCP_DATE 竣工验收日期;
    /**END_USE_DATE 使用年限到期时间
     * */
    @Temporal(TemporalType.DATE)
    private Date    expire;

    /**IS_MOVEEQP 是否流动设备
     * 流动作业设备才会出现省外JC注册　使用证/编码都是外省旧的,oidno才是新的。
     * */
    private Boolean  move;
    //EQP_AREA_COD定义规律大乱；    //统计和行政含义的地址区分；
    //  private String  area;    //实际应该放入Address中, 暂用； EQP_AREA_COD 设备所在区域
    //  private String addr;    //暂时用 EQP_USE_ADDR 使用地址 //该字段数据质量差！
    //.EQP_USE_PLACE场所性质　？不一样概念，或Address pos底下附加属性。
    //   private String occa;    //EQP_USE_OCCA 使用场合　.EQP_USE_OCCA起重才用
    //楼盘=地址的泛房型表达式;     单独设立一个模型对象。　(楼盘名称)＝使用地点！=使用单位的单位地址。
    //  private Long  buildId;    //暂用 BUILD_ID  楼盘ID

    /**算钱搞的，EQP_PRICE 产品设备价(进口安全性能监检的设备价)(单位:元) EQP_SELL_PRICE设备销售价格
     * */
    private Float money;

    /**监察扩展，JSON非结构化存储模式的参数，能支持很多个，但是java无法简单化访问或操控单个技术参数。
     * 可加: 监察非结构化字段；前端可方便操作，后端都不参与的字段{但统计抽取方式就可除外}。
     * 未注册设备可以授权检验人员或SDN告知人员修改的，审核注册后权限关闭，相当于临时设备库倒腾。
     * 临时把它初始化为= USE_MOBILE
     */
    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column( columnDefinition="TEXT (1500)")
    private String  svp;
    /*svp.json参数有这些：
    进口类型：IMPORT_TYPE  "国产";
    制造国 MAKE_COUNTRY {非行政区域实体类型关联字段}
    制造日期：MAKE_DATE  制造（安装）日期 ，统计范围查询  DESIGN_USE_YEAR 设计使用年限
    安装日期 INST_DATE监察告知单，  EQP_INST_DATE{管道单元} 跟安装单位相关；
    COMPE_ACCP_DATE 竣工验收日期 和施工单位相关； 管道才有意义；
    DESIGN_USE_OVERYEAR设计使用年限 到期年份?统计？
    .EXTEND_USE_YEAR延长使用年限; 应该是个历史资料，关联审批单？
    事故隐患类别：ACCI_TYPE， 类似含义字段太多了/监察操心的。ACCI_TYPE=[{id:'1':'特别重大'},{id:'2':'特大'},{id:'3':'重大'},{id:'4':'严重'},{id:'5':'一般'}];
    是否重点监控 IF_MAJCTL， 类似含义字段太多了, 含义雷同:IF_MAJEQP 是否重要特种设备。
    CONST_UNT_ID土建施工单位，CONST_UNT_NAME 监察审核什么东西，难道给个单位名称就都放行、仅备注{没有实际意义}难道验证资质证照，验明真身是浮云。
    CONST_ACCP_UNT_ID土建验收单位, 监察临时设备表特有字段；CONST_ACCP_UNT_NAME
    CONST_CLASS施工类别 CONST_UNT_CHK_NUM, CONST_START_DATE COMPE_ACCP_DATE 施工日期、验收，COMPE_ACCP_DATE竣工验收日期
    DESIGN_CHKUNT设计文件鉴定单位(已淘汰?)，可以在告知或首检录入时低权限用户录入，然后审核登记时高权限用户触发比对关联资料，注册后更新pa.json进历史记录。
    DESIGN_UNT_CHK_NUM 设计{单位？}许可证编号(已淘汰?)
    DESIGN_UNT_ID  设计单位名称/资质{设计时他有资质，注册后，失去资质呢，审核时间做资质快照的}， DESIGN_UNT_NAME管道报告用。
    PRODUCT_MEASURE 产品标准 {号}？关联标准实体列表。
    一次性验证后就不会在做修改的可关联信息{当时快照数据}：
    TYPETEST_UNT_NAME 监察单位管理 型式试验单位{高层级认定},应该是针对生产单位的属性。TYPETEST_UNT_NAME代表产品(限制范围,典型产品)
    TEST_UNT_ID 监察,型式试验 型式试验单位; TEST_REPCOD型式试验报告编号{历史特别检验}，检验设备表也有。
    TEST_UNT_CHK_NUM试验机构核准证编号  TEST_UNT_CERT_NUM型式试验证书编号{单位资格}
    型式试验和制造监检2个独立！。电梯安装监督检验时，申请单位提交符合要求的电梯整机和部件产品型式试验证书或报告。
    MAKE_ISP_UNT_ID制造监检机构 检验平台没有该字段 监察设备许可用的，MAKE_ISP_CHK_NUM监检机构核准证编号;
    MAKE_ISP_CERT_NUM制造监检证书编号{关联制造监检 证号(批量的/人工校对包含哪些设备)、检验Isp历史}。
    DESIGN_PIC 设计图号 || 产品图号；
    ACCEP_INSP_UNT_ID 验收检验单位(审核快照的) ACCEP_INSP_UNT_NAME ？安装监检； ACCEP_INST_REPORT_NUM 验收检验验收报告编号{查证URL}；客运索道?
    PRO_GB_UNT_ID (审核快照的)容器锅炉/产品监检单位；?首次检验？ PRO_GB_UNT_NAME 产品监检单位;

     */

    /**NOTELIGIBLE_FALG1 不合格标志1（在线、年度，外检）
     * 关联状态的快捷汇报字段，免去抽取关联，但是同步工作必须小心，否则会不一致。
     * 还没有做出结论判定的，就直接上null；
     * 判定为不合格的*/
    private Boolean unqf1;
    /**NOTELIGIBLE_FALG2 不合格标志2(机电定检，内检，全面）
     * */
    private Boolean unqf2;

    /**LAST_ISP_CONCLU1  '最后一次检验结论1'
     * 关联状态的快捷汇报字段，免去抽取关联，但是同步工作必须小心，否则会不一致。
     * 判定不合格的，以及不合格情形下的报告结论给出的简短的关键字提示。
     * */
    private String ccl1;

    /**LAST_ISP_CONCLU2  '最后一次检验结论2'
     * 关联状态的快捷汇报字段，免去抽取关联，但是同步工作必须小心，否则会不一致。
     * 判定为合格的或者勉强合格的，带注释提示但是合格的， 还没有做出结论判定的，就直接上null；
     * */
    private String ccl2;
    /**LAST_ISP_DATE1最后一次检验日期1【一般是外检或年度在线】
     * 初始化新平台，本平台还没有关联数据的需要，否则实际是关联Isp数据快照，注意同步一致性。
     * */
    @Temporal(TemporalType.DATE)
    private Date ispd1;
    /**LAST_ISP_DATE2 最后一次检验日期2 (机电定检，内检，全面）
     * 关联状态的快捷汇报字段，免去抽取关联，但是同步工作必须小心，否则会不一致。
     * */
    @Temporal(TemporalType.DATE)
    private Date ispd2;
    //检测记录，check 检测规定时间。检测的监察也要关心？检验机构要关注检测记录的{关联检测报告历史}。
    //Instant? 纳秒时间,不使用java.util.Date
    //@Field(type = FieldType.Date, format = DateFormat.date_time)
    //private Instant nxtD1;   规则：if等级1/3/的，耐压试验6年{2.5年}一次；

    /**NEXT_ISP_DATE1下次检验日期1（在线、年度）粗的检
     * 少部分品种的设备才需要两个系列字段都要用；多数设备只需要系列2的字段
     *管道 例外，需要看具体的管道单元。
     * */
    @Temporal(TemporalType.DATE)
    private Date nxtd1;
    /**NEXT_ISP_DATE2下次检验日期2(机电定检，内检，全面）
     *管道 例外，需要看具体的管道单元。
     * */
    @Temporal(TemporalType.DATE)
    private Date nxtd2;

    /**延期检验日期1：ABNOR_ISP_DATE1 相对NEXT_ISP_DATE1下次检验日期1
     * 检验业务状态关心字段。实际关联了延期检验申请单，申请批准后的/申请延期日期。字段关联直接转为本地状态复制字段，注意保证一致性。
     * */
    @Temporal(TemporalType.DATE)
    private Date did1;
    /**延期检验日期2：ABNOR_ISP_DATE2  超期未检+统计
     * 为什么设置该字段？ 预定下次定检日期2到了，检验任务未搞定，设置延期，就不会上报监察重要事项了，否则自动申告给监察。
     * 字段关联直接转为本地状态复制字段，注意保证一致性。
     * */
    @Temporal(TemporalType.DATE)
    private Date did2;

    //索引会被自动创建的。
    /**PROP_UNT_ID 产权单位, 不是监管重点 可省略null
     * 若null,默认=使用单位。
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  owner;
    /**发证的监察注册机构ID 流动设备原始发证机构
     * 若null,原始发证机构就=责任监察机构；省外注册的。
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "regu_id")
    private Unit regu;

    /**当前的 法定职责下的，责任监察机构。
     * REG_UNT_ID 监察注册机构ID  REG_UNT_NAME注册机构名称
     *就算设备迁出也不能是null,负责到下一个机构转入为止；
     *流动设备管理，注册监察机构和当前实际再做监察管理的机构不一样呢？历史资料／原始发证机构。
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "svu_id")
    private Unit svu;

    /**MAKE_UNT_ID 制造单位ID
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit makeu;

    /**INST_UNT_ID 安装单位ID, 最早 监检
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit insu;

    /**ALT_UNT_ID 改造单位ID; 最近做改造的，改造比维修等级资质要求高。
     * 检验平台 改造维修单位分立的。  ALT_UNT_NAME 监察平台的改造维修单位;
     * 施工告知中的  改造维修单位, 施工告知可以多条的，一次审批后，覆盖上一次的快照改造单位到svp.json中。
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit remu;

    /**OVH_UNT_ID '维修单位' 改造要监察告知，维修等级不够格，多个层级的历史记录；
     * 多次改造维修的新单位覆盖掉旧的单位？报告中能查出历史改造单位。
     * 维保单位 MANT_UNT_ID？todo: 能合并？ 应该放在业务单告知中关联!
     * 多次维修，改造的历史业务申报单子。
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit repu;

    //缺省FetchType.EAGER  不管查询对象后面具体使用的字段，EAGER都会提前获取数据。
    /**地理定位。长输管道覆盖范围大的情形特指核心地点
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "pos_id")
    private Address pos;
    //只要哪个类出现了mappedBy，那么这个类就是关系的被维护端。里面的值指定的是关系维护端
    //缺省FetchType.LAZY  　　 .EAGER
    //一个任务只能搞1个设备的/为出报告准备的{流转Isp报告,报告对应技术参数只能是一个设备，煤气罐系列化的设备号01..09排除05}。
   //todo: 改成１对多
    @ManyToMany(mappedBy="devs" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<Task> task= new HashSet<>();

    //单1次ISP只能做1个EQP;考虑？一次检验很多气瓶？若支持设备汇聚出场编号汇集重新转义呢，1:N子部件设备关联表。
    //Eqp.TASK.Isp  Eqp.Isp {.短路?}  复杂关联关系， 在做EntityGraph选择定义不恰当而貌似可能死循环了？
    //ISP挂接关系到EQP底下还是挂接关系到TASK底下的？不可以两者同时都挂接关联关系，那样就是多余和混淆概念或两种分歧路径，数据多了而且还产生不一致了。
    //检验单独生成，TASK和EQP多对多的；单个ISP检验为了某个EQP和某个TASK而生成的。
    //先有派出TASK，后来才会生成ISP； 两个地方都必须维护数据的。
    //缺省FetchType.EAGER  LAZY
    @OneToMany(mappedBy="dev" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<Isp>  isps;

    //底下这两组实际相当于内嵌结构对象，或者说[mtU，mtud]是复合字段的。单位ID+分支部门ID配套的才能完全表达出来。
    /**MANT_UNT_ID 维保单位ID maintUnt,电梯才有的
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit mtu;
    /**针对维保单位的细化　分支机构部门。
     * MANT_DEPT_ID 监察才关心的	 .MANT_UNT_ID	is '维保单位ID'；检验平台没有设置该数据。
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Division mtud;     //.MANT_DEPT_ID 监察才关心的	 .MANT_UNT_ID	is '维保单位ID'；检验平台没有设置该数据。

    /**USE_UNT_ID 使用单位ID;　正常业务上单位都是用它；
     * 若是个人就一定没分支部门；[useu，usud]复合字段的；
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit useu;     //USE_UNT_ID 使用单位ID
    /**针对使用单位的细化　管理分支部门。
     *假如设备表没有指定Division部门的，那就是Unit作为缺省部门:等价于该单位底下没有细分的部门，若要求具体Division但是该单位没有细分Division情形。
     *MGE_DEPT_TYPE若=2：TB_UNT_SECUDEPT关联; MGE_DEPT_TYPE若=1很少作废了TB_UNT_DEPT关联
     */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Division usud;     //.SECUDEPT_ID	'分支机构ID' || .SAFE_DEPT_ID '安全管理部门'

    /**扩展的技术参数，JSON非结构化存储模式的参数，能支持很多个，但是java无法简单化访问或操控单个技术参数。
     * 可加: 设备联系人，设备联系人电话；前端可以方便操作。 USE_MOBILE 设备联系人手机/短信；维保人员？
     */
    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column( columnDefinition="TEXT (12000)")
    private String  pa;
    /*在pa.json加这些参数：
    USE_MOBILE 设备联系人手机,直接关联Person实体类可能信息更新速度不够快/检验员直接修改最及时。
    INSURANCE_INS_NAME 保险单位 监察 起重机洗才有保险机构, INSURANCE_AMOUNT 保险金额
	INSURANCE_TYPE 保险险种 INSURANCE_VALUE 保险价值 INSURANCE_PREMIUM 保险费；
    IF_WYL是否微压炉？ 检验才有的，该字段已经删除了吗!

    INST_COMP_DATE 安装竣工日期

    QUACERT_COD制造许可证号--资格证书号

    EMERGENCY_TEL应急救援电话： EMERGENCY_USER_NAME应急救援人名
    SAFE_LEV安全评定等级 IF_SPEC_EQP是否特殊设备
    上次检验报告号1 链接关联。
    EQP_WEIGHT 游乐设备重，PRODUCT_NUM质量证明书编号
    LAST_ISPOPE_TYPE2 检验类型代码2
    LAST_ISPOPE_WENTI2 主要问题2 LAST_ISP_WENTI1
    MANT_CYCLE维保周期 MANT_TYPE维保型式 MANT_QUACERT_COD资格证书号 ALT_CYCLE大修周期
    REG_ISP_MONEY定检标准收费(定检、内部、全面)(单位：元); ACP_ISP_MONEY验收标准收费(外部、在线、年度)(单位：元)
    EQP_SETAMOUNT设备布置数量/没用? IF_MAJCTL是否重点监控  IF_MAJPLACE是否在重要场所
    QUACERT_NAME制造资格证书名,QUACERT_COD制造许可证号 PRODUCT_NUM质量证明书编号=产品合格证编号

     */

    /**ISPUNT_NAME ISPUNT_ID当前分配去哪个法定检验机构(市场化标定模式)
     * 缺省由svu监察主动分配的。
    */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit ispu;

    /**.SAFE_LEV安全评定等级{监察才用到，检验没用},对合格字段补充/检验结论还不够{结论/不合格都是两个字段的}，
     * 什么时机设置本字段的？干啥的。备注吗/巡视。
     * 目前只有2000容器类才有，其他类型备用的；
     * 动态的级别，用于监察重点关注用。 null=没事安全
     * 被申报重要事项？汇总enum列表,目的给监察前端一个结论性字段，方便前端过滤。
     */
    private String safe;
    /**注册登记人员REG_USER_NAME 注册人员姓名  REG_USER_ID注册人员{关联操作日志}
     * 状态变更; 注册 流水日志，注销记录？到底谁敢干的；旧平台导入数据的：历史用户以往旧人员呢？
     * 注册历史记录，关联操作User实体的, 可历史记录太旧的可能被当做垃圾数据删除。
     * 把User改成Person实体关联来做，Person还会死亡注销也成垃圾数据。可改成中文姓名String(同名的)，爱查就去找,姓名修改
     */
    private String  rnam;
    /**注销人员, 中文姓名 关联操作记录
     * 使用状态报停的经手人员，操作记录；特别旧平台导入新平台如何处理？旧平台关键历史记录也得导入或伪增。
     * */
    private String  cnam;

    /**注册登记日期REG_DATE
     * */
    @Temporal(TemporalType.DATE)
    private Date regd;
    /**注册登记注销日期REG_LOGOUT_DATE
     * */
    @Temporal(TemporalType.DATE)
    private Date cand;

    //@Transient用法，非实际存在的实体属性，动态生成的实体临时属性字段。
    //大规模数据集查询不可用它，效率太慢，应该。。
    //本函数执行之前，JPA数据实际已都取完成了。
    //安全考虑，过滤isps字段合理输出,代替原来缺省的getXXX
    //@org.springframework.data.annotation.Transient  俩个注解都一样
    @Transient
    public Set<Isp>  meDoIsp(){         //若是getMeDoIsp()名字，REST会使用它序列化输出,getXXX都是。
        Long curruser=(long)5;  //临时test: JwtUser.getUserId();
        //限制只能看自己的ISP; 没登录的人就为空。
        //? REST 序列化会读取到MeDoIsp？
        return   isps.stream().filter(isp ->
                (isp.getIspMen().stream().filter(men->
                curruser==men.getId()
                    ).count()>0 )
        ).collect(Collectors.toSet());
       //[误区]像这样的stream().filter().collect全部转载到后端服务器内存，速度慢！正常的应当依赖数据库去直接驱动SQL查询过滤后返回小部分数据集合。
        //Todo: 应该改为JPA接口从让数据库替您搜索，我这里等着数据库给答案就好了。
    }

}



//EntityGraph用处：用来避免Lazy延迟加载导致的代码失败问题，内部附带效果：减少了发给数据库的select语句条数。
//定义多个 @NamedAttributeNodes 以定义更复杂的图，也可以用 @NamedSubGraph 注解来创建多层次的图。https://thoughts-on-java.org/jpa-21-entity-graph-part-2-define/?utm_source=rebellabs
//lazy/eager loading at runtime延迟加载变成可以动态参数Map hints指定了fetchgraph;  　 @NamedSubgraph指定多层的。
//EntityGraph的定义范围：实际针对的是从上往下看单向LAZY的关联对象字段，EntityGraph才是有用的。
//非 web 请求下的懒加载问题解决  https://blog.csdn.net/johnf_nash/article/details/80658626
//JPA 一对多延迟加载与关系维护,属性级延迟加载blob大字段   https://blog.csdn.net/lhd85/article/details/51692546
//访问延迟属性若EntityManager这个对象被关闭，我们再去访问延迟属性的话，就访问不到，并抛出延迟加载意外;spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
//枚举，enum转换器 @Converter(autoApply = true)  ； https://thoughts-on-java.org/jpa-21-type-converter-better-way-to/

/* 删除没用的头注解： 用@EntityGraph(value="Eqp.task",)做查询优化可不容易掌控的；很容易出笛卡儿积爆炸问题。
@NamedEntityGraphs({
        @NamedEntityGraph(name= "Eqp.task",
                attributeNodes = {
                        @NamedAttributeNode(value= "task",subgraph= "taskg"),
                },
                subgraphs = {       嵌套的指示，下一级关联对象的hits;
                        @NamedSubgraph(name = "taskg", attributeNodes =
                                { @NamedAttributeNode("isps"),  }
                        ),
                }
        ) ,
        @NamedEntityGraph(name = "Eqp.isps",
                attributeNodes = {
                        @NamedAttributeNode(value= "isps",subgraph= "ispsg"),
                }
        )
})
*/

//无法引用其他schema底下的表的外键？建立同义词(synonym)＋授权。
//@Table( schema="newsei")    Oracle下就等于用户，似乎没啥必要性，管理更麻烦。Oracle下RAC数据库可被多实例所使用。

/*
@NamedEntityGraphs({  每个NamedEntityGraph都是独立无关的hints，若一个查询语句同时加上多个hint，底层它该如何协调;底层API不会精确区分把控上层应用实体的真正目的。
        @NamedEntityGraph(name = "Eqp.all",    某个场景用一个hint;
                attributeNodes = {}  )      //实际上attributeNodes可以多个，但是特别小心，关联不密切的关联对象一次性join=会产生爆炸记录数！！attributeNodes只做一个较妥。
        @NamedEntityGraph(name = "Eqp.special",    另外一个场景用另外一个hint;
                attributeNodes = {}  )
    })
join爆炸记录数范例 @NamedEntityGraph( name="Eqp.task",attributeNodes={　@NamedAttributeNode("task"),　@NamedAttributeNode("isps")　} )  无关的task+isps搞在一起＝爆炸。
JPA日期字段短的需要加@Temporal(TemporalType.DATE)节省硬盘，否则缺省是=@Temporal(TemporalType.TIMESTAMP)长的。
*/

//注解定制索引，没啥实际意义。
//@Table(indexes={ @Index(name="type_idx",columnList="type"),
//         　 @Index(name="factoryNo_idx",columnList="fNo")  } )

//二级缓存可移植性@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Fast") 这里region是按照配置来区分的区分标识，竟然不省略。

/*树形层次分类设备； https://baike.baidu.com/item/%E7%89%B9%E7%A7%8D%E8%AE%BE%E5%A4%87%E7%9B%AE%E5%BD%95/19834714?fr=aladdin#2
vart是复合字段就可以唯一表示出设备类型(国家标准范围),vart的3个字符按照顺序分别拆解出type,sort类型。subv是独立的扩展{针对sort}。
type,sort,vart,subv设备细分类别，实际前端采用的多，算展示层映射用的。后端服务器主要用于计费/报告类型的控制。
subv是国家标准之外扩展的分类标记，实际附带于sort底下的，目的用于修饰vart的细分类型。
type,sort,vart三个是层次化的以3个单个字符来组合的属性=复合字段分解为3个字段。
type有：
3 电梯
 sort有：
 31 曳引与强制驱动电梯
    vart有：
    311 曳引驱动乘客电梯
    312 曳引驱动载货电梯
    313 强制驱动载货电梯
    subv在31底下的有：
    3001 有机房电梯
    3002 无机房电梯
 32 液压驱动电梯
    vart有：
    321 液压乘客电梯
    322 液压载货电梯
    subv在32底下的=无： {32底下目前没有3002该细分型}3210有16条3001细分？问题数据
 33 自动扶梯与自动人行道
    vart有：
    331 自动扶梯
    332 自动人行道
    subv在32底下的=无： 已有的是问题数据
 34 其它类型电梯
    vart有：
    341 防爆电梯
    342 消防员电梯
    343 杂物电梯
    subv在34底下的有：
    3001 有机房电梯
    3002 无机房电梯
 <3, 3000,3000,9999无细分>?是问题数据: 待注册+未投入使用的?还未决定细分和具体种类，字段可为空=未决定。
2 压力容器
    sort有：
    21 固定式压力容器
        vart有：
        211 超高压容器
        213 第三类压力容器
        215 第二类压力容器
        217 第一类压力容器<低压>
        subv在21底下的有：
        2001 大型压力容器
        2002 球形容器
        2003 液化气体储配站
        2004 非大型压力容器
        2005 超高压容器
        2006 超高压水晶釜
    22 移动式压力容器
        221 铁路罐车
        222 汽车罐车
        223 长管拖车
        224 罐式集装箱
        225 管束式集装箱
        subv在22底下的=无：
    23 气瓶
        231 无缝气瓶
        232 焊接气瓶
        23T  特种气瓶（内装填料气瓶、纤维缠绕气瓶、低温绝热气瓶）
        subv在=无：
    24 氧舱
        241 医用氧舱
        242 高气压舱
        subv在=无：
4 起重机械
    41: "桥式起重机"
        411: "通用桥式起重机",
        413: "防爆桥式起重机",
        414: "绝缘桥式起重机",
        415: "冶金桥式起重机",
        417: "电动单梁起重机",
        419: "电动葫芦桥式起重机",
        subv在=无：
    42: "门式起重机"
        421: "通用门式起重机"
        422: "防爆门式起重机"
        423: "轨道式集装箱门式起重机"
        424: "轮胎式集装箱门式起重机"
        425: "岸边集装箱起重机"
        426: "造船门式起重机"
        427: "电动葫芦门式起重机"
        428: "装卸桥"
        429: "架桥机"
        subv在42=无：
    43: "塔式起重机"
        431 普通塔式起重机
        432 电站塔式起重机
        subv在=无：
    44: "流动式起重机"
        441 轮胎起重机
        442 履带起重机
        444 集装箱正面吊运起重机
        445 铁路起重机
        4490 4480 4430 =飞出目录
        subv在=无：
    47: "门座式起重机"
        471 门座起重机
        476 固定式起重机
        subv在=无：
    48: "升降机"
        486 施工升降机
        487 简易升降机
        vart='4890'例外1条； 488,489=飞出目录了
        subv在底下的有：
        4871 简易升降机（电动葫芦式）
        4872 简易升降机（曳引式）
    49: "缆索式起重机"
        491 缆索式起重机{490也成}
        subv在=无：
    4A: "桅杆式起重机"
        4A1 桅杆式起重机
        subv在=无：
    4D: "机械式停车设备"
        4D1 机械式停车设备{4D0也成}
        subv在4D底下的有：
            "4001": "升降横移类机械式停车设备",
            "4002": "垂直循环类机械式停车设备",
            "4003": "多层循环类机械式停车设备",
            "4004": "平面移动类机械式停车设备",
            "4005": "巷道堆垛类机械式停车设备",
            "4006": "水平循环类机械式停车设备",
            "4007": "垂直升降类机械式停车设备",
            "4008": "简易升降类机械式停车设备"
    sort=4C00||4B00{已经飞出特种设备目录表了}的数据没啥用。
8 压力管道
    "81": "长输管道",
        811 "输油管道"
        812 "输气管道"
    "82": "公用管道",
        821 "燃气管道"
        822 "热力管道"
    "83": "工业管道",
        "831": "工艺管道",
        "832": "动力管道",
        "833": "制冷管道",
1 锅炉
    "11": "承压蒸汽锅炉",
        111 "承压蒸汽锅炉"
        subv在11底下的有：
        "1002": "电站锅炉",
        "1003": "工业锅炉",
    "12": "承压热水锅炉",
        121 "承压热水锅炉"
        subv在12底下的有：
        "1002": "电站锅炉",
        "1003": "工业锅炉",
    "13": "有机热载体锅炉",
        "131": "有机热载体气相炉",
        "132": "有机热载体液相炉",
        subv在13底下的有：
        "1002": "电站锅炉",
        "1003": "工业锅炉",
5 场（厂）内专用机动车辆
    "51": "机动工业车辆",
        511 "叉车"
        非标准目录的：5120 "叉车(防爆功能)" 有24条数据?, 涉及报告？收费？
    "52": "非公路用旅游观光车辆",
        521 "非公路用旅游观光车辆"
        而 5220 5230 =飞出目录
        subv在52底下的有：
        "5001": "内燃类",
        "5002": "电动类",
     "5C00" "5B00"= 飞出目录
6 大型游乐设施
    "61": "观览车类",
        611 "观览车类"
    "62": "滑行车类",
        621 "滑行车类"
    "63": "架空游览车类",
        631 "架空游览车类"
    "64": "陀螺类",
        641 "陀螺类"
    "65": "飞行塔类",
        651 "飞行塔类"
    "66": "转马类",
        661 "转马类"
    "67": "自控飞机类",
        671 "自控飞机类"
    "68": "赛车类",
        681 "赛车类"
    "69": "小火车类",
        691 "小火车类"
    "6A": "碰碰车类",
        6A1 "碰碰车类"
    "6B": "滑道类",
        6B1 "滑道类"
    "6D": "水上游乐设施",
        "6D1": "峡谷漂流系列",
        "6D2": "水滑梯系列",
        "6D4": "碰碰船系列",
        "6D30" "6D60" = 飞出目录
    "6E": "无动力游乐设施",
        "6E1": "蹦极系列",
        "6E4": "滑索系列",
        "6E5": "空中飞人系列",
        "6E6": "系留式观光气球系列"
        subv在6E底下的有：
        "6001": "高空蹦极系列",
        "6002": "弹射蹦极系列",
        "6003": "小蹦极系列",
        ?"6004": "滑索系列", ?  和"6E4"重合，数据问题？报告/计费？
        subv?"6005" ?"6006": 没数据；
9 客运索道
    "91": "客运架空索道",
        "912": "循环式客运架空索道",
        "911": "往复式客运架空索道",
    "92": "客运缆车",
        "922": "循环式客运缆车",
        "921": "往复式客运缆车",
    "93": "客运拖牵索道",
        "932": "高位客运拖牵索道",
        "931": "低位客运拖牵索道",
R 常压容器 <非国家标准目录的>  ocat=true 是目录外
    "R3":"危险化学品常压容器"
        "R31": "液体危险货物常压容器(罐体)",
        "R32": "危险化学品常压容器",
F 安全附件 <标准目录的> 。
    731 安全阀?? 不能独立构成一个设备，监管目标太小了，检验管理太细了，无法进入Eqp设备模型的表。 没有单独报告/收费。
7 压力管道元件 {目录内，给制造生产的}
    制造的，一次性的，批次的雷同多个单元；没必要进入Eqp设备表。
    "71": "压力管道管子",
    "72": "压力管道管件",
    "73": "压力管道阀门",
    "74": "压力管道法兰",
    "75": "补偿器",
    "77": "压力管道密封元件",
    "7T": "压力管道特种元件",
*/
