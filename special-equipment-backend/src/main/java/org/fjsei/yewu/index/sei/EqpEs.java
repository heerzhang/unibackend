package org.fjsei.yewu.index.sei;

import lombok.*;
import md.cm.geography.Address;
import md.cm.unit.Unit;
import md.specialEqp.Equipment;
import md.specialEqp.inspect.ISP;
import md.specialEqp.inspect.Task;
import org.elasticsearch.common.util.set.Sets;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;



@Document(indexName = "eqps")
@Data
//@Builder(toBuilder = true)
//@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class EqpEs implements Equipment{
    @Id
    protected Long id;

    @Field
    private String cod;         //设备号
    //该条设备记录已被设置成了删除态不再有效，就等待以后维护程序去清理这些被历史淘汰的数据了。
    private Boolean valid=true;
   // @PropertyDef(label="监察识别码")    数据库建表注释文字。
    @Field
    private String oid;
    //光用继承实体类不好解决问题，还是要附加冗余的类别属性；特种设备分类代码 层次码4个字符/大写字母 ；可仅用前1位、前2位或前3位代码；
    private String type;    //EQP_TYPE{首1个字符} ,
    private String sort;    //类别代码 EQP_SORT{首2个字符} ,
    private String vart;    //设备品种代码 EQP_VART{首3个字符}

    private Unit ownerUnt;
    //缺省FetchType.EAGER  不管查询对象后面具体使用的字段，EAGER都会提前获取数据。

    private Address pos;    //多对1，多端来存储定义实体ID字段。 ；地理定位。

    private Unit maintUnt;
  //  private Date instDate;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant nextIspDate1;
   // @GeoPointField
   // @Field(ignoreFields=) 只是忽略注解下的字段中的字段，而不是忽略这个字段本身；
    private String factoryNo;   //出厂编号
    //这个注释加不加都一样的。
    @Field(type = FieldType.Nested)
    private Set<TaskEs> task = Sets.newHashSet();
   // private Set<ISP>  isps;

    public EqpEs(String cod, String type, String oid){
        this.cod=cod;
        this.type=type;
        this.oid=oid;
    }
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

*/

