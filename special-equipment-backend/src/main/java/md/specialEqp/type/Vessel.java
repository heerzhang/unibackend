package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;

//子类不能再做@org.hibernate.annotations.Cache()注解的。
//@DiscriminatorValue(value="电梯")

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Vessel extends Eqp {
    //2000压力容器	 TB_VESSEL_PARA

/*
    public Vessel(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }
*/

    private Float vol;  //CONTAINERVOLUME压力容器容积（立方米），
    private Float pres;  //DESPRE设计压力（MPa）
    private Short upper; //CAPABLIMITNUM氧舱2400 容限（人）；
}

//不可改技术参数：容积(换热面积)"CONTAINERVOLUME"罐车总重量"TANKCARTOWEI"容器高"CONTAINERHEIGHT"载重量"LOADWEIG"充装介质"TINAMPLMEDI"容器内径"CONINNDIA"
// 夹套介质"COVERMEDIUM"罐车牌号"CARSIGN"罐车结构型式"CARSTRFORM"满载总重量"FULLYLOADWEI"保温(绝热形式)"TEMPPREMODE"安装形式"INSFORM"
