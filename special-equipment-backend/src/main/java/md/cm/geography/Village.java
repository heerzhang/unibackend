package md.cm.geography;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

//楼盘的实体概念。 community ，小区名字；楼盘=地址的泛房型表达式; 广义模糊地址；

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"name", "aid"})} )
public class Village {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    //简单公用叫法的。
    private String  name;  //楼盘名     BUILD_NAME  TB_HOUSE_MGE
    //监察平台=〉维保单位覆盖楼盘统计　楼盘名称：from eqp  Where mtU.=UNT_NAME  Group by pos.vlg.BUILDNAME;

    private String type;    //.INST_BUILD_TYPE楼盘性质。,商品房、复建房、拆迁安置房、廉租房、回迁房、经济适用房、限价房,棚户区。

    //小区楼盘底下的　所有已经声明的地址。
    @OneToMany(mappedBy = "vlg")
    private Set<Address>  adrs;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "aid")
    private Adminunit  ad;       //行政区划
}

//旧的　TB_HOUSE_MGE   "BUILD_ID"
//楼盘+管理小区场所；大厦标志名。　private String BUILD_NAME;
