package md.cm.base;

import lombok.*;
import md.cm.geography.Address;
import org.fjsei.yewu.entity.fjtj.UntMge;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

//外部管理的数据源，如何对接？我方只能读的，同步对方数据源的更新，高层次的接口，频率一致性保证；
//假如是直接用接口/微服务/Rest方式，那么存储是在对方，搜索引擎也是在对方，前端只是直接连接调用对方的API接口？授权使用问题；或者后端二传手中继。
//但若失去外部系统访问授权可保障性；只能自己拷贝数据/一部分我方用到的大数据，面临更新滞后和不一致问题？本地建库表no是外部关键ID标识，id是我方内部1:1对应ID。

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region ="Slow")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String name;    //UNT_NAME
    private String no;  //身份证号码 UNT_ORG_COD
    //注册地址，证件上记载的地址。
    private String address;     //住址，身份证地址  也可以保留
    //当前实际上的地址，最新办公/流动人口地址。
    @ManyToOne(fetch= FetchType.LAZY)
    private Address pos;    //多对1，；地理定位 方式。

    private String phone;       //个人手机号
    private String gender;        //性别 男 女 中
    private String occupation;        //职业

    public void copyAs(UntMge untMge){
        //依照老旧平台来比较修改。
        if(no==null || !no.equals(untMge.getUNT_ORG_COD()) )               no=untMge.getUNT_ORG_COD();
        if(phone==null || !phone.equals(untMge.getUNT_MOBILE()) )       phone=untMge.getUNT_MOBILE();
        if(address==null || !address.equals(untMge.getUNT_ADDR()) )     address=untMge.getUNT_ADDR();
    };
}


