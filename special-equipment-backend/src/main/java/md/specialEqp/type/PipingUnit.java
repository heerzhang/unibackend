package md.specialEqp.type;

import lombok.Getter;
import lombok.Setter;
import md.cm.unit.Unit;
import md.specialEqp.inspect.Task;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**管道单元,一条管道底下有很多的管道单元来组成的。TB_PIPELINE_UNIT_PARA
 * 每个单元管道特性表 TB_PIPELINE_UNIT_PARA  JC_TEMP_PIPELINE_UNIT_PARA
 * 单元没有独立的地理定位字段，归属业务管辖区域码也没做独立设置；单元可能超长一百公里。
 * 同一个使用单位下的（工程装置名称、管道编号）不允许重复。
 */
@Getter
@Setter
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"pipe_id", "code"})} )
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Medium")
public class PipingUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipe_id")
    private Pipeline pipe;

    /**业务锁，每个管道单元在本平台只能存在唯一一个活动的Task{局限性，排斥多业务并行，必须前面任务终结后面才能受理}。
    当前管道单元最新已生成task关联关系表[任务锁],直接在PipingUnit增设'当前task'=锁；
     业务锁困扰: 特殊任务可以不指定单元没有在任务单声明每一个PipingUnit只需要Eqp代指(实际报告或证明书可以另外约束指定详细单元)，
     再不行只能多加个业务锁了,分类别加锁。 目前思路针对监管核心业务上锁。
    */
    @ManyToOne(fetch= FetchType.LAZY)
    private Task task;     //单向关联关系，就是锁定占坑的。

    //关键字段》》 "管道名称(登记单元)"	"管道编号"
    //1"序号（监管平台生成）" ?系统内部id?排序的;
    /**管道单元登记编号EQP_UNT_REGCOD;
     * 多数是顺序编号编下去: 总使用证-1,-2,-3...。
     */
    private String  rno;

    //2"管道名称(登记单元)" //管道名称(工程名称)：
    /**管道名称(登记单元); 实际每条单元可不同、也可相同。
     * 使用单位已经自主制定的直接用！  设计资料上的管道名称；用工程编号代替也行的。
     */
    private String  name;

    /**EQP_CODE管道编号:每个单元编码不同，管道单元的代码：报告书和单线图里面的key。
     * 同一个使用单位下的同一工程装置名称、管道编号不允许重复。
     * 使用单位自主制定的 管道编号。
     * */
    private String  code;

    //为止点起点地理定位,经度纬度？没意义，实际业务都要查设计图，找使用单位查更加明细资料来做地理定位。
    //直接套用管道设备的Address　pos字段定位地理，以及归属业务管辖的区域码{单个管道设备一个业务唯一管理部门/出报告方便}。

    /**管道起点START_PLACE; 看单线图{设计单位}和工程图{安装单位}
     * 图纸上的标签代码,甚至是内部定位叙述，不特定指代
     */
    private String  start;

    /**管道止点END_PLACE; 看单线图和工程图
     * 图纸上的标签代码,甚至是内部定位叙述，不特定指代
     */
    private String  stop;

    /**使用状态USE_STA；若1,2,3,9是有效管理的。每个单元都独立的状态；
     * 1未投用2在用3停用9在用未注册登记; 4报废5拆除7\8垃圾, 使用状态和监察注册有一点关系；6迁出=变更监察省份；
     *注册状态是行政上发证书的{要收钱拿证}， 使用状态是微细管理业务停顿{不收钱报停复工}；
     */
    private Byte   ust;

    //TB_PIPELINE_UNIT_PARA.EQP_REG_STA  '注册状态'
    //TB_PIPELINE_UNIT_PARA.EQP_REG_COD '注册代码'
    //TB_PIPELINE_UNIT_PARA.REG_UNT_ID '注册机构'
    /**注册EQP_REG_STA，监察知道吗　[{id:'0',text:'待注册'},{id:'1',text:'在册'},{id:'3',text:'注销登记'}];
     * 每个单元都独立的注册，当前管道设备底下的各个管道单元实际地址和管理区域码不一定都一样的。
     * 当前每个管道设备底下的各个管道单元都是同一个注册机构注册的。
     * 所属管道的父类Eqp.regu注册机构，注册发证的。管道单元没有迁出的/流动设备，责任监察机构就=注册机构;
     * 市场化的检验竞争，以整个管道装置设备oid/使用证、工程（装置）名称，统一划归某个检验机构。
     */
    private Byte   reg;

    //TB_PIPELINE_UNIT_PARA.UNIT_AREA_COD '管道单元所在区域'，设置上级管道设备的区域/大的包含小的地理区域码。
    //管道合并操作重新配置地址区域码Address pos设备登记位置;　注册监察机构的机构位置的区域码。

    /**计费用-管道直径，NOMINAL_DIA公称直径（mm）； 不规范φ114.3　φ168，￠57, 168.3/114.3 有很多数据。
     * .V_PIPELINE_DIA  '计费用-管道直径（公称直径mm）规范，　数据较少;
     * 计费依据：NOMINAL_DIA>=50.0&&NOMINAL_DIA<=150.0  + length;
     * 公称直径(mm); 若是存在多种规格，只填写典型管径数据{壁厚/直径};
     */
    private Float dia;
    /**公称壁厚(mm); 若是存在多种规格，只填写典型管径数据{壁厚/直径};
     * */
    private Float thik;

    /**管道长度(m) LENGTH; 旧数据可null,可能超长一百多公里{这么长也不拆解开}。
     * 管道规格泛指的3个参数："公称直径(mm)"	"公称壁厚(mm)""管道长度(m)"
     * 管道规格必须填写数值型， 长度是本逻辑管道单元的合计数。
     */
    private Float leng;


    //todo: "管道级别"级别enum ?  .V_PIPELINE_LEVEL  '计费用-管道级别（[G][A-D][1-3]）'

    /**计费用-管道级别PIPELINE_LEVEL，管道的各个所属单元都可有自己单独设置的级别。
     * TB_PIPELINE_UNIT_PARA.PIPELINE_LEVEL'管道级别'　.V_PIPELINE_LEVEL'计费用-管道级别（[G][A-D][1-3]）'
     * 合成属性123级别+ABCD类别；PIPELINE_LEVEL = "";管道级别@[{id:'GA1',text:'GA1'},{id:'GA2',text:'GA2'},
     * {id:'GB1',text:'GB1'},{id:'GB2',text:'GB2'},
     * {id:'GC1',text:'GC1'},{id:'GC2',text:'GC2'},{id:'GC3',text:'GC3'},
     * {id:'GD1',text:'GD1'},{id:'GD2',text:'GD2'}]　规范化用V_PIPELINE_LEVEL
     */
    private String level;


    /**计费用-敷设方式，TB_PIPELINE_UNIT_PARA.LAY_MODE   is '敷设方式' 随意填写　不规范
     *TB_PIPELINE_UNIT_PARA.V_LAY_MODE  is '计费用-敷设方式（架空、埋地、其他）'　Enum?比较少
     * LAY_MODE管道敷设方式[{id:'埋地',text:'埋地'},{id:'架空',text:'架空'},{id:'其它',text:'其它'}]
     */
    private String  lay;

    /**计费用管道材质的大类；@[{id:'钢制',text:'钢制'},{id:'PE管',text:'PE管'}]
     * PIPELINE_MEDIUM管道材质，复合字段PIPELINE_MEDIUM材质代码,V_PIPELINE_MEDIUM材质大类，
     * 实际上　是标号 PE100;   20（GB/T8163-2008）
     * 计费依据：PIPELINE_MEDIUM==钢制&&PIPELINE_LEVEL=!GA
     */
    private String  matr;

    /**介质
     */
    private String  mdi;


    //关联 备份字段：
    /**下次年检日期YEAR_NEXT_ISP_DATE；*/
    private Date nxtd1;      //NEXT_ISP_DATE1下次检验日期1（在线、年度）粗的检
    /**定检下检日期NEXT_ISP_DATE；
     * NEXT_ISP_DATE 定期检验下次检验日期 : 预定
     * */
    private Date nxtd2;      //NEXT_ISP_DATE2下次检验日期2(机电定检，内检，全面）
    //省略掉：监检报告下检日期INCP_NEXT_ISP_DATE；

    /**扩展的技术参数，JSON非结构化存储模式的参数
     * 离线快照， 非实时关联的。
     * 设计/工作条件一共5个参数： "设计压力(MPa)"	"工作压力(MPa)"	"设计温度(℃)"	"工作温度(℃)"	介质；
     * 单线图URL;
     */
    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column( columnDefinition="TEXT (2000)")
    private String  pa;

