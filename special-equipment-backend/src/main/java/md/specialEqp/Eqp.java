package md.specialEqp;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.cm.unit.Division;
import md.cm.unit.Unit;
import md.specialEqp.inspect.ISP;
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
//字段名称用了保留字导致表EQP无法自动建立！ 需手动创建最简单eqp表。



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
    private int  version;   //之前Timestamp类型ES过不了
    //该条设备记录已被设置成了删除态不再有效，就等待以后维护程序去清理这些被历史淘汰的数据了。
    @NotNull
    private Boolean valid=true;
    //@PropertyDef(label="监察识别码")    数据库建表注释文字。
    //@Column(length =128, unique = true)
    @Field
    @Column(length =40)
    private String oid;     //OIDNO每一个省份监察机构自己产生的易识别码。
    @Field
    @Size(min = 4, max = 32)
    //@Column( unique = true)
    private String cod;         //EQP_COD设备号? 本平台自己产生的或照抄老旧平台产生的。

    //光用继承实体类不好解决问题，还是要附加冗余的类别属性；特种设备分类代码 层次码4个字符/大写字母 ；可仅用前1位、前2位或前3位代码；
    private String type;    //设备种类 EQP_TYPE{首1个字符} ,
    private String sort;    //设备类别代码 EQP_SORT{首2个字符} ,
    private String vart;    //设备品种代码 EQP_VART{首3个字符}
    private String subVart;     //SUB_EQP_VART 子设备品种？用于做报告选择模板/收费计算参数。
    private char   reg;   //EQP_REG_STA 注册
    //不能用保留字。private char  use ?　：若用了保留字导致表EQP无法自动建立！
    private char   ust;   //EQP_USE_STA 状态码
    private char   cag;   //IN_CAG 目录属性 1:目录内，2：目录外
    private String cert;    //EQP_USECERT_COD 使用证号
    private String sNo;    //EQP_STATION_COD 设备代码(设备国家代码)
    private String rcod;    //EQP_REG_COD 监察注册代码
    private String level;    //EQP_LEVEL 设备等级//CLASS_COD 产品分类代码
    private String fNo;   //FACTORY_COD  出厂编号
    private String name;    //EQP_NAME 设备名称
    //不能用保留字。private String inner;
    //附加上后更加能精确定位某个地理空间的位置
    private String  plNo;    //EQP_INNER_COD 单位内部编号place No
    //不能用保留字。private String mod;
    private String  model;    //EQP_MOD 设备型号
    private Boolean  cping;   //IF_INCPING 是否正在安装监检//IF_NOREG_LEGAR非注册法定设备（未启用）
    private Boolean  important;   //IF_MAJEQP 是否重要特种设备
    //private Date  instDate;
    private Date    useDt;  //FIRSTUSE_DATE 设备投用日期
    private Date    accpDt;  //COMPE_ACCP_DATE 竣工验收日期
    private Date    expire;  //DESIGN_USE_OVERYEAR设计使用年限 到期年份 //END_USE_DATE 使用年限到期时间
    private Boolean  move;   //IS_MOVEEQP 是否流动设备
    //EQP_AREA_COD定义规律大乱；    //统计和行政含义的地址区分；
    //  private String  area;    //实际应该放入Address中, 暂用； EQP_AREA_COD 设备所在区域
    //  private String addr;    //暂时用 EQP_USE_ADDR 使用地址 //该字段数据质量差！
    private String occasion;    //EQP_USE_OCCA 使用场合
    //楼盘=地址的泛房型表达式;     单独设立一个模型对象。　(楼盘名称)＝使用地点！=使用单位的单位地址。
    //  private Long  buildId;    //暂用 BUILD_ID  楼盘ID
    private float  ePrice=0;   //EQP_PRICE 产品设备价(进口安全性能监检的设备价)(元)
    private String  contact;    //USE_MOBILE 设备联系手机/短信； ?使用单位负责人or维保人员？
    //还没有做出结论判定的，就直接上null；
    private Boolean unqf1;    //NOTELIGIBLE_FALG1 不合格标志1（在线、年度，外检）
    //判定为合格的
    private Boolean unqf2;    //NOTELIGIBLE_FALG2 不合格标志2(机电定检，内检，全面）
    //判定不合格的，以及不合格情形下的报告结论给出的简短的关键字提示。
    private String ccl1;    //LAST_ISP_CONCLU1  '最后一次检验结论1'
    //判定为合格的或者勉强合格的，带注释提示但是合格的， 还没有做出结论判定的，就直接上null；
    private String ccl2;    //LAST_ISP_CONCLU2  '最后一次检验结论2'
    private Date    ispD1;   //LAST_ISP_DATE1最后一次检验日期1【一般是外检或年度在线】
    private Date    ispD2;      //LAST_ISP_DATE2
    //Instant? 纳秒时间,不使用java.util.Date
    //@Field(type = FieldType.Date, format = DateFormat.date_time)
    //private Instant nxtD1;
    private Date nxtD1;      //NEXT_ISP_DATE1下次检验日期1（在线、年度）
    private Date nxtD2;      //NEXT_ISP_DATE2下次检验日期2(机电定检，内检，全面）

    //索引会被自动创建的。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  owner;      //PROP_UNT_ID 产权单位
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "regu_id")
    private Unit  regU;     //REG_UNT_ID 监察注册机构ID //REG_UNT_NAME注册机构名称

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  makeU;     //MAKE_UNT_ID 制造单位ID
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  insU;     //INST_UNT_ID 安装单位ID
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  remU;     //ALT_UNT_ID 改造单位ID
    //可以和使用单位地址不同的。
    //缺省FetchType.EAGER  不管查询对象后面具体使用的字段，EAGER都会提前获取数据。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "pos_id")
    private Address pos;    //多对1，多端来存储定义实体ID字段。 ；地理定位。
    //只要哪个类出现了mappedBy，那么这个类就是关系的被维护端。里面的值指定的是关系维护端
    //缺省FetchType.LAZY  　　 .EAGER
    @ManyToMany(mappedBy="devs" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<Task> task= new HashSet<>();

    //单1次ISP只能做1个EQP;考虑？一次检验很多气瓶？若支持设备汇聚出场编号汇集重新转义呢，1:N子部件设备关联表。
    //Eqp.TASK.ISP  Eqp.ISP {.短路?}  复杂关联关系， 在做EntityGraph选择定义不恰当而貌似可能死循环了？
    //ISP挂接关系到EQP底下还是挂接关系到TASK底下的？不可以两者同时都挂接关联关系，那样就是多余和混淆概念或两种分歧路径，数据多了而且还产生不一致了。
    //检验单独生成，TASK和EQP多对多的；单个ISP检验为了某个EQP和某个TASK而生成的。
    //先有派出TASK，后来才会生成ISP； 两个地方都必须维护数据的。
    //缺省FetchType.EAGER  LAZY
    @OneToMany(mappedBy="dev" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<ISP>  isps;

    //底下这两组实际相当于内嵌结构对象，或者说[mtU，mtud]是复合字段的。单位ID+分支部门ID配套的才能完全表达出来。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit   mtU;     //MANT_UNT_ID 维保单位ID maintUnt
    //针对维保单位的细化　分支机构部门。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Division mtud;     //.MANT_DEPT_ID 监察才关心的	 .MANT_UNT_ID	is '维保单位ID'
    //若是个人就一定没分支部门；[useU，usud]复合字段的；
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  useU;     //USE_UNT_ID 使用单位ID
    //针对使用单位的细化　管理分支部门。
    //假如设备表没有指定Division部门的，那就是Unit作为缺省部门:等价于该单位底下没有细分的部门，若要求具体Division但是该单位没有细分Division情形。
    //MGE_DEPT_TYPE若=2：TB_UNT_SECUDEPT关联; MGE_DEPT_TYPE若=1很少作废了TB_UNT_DEPT关联
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Division usud;     //.SECUDEPT_ID	 '分支机构ID'  .SAFE_DEPT_ID '安全管理部门ID'

    //@Transient用法，非实际存在的实体属性，动态生成的实体临时属性字段。
    //大规模数据集查询不可用它，效率太慢，应该。。
    //本函数执行之前，JPA数据实际已都取完成了。
    //安全考虑，过滤isps字段合理输出,代替原来缺省的getXXX
    //@org.springframework.data.annotation.Transient  俩个注解都一样
    @Transient
    public Set<ISP>  meDoIsp(){         //若是getMeDoIsp()名字，REST会使用它序列化输出,getXXX都是。
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
*/

//注解定制索引，没啥实际意义。
//@Table(indexes={ @Index(name="type_idx",columnList="type"),
//         　 @Index(name="factoryNo_idx",columnList="fNo")  } )

//二级缓存可移植性@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Fast") 这里region是按照配置来区分的区分标识，竟然不省略。
