package md.cm.geography;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

//县，区级。

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"name", "city_id"})} )
public class County {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    //台江区  长乐市  马尾区， 根据习惯写上什么名字 就写啥。
    private String  name;        //+'县'、'区'； 独立市。
    //？某个市级单位，底下只有一个区。
    //市辖区和县级城市是同一个行政级别。 县底下能管的镇级市。
    //龙港市{原名字=苍南县龙港镇}=浙江省辖县级市，由温州市‘代管’？。
    //代管的应该不能算数，行政意义叫法。 国家一级的计划单列市{}，下面也有省一级的计划单列市；
    //代管的特殊情况太多了！！ 内蒙古自治区.呼伦贝尔{地级市}.满洲里市{计划单列市=准地级市，县级市也想管区呀}.扎赉诺尔区.6街道镇; ?太没谱了！。满洲里市应当当成区县。

    //平潭县()如何？
    //竟然是倒过来的关系：县的底下才是城市域。和中国相反了。在中国，市比县大；在美国，县比市大。
    @OneToMany(mappedBy = "county")
    private Set<Adminunit> ads;
    //上级行政关系的关联：
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    //下级行政关系的关联：
    @OneToMany(mappedBy = "county")
    private Set<Town>   towns;
    //下一级行政关系坍塌,下一级关联区划实际上是摆设：只有一条虚拟的记录。
    //底下只有一个镇的，没有划分镇的县级别。
    //搜索的时候，支持 按照 镇 一级，  按县级别，都算有效的搜索匹配区域。
    private boolean collapse;
}

//福建--平潭--乡镇街道；平潭是跳级升格为地级市，县区级别空缺。
//平潭is City; 　　底下County=自己本身; 福州变成管不到平潭了。
//上海is 省级，　　底下City=自己本身。
//香港is国际级地区级别，　底下省级以及City都=自己本身。
