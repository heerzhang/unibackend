package md.cm.unit;

import lombok.Getter;
import lombok.Setter;
import md.specialEqp.EQP;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region ="Slow")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String name;
    private String address;
    private String linkMen;
    private String phone;
    //加载方式修改影响很大。根据业务场景挑选。懒加载了若想关联内省查询会运行错误。
    @OneToMany(mappedBy = "ownerUnt")
    private Set<EQP> owns;
    //默认采用LAZY方式加载实体,懒加载时加了@Transactional的查询才能不报错，但是graphQL内省阶段是与入口函数分离的=还是报错。
    //一对多或多对多时，默认懒加载，graphQL遇到这个字段，若想要顺着关联查询下去，程序报错，等于有一种信息安全控制机制。
    //懒加载的坏处，该字段代码不能直接使用，必须绕道，从反向关系依据id倒着查。
    @OneToMany(mappedBy = "maintUnt")
    private Set<EQP> maints;    //维保设备集合

    public Unit() {
    }
    public  Unit(String name,String address){
        this.name=name;
        this.address=address;
    }
    public Set<EQP>  getMaints() {
        return  this.maints;
    }
}


/*
应和大库思维，把unit 使用单位，继续拆分成了：person + company 优化存储语义；前端界面提前区分两种unit。
company 法人单位,其他组织
person 个人
unit是业务代理者，company和person是通用的基础库。

*/
