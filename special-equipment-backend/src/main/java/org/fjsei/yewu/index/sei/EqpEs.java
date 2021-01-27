package org.fjsei.yewu.index.sei;

import lombok.*;
import md.cm.unit.Unit;
import md.specialEqp.Equipment;
import md.specialEqp.RegState_Enum;
import md.specialEqp.UseState_Enum;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import javax.persistence.*;
import java.util.Date;

//不可直接在JPA原生Eqp实体类基础上合并注解@Document方式，把Elasticsearch和JPA实体定义凑合在一个java文件中。
//独立定义ES模型实体类，支持非规范化和宽表等处理需求。

//设备Eqp对应的ES索引库,  从设备角度来过滤搜索。
//无法改代码的生产系统情形，最好用index别名xxx_latest，方便维护。

/**搜索Eqp设备用：这里字段只需要那些需要作为搜索条件的字段即可，可不考虑稀罕统计需求，常见统计可以满足。
 *
 */

@Document(indexName = "eqp_latest")
@Data
//@Builder(toBuilder = true)
//@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Setting(settingPath = "elastic/esSetting.json")
public class EqpEs implements Equipment{
    //这个id不是这个ES保存时间自动生成的是JPA那边带过来的。
    @Id
    protected Long id;
    //该条设备记录已被设置成了删除态不再有效，就等待以后维护程序去清理这些被历史淘汰的数据了。
    //private Boolean valid;

    //@PropertyDef(label="监察识别码")    数据库建表注释文字。
    //ngram_analyzer缺省3+3配置的，若是少于3个字符就无法搜索出来。最少要求输入3个字符。
    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=32)
            }
    )
    private String oid;

    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=32)
            }
    )
    private String cod;         //设备号

    //光用继承实体类不好解决问题，还是要附加冗余的类别属性；特种设备分类代码 层次码4个字符/大写字母 ；可仅用前1位、前2位或前3位代码；
    @Field(type = FieldType.Keyword)
    private String type;    //EQP_TYPE{首1个字符} ,
    @Field(type = FieldType.Keyword)
    private String sort;    //类别代码 EQP_SORT{首2个字符} ,
    @Field(type = FieldType.Keyword)
    private String vart;    //设备品种代码 EQP_VART{首3个字符}
    @Field(type = FieldType.Keyword)
    private String subv; //SUB_EQP_VART 子设备品种
    /**EQP_REG_STA 注册*/
    @Enumerated
    @Field(type = FieldType.Keyword)
    private RegState_Enum reg;
    //char默认和String一样处理映射_mapping。
    /**EQP_USE_STA 状态码; ES对enum会实际看做FieldType.Keyword来处理的;
     * 不能上@Field(type = FieldType.Byte)
     * JSON.toJSONString(eQP)会直接上字符串的，无法简单从Eqp复制成EqpEs; ES要求Keyword类型。
    */
    @Enumerated
    @Field(type = FieldType.Keyword)
    private UseState_Enum ust;
    @Field(type = FieldType.Boolean)
    private Boolean   ocat;   //IN_CAG 目录属性 1:目录内，2：目录外
    //无法动态修改已经初始化过的字段的类型，需要先删除再来
    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=40)
            }
    )
    private String cert;    //EQP_USECERT_COD 使用证号

    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=40)
            }
    )
    private String sno;    //EQP_STATION_COD 设备代码(设备国家代码)
    @Field(type = FieldType.Keyword)
    private String rcod;    //EQP_REG_COD 监察注册代码
    @Field(type = FieldType.Keyword)
    private String level;    //EQP_LEVEL 设备等级
    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=150)
            }
    )
    private String fno;   //出厂编号
    //  private Unit  owner;
    //缺省FetchType.EAGER  不管查询对象后面具体使用的字段，EAGER都会提前获取数据。

 //   private Address pos;    //多对1，多端来存储定义实体ID字段。 ；地理定位。
 //   private Unit mtU;
    @Field(type = FieldType.Keyword)
    private String name;    //EQP_NAME 设备名称
    //不能用保留字。private String inner;
    //附加上后更加能精确定位某个地理空间的位置
    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=100)
            }
    )
    private String  plno;    //EQP_INNER_COD 单位内部编号place No
    //不能用保留字。private String mod;
    @MultiField(mainField= @Field(type=FieldType.Text),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=50)
            }
    )
    private String  model;    //EQP_MOD 设备型号
    private Boolean  cping;   //IF_INCPING 是否正在安装监检//IF_NOREG_LEGAR非注册法定设备（未启用）
    private Boolean  vital;
    //  private Date instDate;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date    used;  //FIRSTUSE_DATE 设备投用日期
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date    accd;  //COMPE_ACCP_DATE 竣工验收日期
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date    expire;  //DESIGN_USE_OVERYEAR设计使用年限 到期年份 //END_USE_DATE 使用年限到期时间
    private Boolean  move;   //IS_MOVEEQP 是否流动设备
 //   @Field(type = FieldType.Keyword)
  //  private String  area;    //实际应该放入Address中, 暂用； EQP_AREA_COD 设备所在区域

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String addr;    //暂时用 EQP_USE_ADDR 使用地址 //该字段数据质量差！
    @Field(type = FieldType.Keyword)
    private String occa;    //EQP_USE_OCCA 使用场合

    private Float  money;   //EQP_PRICE 产品设备价(进口安全性能监检的设备价)(元)
    //@Field(type = FieldType.Keyword)
    //private String  contact;    //USE_MOBILE 设备联系手机/短信； ?使用单位负责人
    //还没有做出结论判定的，就直接上null；
    private Boolean unqf1;    //NOTELIGIBLE_FALG1 不合格标志1（在线、年度，外检）
    //判定为合格的
    private Boolean unqf2;    //NOTELIGIBLE_FALG2 不合格标志2
    @Field(type = FieldType.Keyword)
    private String ccl1;    //LAST_ISP_CONCLU1  '最后一次检验结论1'
    //判定为合格的或者勉强合格的，带注释提示但是合格的， 还没有做出结论判定的，就直接上null；
    @Field(type = FieldType.Keyword)
    private String ccl2;    //LAST_ISP_CONCLU2  '最后一次检验结论2
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date    ispd1;   //LAST_ISP_DATE1最后一次检验日期1【一般是外检或年度在线】
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date    ispd2;      //LAST_ISP_DATE2
    //有指定FieldType的是启动就会初始化_mapping中出现; 否则是保存才初始化。
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date nxtd1;      //NEXT_ISP_DATE1下次检验日期1（在线、年度）
    //Date字段没注解的，缺省ES映射是给　long　类型。　初始化_mapping时null字段没添加上。
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date nxtd2;      //NEXT_ISP_DATE2下次检验日期2(机电定检，内检，全面）
   // @GeoPointField
   // @Field(ignoreFields=) 只是忽略注解下的字段中的字段，而不是忽略这个字段本身；

    //这个注释加不加都一样的。
 //   @Field(type = FieldType.Nested)
 //   private Set<TaskEs> task = Sets.newHashSet();
   // private Set<Isp>  isps;
    //引入这两个单位字段消耗磁盘空间大
    @Field(type = FieldType.Object)
    private UnitEs  useu;     //USE_UNT_ID 使用单位ID
    //UnitEs并不会当成Java实体对象那样存储，相同UnitEs.id的可以有从ES读取出不同的内容。好像json{}存储那样。
    //@Field(type = FieldType.Object)
    //private UnitEs  owner;      //PROP_UNT_ID 产权单位

    //@Field(type = FieldType.Nested) 集合数组对象的；性能较差；
    //坐标 Object 内部 lat lon: float
    @GeoPointField
    private GeoPoint pt;     //地理坐标 搜索
}




