package md.cm.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.cm.geography.Address;
import md.specialEqp.Eqp;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Set;

//单位底下细分出来的分支机构branch office｛安全管理部门或分支机构｝；　　实际是关注到了某个企业的内部组织架构；
//因为监察要求把证书，或资格，或其它管理约束规定，最小划分单元进一步细化，细化到了企业部门级别。
//维保单位管理关注到下设置 有细分的维保部门; 驻点挂接/解除维保 驻点设备
//使用单位管理关注到下设置 有细分的分支机构|内部设立管理部门；


@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region ="Slow")
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //该字段淘汰：避免多头维护数据，直接引用关联属性类company或person中的name字段,与Unit是1对1关系。
    //不建议保留name字段。name搜索都绕道company或person,实在必要可以unit.company OR unit.person is。
    private String name;    //名字　   单位名||'-'||分支机构名
    //Eqp表当中的设备联系人 使用单位联系人 可能替换该字段。　原来每一个设备都有自己的联系人，新版联系人从设备表移挂到单位机构表下。
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
    //过渡用　地区代码
    private String  area;   //UntSecudept UntDept . 　_AREA_COD
    //MGE_DEPT_TYPE=1-分支机构.SECUDEPT_ID 关联TB_UNT_SECUDEPT; if=2-安全管理部门.SAFE_DEPT_ID  关联TB_UNT_DEPT

    /*使用单位用到了
    分支机构， 任务生成区分不同的分支(根本就不同的科室负责的作业对象吧)？没啥可再去分。
    出发点？监管细分照顾的？还是检验业务收钱对象差异，或是报告证书发送对象差异。
    单位分解规则，机构识别代码 统一社会信用代码 UNT_ORG_COD。大公司可以分开多个信用代码=多个单位。
    */
    @OneToMany(mappedBy = "usud")
    private Set<Eqp> uses;    //在用设备集合

    //维保单位用到了
    @OneToMany(mappedBy = "mtud")
    private Set<Eqp> maints;    //维保设备集合


    //本分支机构到底归属于哪一个单位的？　管理意义上的细化：　单位Unit 1-->N 分支。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private Unit  unit;
    //目前，假设UntSecudept 和UntDept 两个表的ID不重复！
    private Long oldId;  //对接旧系统 内设管理部门 .SAFE_DEPT_ID  关联TB_UNT_DEPT ,内设分支机构 .SECUDEPT_ID 关联TB_UNT_SECUDEPT
}


//MGE_DEPT_TYPE=1安全管理部门TB_UNT_DEPT，MGE_DEPT_TYPE=2分支机构TB_UNT_SECUDEPT
/*
TB_EQP_MGE.USE_UNT_ID is '使用单位ID'   检验数据库: 使用单位用  管理部门类型，0:无内设
if MGE_DEPT_TYPE=2-    内设分支机构 .SECUDEPT_ID 关联TB_UNT_SECUDEPT;
if=1-内设管理部门 .SAFE_DEPT_ID  关联TB_UNT_DEPT
维保单位用     comment on column TB_EQP_MGE.MANT_UNT_ID is '维保单位ID'
comment on column TB_EQP_MGE.MANT_DEPT_ID is '维保部门ID' ! ! 检验都是空着的
*/
