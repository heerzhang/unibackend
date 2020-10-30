package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.*;

//子类不能再做@org.hibernate.annotations.Cache() 与 @Inheritance(strategy=)和@Version注解的。
//java中的接口之间可以实现多继承，也可以实现多实现。但是java中的类只能实现单extends继承


@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Elevator  extends Eqp {
    //3000电梯  TB_ELEV_PARA  电梯技术参数表
    private String liftHeight;

    public  Elevator(String cod,String type,String oid){
        super(cod,type,oid);
        liftHeight="253Meters";
    }

}


//@NoArgsConstructor缺少导致 No default constructor for entity:  : md.specialEqp.type.电梯
