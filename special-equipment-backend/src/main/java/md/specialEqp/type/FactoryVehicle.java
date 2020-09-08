package md.specialEqp.type;

import lombok.Getter;
import lombok.Setter;
import md.specialEqp.EQP;

import javax.persistence.Entity;


@Getter
@Setter
@Entity
public class FactoryVehicle extends EQP {
    //5000场（厂）内专用机动车辆   TB_VEHIC_PARA
    private String volume;

    public FactoryVehicle(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


