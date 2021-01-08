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


