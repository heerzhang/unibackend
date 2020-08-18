package md.specialEqp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.specialEqp.EQP;
import org.fjsei.yewu.filter.Equipment;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Elevator  implements Equipment{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String liftHeight;
 /*
    public  Elevator(String cod,String type,String oid){
       // super(cod,type,oid);
        liftHeight="300米";
    }
    */
}


//@NoArgsConstructor缺少导致 No default constructor for entity:  : md.specialEqp.type.电梯
