package md.specialEqp.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;

/**9000 客运索道  TB_CABLEWAY_PARA没， JC_CABLEWAY_PARA JC_TEMP_CABLEWAY_PARA 技术参数;
 * 监察才有，检验没权搞这个品种业务。福建一共才13台设备。
 */

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Ropeway extends Eqp {
    /**"长度(m) ROPEWAYLENGTH"*/
    private Float  leng;

    /**"运行速度m/s RUNVELOCITY"*/
    private Float  vl;

    /**"输送能力(运量), 人/小时, TRANSPORTATIONABILITY"
     * */
    private Integer rtl;

    /**"支架数目 BRANUM"*/
    private Short  flo;

    //对接外部系统，EQUDATASYSID: "-6735c775:12c6d2ac6be:-2b9a"
}

//不可改技术参数：