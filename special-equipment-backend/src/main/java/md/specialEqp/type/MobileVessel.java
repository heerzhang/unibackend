package md.specialEqp.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
//@DiscriminatorValue(value="移动式压力容器")
public class MobileVessel extends Vessel {


    private String thickness;

    public MobileVessel(String cod, String type, String oid){
        super(cod,type,oid);
        thickness="9mm";
    }

}