/*  每一个单元可独立配置的：
	EQP_INST_DATE 安装日期
	第二页：管道基本信息汇总表
    INCP_ISP_REPORT_COD 监督检验报告号, 关联Isp;
    INCP_ISP_NAME 监督检验机构 关联ID;
    INCP_ISP_DATE 监检日期
    INCP_ISP_CONCLU 监督检验结论
    ISP_UNT_NAME 定检机构 ;?应该分配定检机构=管道装置统一敲定一个定检机构的-不能肢解了。
    ISP_REPORT_COD 定检报告号, 关联Isp;
    ISP_DATE 定期检验检验日期
    ISP_CONCLU 定期检验检验结论
    "设计压力(MPa)"	"工作压力(MPa)"	"设计温度(℃)"	"工作温度(℃)"
    单线图
*/


    /**DESIGN_UNIT 设计单位
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit desu;

    /**INST_UNIT 安装单位
     * */
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit insu;

    /**EQP_FINMAKE_DATE 投用日期 ，超期服役？ 影响到结论的！改造大修
     * */
    @Temporal(TemporalType.DATE)
    private Date used;

    //todo:关联 实时关联的Isp字段：关联Isp 以过滤排序形式返回给前端展示层的
    //  private Isp isp;   //定检检验报告
    //private Isp year;  //年检报告
    //监检报告

}



