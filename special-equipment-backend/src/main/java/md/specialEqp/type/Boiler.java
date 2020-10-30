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
public class Boiler extends Eqp {
    //1000锅炉  TB_BOIL_PARA  锅炉技术参数
    private String volume;

    public Boiler(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


