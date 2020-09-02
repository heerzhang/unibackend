package md.specialEqp.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Slow")
public class Escalator extends Elevator {


    private String steps;
 /*
    public  Elevator(String cod,String type,String oid){
       // super(cod,type,oid);
        liftHeight="300米";
    }
    */
}


//@NoArgsConstructor缺少导致 No default constructor for entity:  : md.specialEqp.type.电梯
