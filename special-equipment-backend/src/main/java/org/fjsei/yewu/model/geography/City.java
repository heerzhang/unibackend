package org.fjsei.yewu.model.geography;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


//地区一级的城市。 可以大的City套小的市镇。

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"name", "province_id"})} )
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //直接填写  福州 而不是  福州市。 搜索 和 给用户输入用的。
    private String  name;         //+',市' '洲？' ,行政级别：？‘xx实验区’； 旧的地区级区域
    //‘香港’‘上海市’ 如何呢： 省 市 都是同一个。 两个实体表都有的。
    //特殊例外的 标志位。
    //private int promote;    //缺省=0，  1表示区域管理级Grade提升了1级　：上海市 /上海市/　区，提升了就可以合并抹掉。
    //外国竟然是倒过来的关系：县(=郡)的底下才是城市域（也有县改成市的）。和中国相反了。
    //美国是没有“地级”的，一个州可以辖极少到极多的“县级”单位; 特例 纽约市。 加州行政区划是 州-县-市/镇-街区 四级。
    //中国县级市的？ 福建省福州市福清市某乡镇。
    @OneToMany(mappedBy = "city")
    private Set<Adminunit>  ads;
    //上级行政关系的关联：
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;
    //下级行政关系的关联：
    @OneToMany(mappedBy = "city")
    private Set<County>   counties;
    //下一级行政关系坍塌,下一级关联区划实际上是摆设：只有一条虚拟的记录。
    //县级级别的计划单列城市 ，没有划分区县的独立城市。
    //搜索的时候，支持 按照 区 县那一级， 按城市级别，都算有效的搜索匹配区域。
    private boolean collapse;
}

//美国比中国少了一个级别。=地区级别少了。
//地名重名现象非常严重