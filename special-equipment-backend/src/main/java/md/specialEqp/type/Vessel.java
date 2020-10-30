package md.specialEqp.type;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;

//子类不能再做@org.hibernate.annotations.Cache()注解的。
//@DiscriminatorValue(value="电梯")


@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Vessel extends Eqp {
    //2000压力容器	 TB_VESSEL_PARA
    private String volume;

    public Vessel(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


