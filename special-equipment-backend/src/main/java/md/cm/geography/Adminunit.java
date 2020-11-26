package md.cm.geography;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

//聚合表;
//最小的行政单位;  数据库搜索查询基础对象。
//自贸区？，一个地址既是普通行政区属，又是自贸区属: 自贸区可以包括多个最小的行政单元单元；福州片区、厦门片区和平潭片区还要区分；附带关联属性表的？if(IN[,])else;
//没法：自贸区比起最小的行政单元还要更加的细分的地域概念，和楼盘很像，但是会跨越多行政区划的。自贸区只能额外增加标识关联属性？特别对待区域标志符号。


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Adminunit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //前缀行政地理描述部分， 规范地址命名空间
    //在最小的行政单元内部　prefix　名称唯一的。
    //地址名字不一定要指明街道名称的。 五四路241号XX大厦23#308;
    private String  prefix;    //街道'乡'镇';但允许街道名称省略掉。 鼓楼区就行，不一定要加上街道称呼。
    //旧平台 ， 外部地理系统的对接的 地区码。
    //行政区划代码9位数字;350100 福州市; 350101 市辖区　350102 鼓楼区 350181 福清市　350182 长乐区市; 福建省350000;

    //平潭和福州区划代码如何区分开        //编码规则已经被破坏。
    //行政区划代码9位数字编码失去意义了。
    private String  areacode;
    //老去的 邮政编码；
    private String  zipcode;

    //一个乡镇社区行政最小单元底下的　所有已经声明的地址。
    @OneToMany(mappedBy = "ad")
    private Set<Address>  adrs;
    //JoinColumn 的 name 和 referencedColumnName 指的都是数据库的字段名，不是 Entity 的属性。
    //行政区划4个等级+1的； 用于提高搜索判定速度。
    //1:1关联； Adminunit本id对应Town的ID； 本来应当这张表添加1:1关联id字段。
    //1 ：1关系，关系是本类来维护，添加外键指向对方实体表的主键；
    //本类来维护1：1缺省的字段关联名字；@JoinColumn(name = "townID我这一边的关联字段不一定是id", referencedColumnName = "ID是对方的ＩＤ")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn( referencedColumnName = "ID")
    private Town town;         //最小的1:1关系。


    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "county_id")
    private County  county;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    //一个乡镇社区行政最小单元底下的，所有的 俗称的 楼盘。
    @OneToMany(mappedBy = "ad")
    private Set<Village>  vlgs;
}


/*
行政区划代码代码从左至右的含义是：第一、二位表示省（自治区、直辖市、特别行政区）、第三、四位表示市（地区、自治州、盟及国家直辖市所属市辖区和县的汇总码）、
第五、六位表示县（市辖区、县级市、旗）、第七至九位表示乡、镇（街道办事处）。
邮政编码6位数编码,前两位数字表示省（直辖市、自治区）；前三位数字表示邮区；前四位数字表示县（市）；最后两位数字表示投递局（所）。
中国有省级34个，地级333个，县级2862个，乡镇级41636个。
*/

//CascadeType.ALL（各种级联操作）CascadeType.DETACH  默认情况没有级联操作。  https://www.jianshu.com/p/e8caafce5445
