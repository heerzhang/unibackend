package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;


@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Crane extends Eqp {
    //4000起重机械  TB_CRANE_PARA技术参数
    //4800升降机 TB_LIFT_PARA
    private String volume;

    public Crane(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


