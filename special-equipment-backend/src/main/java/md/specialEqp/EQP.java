package md.specialEqp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.cm.unit.Unit;
import md.specialEqp.inspect.ISP;
import md.specialEqp.inspect.Task;
import md.cm.geography.Address;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//同名冲突！@Cache不是来自javax.persistence.*的，所以添加org.hibernate.annotations.Cache在其上方。或直接@org.hibernate.annotations.Cache上了。

//云数据库网关服务 支持跨DB实例SQL关联查询; 但是无法确保事务，数据一致性毛病？

//懒加载导致了N+1问题(按照后面逻辑代码需求再去那可能的会执行N条SQL)，假设不用懒加载的EAGER话就会强行加载可能根本就不会用到的大量的关联数据(不是更浪费?)。
//EntityGraph是和LAZY相反的？，总体写死掉策略搞lazy，动态的个性化查询用EntityGraph来提示{深度定制的,细化,仅针对个别使用到的字段的}，俩个机制的目标完全冲突。
//Lazy字段才需要搞@NamedEntityGraph的，嵌套Lazy字段/下一级Lazy属性字段用@NamedSubgraph。目的提前join取数据,减少sql语句数,能提高效率。不是FetchType.LAZY的就没必要@EntityGraph。
//举例@NamedEntityGraph(name="EQP.task",attributeNodes={@NamedAttributeNode("task"),@NamedAttributeNode("isps")}) 关联不密切的关联对象一次性join=会产生爆炸记录数；
//EntityGraph不可随便加;就为了多出一个isps关联对象left outer join ？,对多出join约束性少引起笛卡儿积级别记录个数爆炸，本来只有290条变成12588条了。
//EntityGraph存在理由:提示JPA去屏蔽LAZY，用JOIN FETCH一次关联表全查，减少SQL语句(规避了1+N 问题)，从而提高速度；但也失去懒加载优点。https://blog.csdn.net/dm_vincent/article/details/53366934
//对于@NamedEntityGraphs({ @NamedEntityGraph每条定义尽量精简，不要太多字段，必须每一条/每一个接口都要测试对比/打印调试hibernate SQL。


@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Slow")
public class EQP implements Equipment{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    //JPA中，使用@Version来做乐观锁的事务控制。对比的,悲观锁限制并发所以很少被用的。
    //乐观锁同步用，［注意］外部系统修改本实体数据就要改它时间一起commit事务。@Version防第二类更新丢失；
    @Version
    private Timestamp version;
  //  @Transient
  //  private String dtype;    //从hibernate取的继承类区分标识

    @Size(min = 5, max = 30)
    @Column( unique = true)
    private String cod;         //设备号

    //该条设备记录已被设置成了删除态不再有效，就等待以后维护程序去清理这些被历史淘汰的数据了。
    @NotNull
    private Boolean valid=true;

   // @PropertyDef(label="监察识别码")    数据库建表注释文字。
    @Column(length =128, unique = true)
    private String oid;
    //光用继承实体类不好解决问题，还是要附加冗余的类别属性；特种设备分类代码 层次码4个字符/大写字母 ；可仅用前1位、前2位或前3位代码；
    private String type;    //EQP_TYPE{首1个字符} ,
    private String sort;    //类别代码 EQP_SORT{首2个字符} ,
    private String vart;    //设备品种代码 EQP_VART{首3个字符}


    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit ownerUnt;
    //缺省FetchType.EAGER  不管查询对象后面具体使用的字段，EAGER都会提前获取数据。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "pos_id")
    private Address pos;    //多对1，多端来存储定义实体ID字段。 ；地理定位。

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit maintUnt;
    private Date instDate;
    private String factoryNo; //出厂编号
    //只要哪个类出现了mappedBy，那么这个类就是关系的被维护端。里面的值指定的是关系维护端
    //缺省FetchType.LAZY  　　 .EAGER
    @ManyToMany(mappedBy="devs" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<Task> task= new HashSet<>();

    //单1次ISP只能做1个EQP;考虑？一次检验很多气瓶？若支持设备汇聚出场编号汇集重新转义呢，1:N子部件设备关联表。
    //EQP.TASK.ISP  EQP.ISP {.短路?}  复杂关联关系， 在做EntityGraph选择定义不恰当而貌似可能死循环了？
    //ISP挂接关系到EQP底下还是挂接关系到TASK底下的？不可以两者同时都挂接关联关系，那样就是多余和混淆概念或两种分歧路径，数据多了而且还产生不一致了。
    //检验单独生成，TASK和EQP多对多的；单个ISP检验为了某个EQP和某个TASK而生成的。
    //先有派出TASK，后来才会生成ISP； 两个地方都必须维护数据的。
    //缺省FetchType.EAGER  LAZY
    @OneToMany(mappedBy="dev" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<ISP>  isps;


    public  EQP(String cod,String type,String oid){
        this.cod=cod;
        this.type=type;
        this.oid=oid;
    }
    //@Transient用法，非实际存在的实体属性，动态生成的实体临时属性字段。
    //大规模数据集查询不可用它，效率太慢，应该。。
    //本函数执行之前，JPA数据实际已都取完成了。
    //安全考虑，过滤isps字段合理输出,代替原来缺省的getXXX
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

/* 删除没用的头注解： 用@EntityGraph(value="EQP.task",)做查询优化可不容易掌控的；很容易出笛卡儿积爆炸问题。
@NamedEntityGraphs({
        @NamedEntityGraph(name= "EQP.task",
                attributeNodes = {
                        @NamedAttributeNode(value= "task",subgraph= "taskg"),
                },
                subgraphs = {       嵌套的指示，下一级关联对象的hits;
                        @NamedSubgraph(name = "taskg", attributeNodes =
                                { @NamedAttributeNode("isps"),  }
                        ),
                }
        ) ,
        @NamedEntityGraph(name = "EQP.isps",
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
        @NamedEntityGraph(name = "EQP.all",    某个场景用一个hint;
                attributeNodes = {}  )      //实际上attributeNodes可以多个，但是特别小心，关联不密切的关联对象一次性join=会产生爆炸记录数！！attributeNodes只做一个较妥。
        @NamedEntityGraph(name = "EQP.special",    另外一个场景用另外一个hint;
                attributeNodes = {}  )
    })
join爆炸记录数范例 @NamedEntityGraph( name="EQP.task",attributeNodes={　@NamedAttributeNode("task"),　@NamedAttributeNode("isps")　} )  无关的task+isps搞在一起＝爆炸。
*/

//注解定制索引，没啥实际意义。
//@Table(indexes={ @Index(name="type_idx",columnList="type"),
//         　 @Index(name="factoryNo_idx",columnList="factoryNo")  } )

//二级缓存可移植性@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Fast") 这里region是按照配置来区分的区分标识，竟然不省略。
