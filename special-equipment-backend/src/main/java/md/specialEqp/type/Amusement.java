package md.specialEqp.type;

import lombok.Getter;
import lombok.Setter;
import md.specialEqp.Eqp;

import javax.persistence.Entity;
//9000客运索道F000安全附件7000压力管道元件 这三个type特种设备没有做独立参数表，减少派生实体类。
//不增加实体类的继承子类就能解决简单问题，直接做EQP.otherParamJSON字段。

@Getter
@Setter
@Entity
public class Amusement extends Eqp {
    //6000大型游乐设施 TB_AMUS_PARA
    private String volume;

    public Amusement(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


