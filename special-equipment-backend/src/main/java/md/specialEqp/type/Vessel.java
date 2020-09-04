package md.specialEqp.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.specialEqp.EQP;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

//子类不能再做@org.hibernate.annotations.Cache()注解的。
//@DiscriminatorValue(value="电梯")

@Getter
@Setter
@Entity
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Vessel extends EQP {


    private String volume;

    public Vessel(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


