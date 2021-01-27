package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;

//子类不能再做@org.hibernate.annotations.Cache()注解的。
//@DiscriminatorValue(value="电梯")

/**2000压力容器	 TB_VESSEL_PARA
 * R000常压容器 能否也放在这个派生类{技术参数表}；再已经 数据库-type-sort-vart字段区分。实体继承成了技术参数表分别了。
 */

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Vessel extends Eqp {
    //2000压力容器	 TB_VESSEL_PARA
    /**CONTAINERVOLUME压力容器容积（立方米），复合字段
     *容积(换热面积)"CONTAINERVOLUME"  内容器/外壳:0.077/0.05
     */
    private String  vol;
    /**DESPRE设计压力（MPa） 复合字段，壳程/管程： 1.4/2.0
     */
    private String  prs;

    /**CAPABLIMITNUM 氧舱2400 容限（人）*/
    private Short  pnum;
    /**容器高"CONTAINERHEIGHT", 设备外形高*/
    private Float  high;
    /**罐车总重量"TANKCARTOWEI"*/
    private Float  weig;
    /**载重量"LOADWEIG*/
    private Float rtl;
    /**满载总重量"FULLYLOADWEI"
     * 没几个有数据的; H2数据库不能用full这个名只能改。
     */
    private Float fulw;
    /**容器内径"CONINNDIA"  复合字段 宽×高：680×1180; 上：800/下：3000; DN800mm
     * Φ400/Φ850/Φ1000;  球形505; 2200(内筒)/2800(夹套)
     */
    private String  dim;
    /**充装介质"TINAMPLMEDI"*/
    private String  mdi;
    /**夹套介质"COVERMEDIUM"*/
    private String  jakm;
    /**罐车牌号"CARSIGN"
     * 车牌
     * */
    private String  plat;
    /**罐车结构型式"CARSTRFORM"
     * 没几个有数据的;
     */
    private String  form;
    /**保温(绝热形式)"TEMPPREMODE*/
    private String  insul;
    /**安装形式"INSFORM"*/
    private String  mont;

}


//不可改技术参数：容积(换热面积)"CONTAINERVOLUME"罐车总重量"TANKCARTOWEI"容器高"CONTAINERHEIGHT"载重量"LOADWEIG"充装介质"TINAMPLMEDI"容器内径"CONINNDIA"
// 夹套介质"COVERMEDIUM"罐车牌号"CARSIGN"罐车结构型式"CARSTRFORM"满载总重量"FULLYLOADWEI"保温(绝热形式)"TEMPPREMODE"安装形式"INSFORM"
