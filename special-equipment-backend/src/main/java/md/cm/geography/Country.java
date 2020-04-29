package md.cm.geography;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //name=搜索名，不一定要全称呼的。 中华xxx;
    //null=世界;
    @Column(unique = true)
    private String  name;        //世界--》国家和地区。
    @OneToMany(mappedBy = "country")
    private Set<Adminunit> ads;
    //没有 ： 上级行政关系的关联：
    //下级行政关系的关联：
    @OneToMany(mappedBy = "country")
    private Set<Province>   provinces;
    //下一级行政关系坍塌,下一级关联区划实际上是摆设：只有一条虚拟的记录。
    //国家级新加坡->省一级新加坡->地市一级新加坡；可以省略掉2层区划级别。
    //搜索的时候，支持 按照城市那一级， 按国家级，都算有效的搜索匹配区域。
    private boolean collapse;
}
//新加坡实际是地市级别提升到国家级。
