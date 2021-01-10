package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;
//9000客运索道F000安全附件7000压力管道元件 这三个type特种设备没有做独立参数表，减少派生实体类。客运索道9000是全国唯一一家专门检验的。
//不增加实体类的继承子类就能解决简单问题，直接做EQP.otherParamJSON字段。
/**大型游乐设施: A级别的是国家级一家检验的。 B/C级别才有数据，A级的在监察平台都没技术参数和报告。
*/

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Amusement extends Eqp {
    //6000大型游乐设施 TB_AMUS_PARA

/*
    public Amusement(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }
*/
    //父类String level; 字段一个概念的？？ABC==>123仅仅代码描述差异？前端给的名称不同罢了。
    //private String  matr;   //游乐AMUS_TYPE游乐设施等级类型，游乐设施类型(检验)@[{id:'A',text:'A'},{id:'B',text:'B'},{id:'C',text:'C'}]
    private Boolean mbig;    // IF_SHIFT是否移动大型游乐；
    //游乐设施级别AMUS_TYPE 实际等同 EQP_LEVEL: "B级"，字段要合并；

}


//不可改技术参数：长度"LENGTH"运行高度"MOV_HIGH"额定乘客人数(人)"RATEDPASSENGERNUM"摆角"SWINGANGLE"额定速度"RATEDVELOCITY"回转直径"TURNINGDIAMETER"游乐设施高度"HEIGHT"倾夹角或坡度"GRADE"

