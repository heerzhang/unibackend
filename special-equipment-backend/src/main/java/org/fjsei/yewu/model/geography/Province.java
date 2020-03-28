package org.fjsei.yewu.model.geography;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

//一级行政区= 省、直辖市、自治区、特别行政区;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"name", "country_id"})} )
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //名字是 =搜索用的全名。用于界面交互给用户做列表选项名。 福建，香港，内蒙古，不一定要添加后缀单位的称呼。
    private String  name;        //+'中国的省+直辖市'=美国州+特区，’上海市‘ ？‘香港’； 省、直辖市、新疆 自治区。州，准区划领地，岛屿地区。
    //可以吗？纽约州纽约市xx。 行政区划关系却无法直接代表某地地址名字的生成？关系太离谱了。

    //地级行政区: 地级市、地区、自治州、盟。
    //有些的却是关系少了一行政层次，有些的却是多了一个行政层次。更多层次等级数差距啊，上面有3级的上头有5级的。无法建模！
    //新疆维吾尔自治区伊犁哈萨克自治州下辖地区2个地级行政区11县; 这中间多了一个级别。 省--自治州{泛地区}--地区级别--县。 阿勒泰地区就是地区一级；
    //太特殊了, 阿勒泰地区下辖1市6县，其所属地区=伊犁哈萨克自治州;。
    @OneToMany(mappedBy = "province")
    private Set<Adminunit> ads;
    //上级行政关系的关联：
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country  country;
    //下级行政关系的关联：
    @OneToMany(mappedBy = "province")
    private Set<City>   cities;
    //下一级行政关系坍塌,下一级关联区划实际上是摆设：只有一条虚拟的记录。
    //上海市 上海。
    //搜索的时候，支持 按照 城市那一级， 按省份级别，都算有效的搜索匹配区域。
    private boolean collapse;
}

//中国规范名称
//Province=美国State；州/道(Do)/邦/特别市、广域市/大区/府和县/酋长国/郡级市/区(Region)/自治共和国/地区/市(Municipality)/教区/岛组(island group)/新加坡五个社区发展理事会;
