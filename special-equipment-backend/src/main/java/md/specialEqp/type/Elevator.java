package md.specialEqp.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.specialEqp.EQP;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Elevator  extends EQP {


    private String liftHeight;
 /*
    public  Elevator(String cod,String type,String oid){
       // super(cod,type,oid);
        liftHeight="300米";
    }
    */
}


//@NoArgsConstructor缺少导致 No default constructor for entity:  : md.specialEqp.type.电梯
