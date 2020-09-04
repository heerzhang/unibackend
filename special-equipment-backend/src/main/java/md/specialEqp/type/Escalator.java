package md.specialEqp.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(value="自动扶梯")
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Slow")
public class Escalator extends Elevator {


    private String steps;

    public  Escalator(String cod,String type,String oid){
        super(cod,type,oid);
        steps="150";
    }

}


//@NoArgsConstructor缺少导致 No default constructor for entity:  : md.specialEqp.type.电梯