//不可改参数：管道编号EQP_CODE; 管道长度LENGTH;管道起点START_PLACE;管道止点END_PLACE;管道单元登记编号EQP_UNT_REGCOD;
//附带不可改属性： 使用状态USE_STA； 下次年检日期YEAR_NEXT_ISP_DATE；定检下检日期NEXT_ISP_DATE；监检报告下检日期INCP_NEXT_ISP_DATE；
// 年检报告编号YEAR_ISP_REPORT_COD； 年检结论YEAR_ISP_CONCLU；　年检日期YEAR_ISP_DATE；
//   定检检验报告编号ISP_REPORT_COD；　定检结论ISP_CONCLU；　定检日期ISP_DATE；
//   监检报告编号INCP_ISP_REPORT_COD；　监检结论INCP_ISP_CONCLU；　监检日期INCP_ISP_DATE；

//NOMINAL_DIA>=50.0&&NOMINAL_DIA<=150.0  + length;
//单项管道检验总长度;  LAY_MODE=!埋地||LAY_MODE=!架空     //PIPELINE_MEDIUM==钢制&&PIPELINE_LEVEL=!GA
/*
管道的分类、级别及界别划分http://xinzhi.wenda.so.com/a/1511970916201369
GA类(长输管道)又分为：GA1类、GA2类；
GB类(公用管道)又分为：GB1类、GB2类；
GC类(工业管道)又分为：GC1类、GC2类、GC3类；工业管道=是指企业、事业单位所属的;
GD类(动力管道) 又分为：GD1类、GD2类。 动力管道=火力发电厂用于输送蒸汽、汽水两相介质的管道
TB_PIPELINE_UNIT_PARA.IS_KX'是否为跨县区管道登记单元：0-否，1-是' 实际数据没有=1的。
单个管道设备底下再做细分的跨越区域登记管理一个管道单元有何意义？？登记管理区域实际上还能够升级地区级别的，还怕分解不了吗!
长输管道完全可以直接肢解成各个大地区独立的管道设备去搞登记的。
设计/工作条件一共5个参数： "设计压力(MPa)"	"工作压力(MPa)"	"设计温度(℃)"	"工作温度(℃)"	介质；

*/

