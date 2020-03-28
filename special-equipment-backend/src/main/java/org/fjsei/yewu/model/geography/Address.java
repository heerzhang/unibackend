package org.fjsei.yewu.model.geography;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fjsei.yewu.entity.sei.EQP;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.*;
import java.util.Set;
//联合主键类比ID字段，并不是 联合唯一约束，两个概念用法不同。 主键只能有一个，但是唯一约束搞可以多个的。而ID关系到JPA接口方法引用。

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"name", "aid"})} )
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //【后半部分】非行政的，用户地址命名空间 部分。
    private String  name;     //'[前缀不需要]单位详细地址，门牌号'； 前面行政地理描述部分要省略掉。
    //【前缀，行政地理描述部分】     用于提高搜索判定速度。
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "aid")
    private Adminunit  ad;       //行政区划
    //地址需要再次丰富掉， 省 市 区 镇 小区。
    // private String  area;   //地区码 "zipCode": "",          area;   //地区码
    //UnitAddress 广域 1 : N Position 门牌栋号　+。
    private double lat;  //纬度 ;精确到小数点后6位可达约1米精度。
    private double lng;  //经度  前面的是纬度,后面的是经度
    //楼盘小区 大厦名字，可另外并行独立出去。              String building;
    @OneToMany(mappedBy = "pos")
    private Set<EQP> eqps;      //双向的关联，需要在外部实体表内也要声明，虽然数据库实体表没有字段，但内存操作需要它。

    //测试
    @PreAuthorize("hasRole('ADMIN')")
    public boolean setLngAndLat(String lat, String lng){
        this.lng=Double.parseDouble(lng);
        this.lat=Double.parseDouble(lat);
        return true;
    }
}


//能减少重复性录入的随意性，地址字符串实体化。 已经输入生成的就直接能选择关联旧的地址登记字符串。同时隐含地就选定了地区编码xxx_AREA_COD。
//旧平台设备表登记的EQP_USE_ADDR; '使用地点'==》 强化变成Address关联表。 原来是直接任意字符串字段。现在是签了ID的独立一个地址表，可重复性关联。
//这里name+Adminunit是唯一索引约束：重复报错violation，要尽量约束name的同义词减少重复数据量,注册地址需要核准制度。
