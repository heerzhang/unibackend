package md.cm.base;

import lombok.Getter;
import lombok.Setter;
import md.cm.unit.Unit;
import md.specialEqp.EQP;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
//不采用接口/微服务/Rest方式，扯皮和费用授权问题多；反正这个大数据类别的事实上的实时一致性要求很低。
//采用本地维护模式，只能自己拷贝数据/一部分我方用到的大数据，面临更新滞后和不一致问题？本地建库表no是外部大数据库关键ID标识，id是我方内部1:1对应ID。

@Getter
@Setter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region ="Slow")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String name;
    private String no;   //统一社会信用代码
    private String address;     //首要办公地点
    private String linkMen;     //对外 负责人
    private String phone;

    //若需要双向的1 ：1关系，关系是Unit类维护； 就把下面俩个行添加上。 太多了？很多的业务很多模块插入关联关系字段？
    //@OneToOne(mappedBy = "company")
    //private Unit  unit;       //只能有一个了。


}


//统一社会信用代码是指对中华人民共和国境内每个依法成立和依法注册的单位由各级质量技术监督部门颁发的一个在全国范围内唯一的、始终不变的代码标识,其作用相当于单位的身份证。

