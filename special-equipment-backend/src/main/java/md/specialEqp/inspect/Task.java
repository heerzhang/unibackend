package md.specialEqp.inspect;
//任务单，检验的业务起始点。
import lombok.Getter;
import lombok.Setter;
import md.specialEqp.Eqp;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

//旧平台1个TASK映射1个ISP{可以多分项SubISPid}；Isp流转后出报告内容。
//这里放置每次作业时刻定夺动态的参数。

@Getter
@Setter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Fast")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //todo: 1个任务只有1个设备,没有关联设备，虚拟设备（煤气罐系列化的设备号01..09排除05}），管道特别[选择几个单元为了报告/计费]。
    //我是关系维护方,必须在我这save()，　缺省.LAZY;
    @ManyToMany
    @JoinTable(name="TASK_DEVS", joinColumns={@JoinColumn(name="TASK_ID")}, inverseJoinColumns={@JoinColumn(name="DEVS_ID")}
                //indexes={@Index(name="DEVS_ID_idx",columnList="DEVS_ID")}
            )
    private List<Eqp> devs;
    //管道检查　监察的处理较为特殊，可分解开成多个任务单, 1 Eqp配合 多 pipeUnit两个实体分解。
    //todo：子设备列表-管道单元的选择列表。管道特别：每个单元下次定检日期都不一样(可分开多批次的任务)。

    //对方ISP去维护这个关联关系。
    //一个任务单Task包含了多个的ISP检验记录。 　任务1：检验N；
    //默认fetch= FetchType.LAZY; 而mappedBy代表对方维护关系  EAGER
    @OneToMany(mappedBy = "task" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Fast")
    private Set<Isp>  isps;

    private String dep;  //类型弱化? ,应该是部门表的ID!

    //ORACLE: 表名USER不能用，字段date不能用。
    @Column(name="TASK_DATE")
    private Date date;
    private String status;
    private String  fee;
    //[两个归并字段]只是前端显示区别；.IF_AQ_TEST '4000是否安全监控管理系统试验验证'
    private Boolean test;   //.IF_WORKEQP_TEST '5000厂车是否工作装置测试'

    private Float cost;  //TB_TASK_MGE.INST_PRICE单台工程施工费（单位:万元）


}


/*
如果是EAGER，那么表示取出这条数据时，它关联的数据也同时取出放入内存中;用EAGER时，因为在内存里，所以在session外也可以取。
如果是LAZY，那么取出这条数据时，它关联的数据并不取出来，在同一个session中，什么时候要用，就什么时候取(再次访问数据库)。
但在session外就不能再取了,failed to lazily initialize a collection报错；因为操作实际被多场次的数据库session分开。
查询一个graphQL按策略执行器（因为性能要求）＝＝〉并发分解的和异步的策略＝＝〉导致分配到了多个数据库session了。
*/


