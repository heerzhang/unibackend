package md.cm.geography;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.specialEqp.Eqp;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.*;
import java.util.Set;
//联合主键类比ID字段，并不是 联合唯一约束，两个概念用法不同。 主键只能有一个，但是唯一约束搞可以多个的。而ID关系到JPA接口方法引用。
//地址的数据维护可以拆分成2-3个部分，独立分权管理。
//自贸区的地域概念，自贸区只能额外增加标识关联属性？特别对待区域标志符号，多对多的关联附表：特殊区域概念名词实体表。


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

    //【后半部分】非行政的，用户地址命名空间 部分。  楼盘不属于地址组成部分/楼盘属于名义地址。
    private String  name;     //'[前缀不需要]单位详细地址，门牌号'； 前面行政地理描述部分要省略掉。
    //配合Geo/lon/lat: 立体的位置坐标，位于大厦的第几层位置。

    //【前缀，行政地理描述部分】     用于提高搜索判定速度。
    //正常这个字段不能为空的！
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "aid", nullable=false)
    private Adminunit  ad;       //行政区划

    //地址需要再次丰富掉， 省 市 区 镇 小区。
    // private String  area;   //地区码 "zipCode": "",          area;   //地区码
    //UnitAddress 广域 1 : N Position 门牌栋号　+。
    //经纬度坐标小数位与精度的对应关系 https://www.jianshu.com/p/cff30c491a0b
    private Double lat;  //纬度 ;精确到小数点后6位可达约1米精度。
    private Double lon;  //经度  前面的是纬度,后面的是经度
    //Point ( x=lat  ,y=lon ); "lat": 48.86111099738628, "lon": 2.3269999679178
    //private Point  pt;  直接序列化占用mysql磁盘很大，pt=79个字节。

    //地理定位。  .EQP_LONG is '地理经度'     .EQP_LAT is '地理纬度'

    //楼盘，并非是强制都要求的地址管理部分，可以空缺。
    //楼盘小区 大厦名字，可另外并行独立出去。              String building;
    //广义模糊地址；
    //楼盘=地址的泛房型表达式;     单独设立一个模型对象。　(楼盘名称)＝使用地点！=使用单位的单位地址。
    //private Long  buildId;    //暂用 BUILD_ID  楼盘ID
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "vid")
    private Village  vlg;       //楼盘

    //保持独立性：不需要双向的关联
    @OneToMany(mappedBy = "pos")
    private Set<Eqp> eqps;      //双向的关联，需要在外部实体表内也要声明，虽然数据库实体表没有字段，但内存操作需要它。

    //测试　
    @PreAuthorize("hasRole('ADMIN')")
    public boolean setLatAndLon(String lat, String lng){
        this.lon =Double.parseDouble(lng);
        this.lat=Double.parseDouble(lat);
        return true;
    }

    /**
     EQP_USE_PLACE 设备使用场所--字典库； 只有4000起重机械才会使用登记证上注明本字段的{混淆！}; JC_TASK_EQP,报告会用到。
     EQP_USE_OCCA 使用场合 EQP_USE_OCCA起重机械才用;
     EQP_USE_PLACE=[{id:'1',text:'公众聚集场所（学校）'},{id:'2',text:'公众聚集场所（幼儿园）'},{id:'3',text:'公众聚集场所（医院）'},
     {id:'4',text:'公众聚集场所（车站）'},{id:'5',text:'公众聚集场所（客运码头）'},{id:'6',text:'公众聚集场所（商场）'},{id:'7',text:'公众聚集场所（体育场馆）'},
     {id:'8',text:'公众聚集场所（展览馆）'},{id:'9',text:'公众聚集场所（公园）'},{id:'10',text:'公众聚集场所（其它）'},{id:'11',text:'住宅'}];
     IF_PUBLIC_AREA 是否公共领域（0不是，1是）:已被删除字段?
     IF_MAJPLACE 是否在重要场所{没实质性意义，只是显示标记的},
     IF_POPULATED 是否人口密集区{没实质性意义，只是显示标记的}
     * */
}



//能减少重复性录入的随意性，地址字符串实体化。 已经输入生成的就直接能选择关联旧的地址登记字符串。同时隐含地就选定了地区编码xxx_AREA_COD。
//旧平台设备表登记的EQP_USE_ADDR; '使用地点'==》 强化变成Address关联表。 原来是直接任意字符串字段。现在是签了ID的独立一个地址表，可重复性关联。
//这里name+Adminunit是唯一索引约束：重复报错violation，要尽量约束name的同义词减少重复数据量,注册地址需要核准制度。
//设备空地址还不少啊：select  * from TB_EQP_MGE A  where  EQP_USE_ADDR is null and IN_CAG=1 and EQP_USE_STA=2 and EQP_REG_STA=1 and OIDNO is not null and EQP_AREA_COD is not null
