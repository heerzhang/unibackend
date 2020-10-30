package md.specialEqp.type;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;


@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class FactoryVehicle extends Eqp {
    //5000场（厂）内专用机动车辆   TB_VEHIC_PARA
    private String volume;

    public FactoryVehicle(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


