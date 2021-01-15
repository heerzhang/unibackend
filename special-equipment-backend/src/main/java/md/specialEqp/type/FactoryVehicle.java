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
    /**厂车牌照"CATLICENNUM"*/
    private String  plat;
    /**额定载荷"RATEDLOADWEIG"*/
    private Float rtl;
    /**发动机型号"ENGINEMODEL"*/
    private String  mtm;

    /**动力方式"DYNAMICMODE"*/
    private String  pow;
}


//不可改技术参数：动力方式"DYNAMICMODE" 额定载荷"RATEDLOADWEIG" 发动机型号"ENGINEMODEL" 厂车牌照"CATLICENNUM"