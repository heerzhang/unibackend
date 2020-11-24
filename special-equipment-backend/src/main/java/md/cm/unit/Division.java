package md.cm.unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import md.cm.base.Company;
import md.cm.base.Person;
import md.cm.geography.Address;
import md.specialEqp.Eqp;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Set;

//分支机构branch office｛安全管理部门或分支机构｝；　　实际是关注到了某个企业的内部组织架构；
//因为监察要求把证书，或资格，或其它管理约束规定，最小划分单元进一步细化，细化到了企业部门级别。
//维保单位管理关注到下设置 有细分的维保部门; 驻点挂接/解除维保 驻点设备
//使用单位管理关注到下设置 有细分的分支机构|内部设立管理部门；


@AllArgsConstructor
@Data
@Entity
@Builder
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region ="Slow")
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //该字段淘汰：避免多头维护数据，直接引用关联属性类company或person中的name字段,与Unit是1对1关系。
    //不建议保留name字段。name搜索都绕道company或person,实在必要可以unit.company OR unit.person is。
    private String name;    //名字　   单位名||'-'||分支机构名
    private String linkMen;     //TB_EQP_MGE.USE_LKMEN  '操作人员/联系人'
    private String phone;       //TB_EQP_MGE.USE_PHONE  '联系人电话'

    //分支机构才需要设置正式的地址字段。
    //安全管理部门不能设置正式的地址字段/可有内部说话的办公地点。
    //对外声称正式地址；　临时过渡　保留用
    private String address;     //UNT_ADDR
    //单向关联，　对方可不必知道我这一方的存在。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Address pos;    //多对1，多端来存储定义实体ID字段。 ；地理定位。

    //MGE_DEPT_TYPE=1-分支机构.SECUDEPT_ID 关联TB_UNT_SECUDEPT; if=2-安全管理部门.SAFE_DEPT_ID  关联TB_UNT_DEPT

    //使用单位用到了
    @OneToMany(mappedBy = "usud")
    private Set<Eqp> uses;    //在用设备集合

    //维保单位用到了
    @OneToMany(mappedBy = "mtud")
    private Set<Eqp> maints;    //维保设备集合


    //本分支机构到底归属于哪一个单位的？　管理意义上的细化：　单位Unit 1-->N 分支。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  unit;
}


//MGE_DEPT_TYPE=1安全管理部门TB_UNT_DEPT，MGE_DEPT_TYPE=2分支机构TB_UNT_SECUDEPT
