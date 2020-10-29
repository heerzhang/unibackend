package md.cm.unit;

import lombok.*;
import md.cm.base.Company;
import md.cm.base.Person;
import md.specialEqp.Eqp;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Set;
//设备中的单位， 即可以是公司，也可以是个人。company和person是大数据的影子实体类/只能读。Unit是本地附加的属性。
//搜索引擎ES找到company或者person的id后，就能通过Unit的关联ID和数据库索引快速找到其它相关的字段属性，比如owns设备集合。


@AllArgsConstructor
@Data
@Entity
@Builder
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region ="Slow")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //该字段淘汰：避免多头维护数据，直接引用关联属性类company或person中的name字段,与Unit是1对1关系。
    //不建议保留name字段。name搜索都绕道company或person,实在必要可以unit.company OR unit.person is。
    private String name;    //UNT_NAME
    private Long oldId;      //UNT_ID
    private Long jcId;  //JC_UNT_ID
    private String address; //UNT_ADDR
    private String linkMen; //UNT_LKMEN
    private String  indCod;  //行业性质INDUSTRY_PROP_COD    认定为个人Z01||length(a.UNT_NAME)<<3;
    private String phone;
    //加载方式修改影响很大。根据业务场景挑选。懒加载了若想关联内省查询会运行错误。
    @OneToMany(mappedBy = "owner")
    private Set<Eqp> owns;
    //默认采用LAZY方式加载实体,懒加载时加了@Transactional的查询才能不报错，但是graphQL内省阶段是与入口函数分离的=还是报错。
    //一对多或多对多时，默认懒加载，graphQL遇到这个字段，若想要顺着关联查询下去，程序报错，等于有一种信息安全控制机制。
    //懒加载的坏处，该字段代码不能直接使用，必须绕道，从反向关系依据id倒着查。
    @OneToMany(mappedBy = "mtU")
    private Set<Eqp> maints;    //维保设备集合

    //这里company,person两个，若采用接口/微服务/Rest方式，实际上本地无需DB库表实体类，只需要外部大数据库no以及类型标识。
    //但我这里采用本地维护模式，Company和Person可以直接使用来关联,两个id，不需要类型标识。

    //1:1关联； Adminunit本id对应Town的ID； 本来应当这张表添加1:1关联id字段。
    //1 ：1关系，关系是本类来维护，添加外键指向对方实体表的主键；
    //本类来维护1：1缺省的字段关联名字；@JoinColumn(name = "townID我这一边的关联字段不一定是id", referencedColumnName = "ID是对方的ＩＤ")
    //对方Company类 根本不知道我方的存在感。搜索Company获得company_id来我这表做company_id索引的再次查询。
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn( referencedColumnName = "ID")
    private Company company;        //直接代替实体类继承模式，改做1:1关系。
    //company person两个只能二选一，company字段非空的那么company就算优先。
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn( referencedColumnName = "ID")
    private Person person;        //直接代替实体类继承模式，改做1:1关系。


    public Unit() {
    }
    public  Unit(String name,String address){
        this.name=name;
        this.address=address;
    }
    public Set<Eqp>  getMaints() {
        return  this.maints;
    }
}


/*
应和大库思维，把unit 使用单位，继续拆分成了：person + company 优化存储语义；前端界面提前区分两种unit。
company 法人单位,其他组织
person 个人
unit是业务代理者，company和person是通用的大数据基础库。
company和person两个实体类的库表数据很可能来自外部同步过来的，数据维护负责是其他方面的人。
*/
