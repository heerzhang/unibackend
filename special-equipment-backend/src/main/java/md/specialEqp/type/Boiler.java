package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;

import javax.persistence.Entity;

/**1000锅炉  TB_BOIL_PARA  锅炉技术参数;
 * 锅炉/电梯，在监察首检录入中不允许新增？ 施工告急中添加的。要监检
 * 告知单编号自己填，电梯设备代码可null;锅炉告知有设备代码。
 * 监察只看识别码oid。
 */

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Boiler extends Eqp {
    /**TB_BOIL_PARA.IF_BOIL_WALL'是否有炉墙锅炉；*/
    private Boolean wall;
    /**TB_BOIL_PARA.RATCON '1000额定功率(MW)/蒸发量；RATCONS*/
    private Float power;
    //todo: 改成Enum形式  IF_VERTICAL ？ MAINSTRFORM 合并字段 TB_BOIL_PARA.IF_VERTICAL安装形式（作废）
    /**IF_VERTICAL锅炉结构形式@[{id:'立式',text:'立式'},{id:'卧式',text:'卧式'} "锅壳式"] 锅炉结构形式MAINSTRFORM: "锅壳式", 立式很少
     */
    private String  form;
    /**BURNINGTYPE燃料种类 [{id:'无烟煤',text:'无烟煤'},{id:'烟煤',text:'烟煤'},{id:'褐煤',text:'褐煤'},{id:'煤矸石',text:'煤矸石'},{id:'柴油',text:'柴油'},
     * {id:'重油',text:'重油'},{id:'渣油',text:'渣油'},{id:'天然气',text:'天然气'},{id:'管道液化气',text:'管道液化气'},{id:'城市煤气',text:'城市煤气'},{id:'高炉煤气',text:'高炉煤气'},
     * {id:'电加热',text:'电加热'},{id:'余热',text:'余热'},{id:'再生生物资',text:'再生生物资'},{id:'黑液',text:'黑液'},{id:'垃圾',text:'垃圾'}]
    */
    private String  fuel;
    /**额定工作压力DESWORKPRESS*/
    private Float  pres;
    /**燃烧方式BURNMODE*/
    private String  bmod;
}


//不可改技术参数：额定工作压力DESWORKPRESS  燃料种类BURNINGTYPE  燃烧方式BURNMODE  锅炉结构形式MAINSTRFORM: "锅壳式"


