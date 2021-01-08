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
    /*
    public Crane(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }
    */
    private Boolean norm;  //TB_CRANE_PARA.IF_UNNORMAL非标;
    private Float load; //CHAENGLOAMAIN{不算4D00,4800}主钩）起重机械载荷； :吨
    private Short pnum; //BERNUM停车设备泊位数4D00，
    private Float hlif; //ELEHEIGHTMAIN主钩）起升高度；
    private Boolean two; //IF_TWO_CAB双司机室，
    private Boolean twoc; //IF_TWO_LCAR有否双小车
    private Boolean grab; //IF_GRAB_B否抓斗，
    private Boolean suck; //IF_SUCTORIAL有否起重吸盘，
    private Boolean cotr; //IF_CONTAIN_H否集装箱吊具，
    private Float span; //SPAN跨度（m；
    private Boolean walk;  //IF_XZS否行走式,
    private Float mom; //CHAADVMOM起重力矩（吨米;
    private Boolean whole; //IF_ZJCC否整机出厂整机上岸的;
}


