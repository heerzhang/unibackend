package md.cm.base;

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
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String name;
    private String no;  //身份证号码
    private String address;     //住址，身份证地址
    private String phone;       //个人手机号
    private String gender;        //性别
    private String occupation;        //职业

}


