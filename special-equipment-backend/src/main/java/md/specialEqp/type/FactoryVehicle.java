package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class FactoryVehicle extends Eqp {
    //5000场（厂）内专用机动车辆   TB_VEHIC_PARA
    private String  plat;      //厂车牌照"CATLICENNUM"
/*
    public FactoryVehicle(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }
    */
    private Float  load;    //额定载荷"RATEDLOADWEIG"
    private String  emod;   //发动机型号"ENGINEMODEL"
    private String  pmd;   //动力方式"DYNAMICMODE"
}


//不可改技术参数：动力方式"DYNAMICMODE" 额定载荷"RATEDLOADWEIG" 发动机型号"ENGINEMODEL" 厂车牌照"CATLICENNUM"