/* 清空elasticsearch-7.9.1\data底下数据可清理旧数据库。数据文件名字格式都会变的；lucene数据存储。
单条记录底下一个字段内容(String)在ES磁盘文件上竟然保存(8份/嵌套6份)，在segment小的时候，segment的所有文件内容都保存在cfs文件中，cfe文件保存了lucene各文件在cfs文件的位置信息
;除了id/bool的字段外,集合对象字段例外。　8份实际上=lucene中各数据结构类型。
Elasticsearch除了存储原始正排数据、倒排索引，还存储了一份列式存储docvalues，用作分析和排序。列式存储用作聚合和排序;
Lucene段要合并：索引段粒度越小，性能低/耗内存。频繁的文档更改导致大量小索引段Segment，从而导致文件句柄打开过多问题。
分词字段：maxFieldLength=1000;默认；一个Field中最大Term数目，超过部分忽略，不会index到field中，所以也就搜索不到。
@lombok.Data相当于@Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode，@lombok.Value这5个注解的合集。
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)类继承时;
@lombok.Builder(toBuilder = true)
@Getter
Elasticsearch创建别名时可以指定路由"routing"　　https://www.xujun.org/note-76931.html
ES过滤使用termQuery例子：boolQueryBuilder.must(termQuery("useU.id",where.getUseUid()));
NativeSearchQueryBuilder().withFilter()只能用在已经统计后的过滤(最后的统计条目过滤)，其它情形不要用；正常查询应该用NativeSearchQueryBuilder().withQuery();
ES:将Geo精度设置到3米,内存占用可以减少62%    https://blog.csdn.net/u012332735/article/details/54971638
ES对Enum 枚举数据类型的处置：转成Keword / String方式的，会自带优化。
_mapping无法直接修改，需要重做index?
*/

