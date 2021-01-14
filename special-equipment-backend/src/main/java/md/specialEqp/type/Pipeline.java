package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

/**8000压力管道/ TB_PIPELINE_PARA  JC_TEMP_PIPELINE_PARA
 * 管道总体的相关参数，不是具体单元的，是合计数据或者都统一的参数。
*/

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Pipeline extends Eqp {
    //8000压力管道/ TB_PIPELINE_PARA  JC_TEMP_PIPELINE_PARA

    /**管道底下的具体的许多个单元组成集合： TB_PIPELINE_UNIT_PARA  JC_TEMP_PIPELINE_UNIT_PARA
    *单元也可以合并。
     */
    @OneToMany(mappedBy="pipe" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Slow")
    private Set<PipingUnit> cells;

    /**全部的，所有管道单元的都是PIPELINE_MEDIUM管道材质，@[{id:'钢制',text:'钢制'},{id:'PE管',text:'PE管'}]
     * 实际是标号， PE管； 20 ；钢制；碳钢；无缝钢管（牌号不清）; 详见管道特性表
    */
    private String  matr;
    //合成属性123级别+ABCD类别；PIPELINE_LEVEL = "";// 管道级别@[{id:'GA1',text:'GA1'},{id:'GA2',text:'GA2'},{id:'GB1',text:'GB1'},{id:'GB2',text:'GB2'},{id:'GC1',text:'GC1'},{id:'GC2',text:'GC2'},{id:'GC3',text:'GC3'},{id:'GD1',text:'GD1'},{id:'GD2',text:'GD2'}]

    /**管道介质"WORK_MEDIUM" 见特性表
     * 乙炔/粗BYD/BYD+水
     */
    private String  mdi;
    /**设计压力"DESIGN_PRESS" 见特性表 详见管道特性表,
     * 高压部分/低压部分：2.0/1.5    高压侧：2.0；低压侧1.5
     * {"高":, "低":, "某部分管":, }
     */
    private String  prs;
    /**设计温度"DESIGN_TEMP"  常温; 见管道单线图
     * 蒸汽：190、空气：60 ; 80(PL8005～S8068段）/60(S8068～S8069段);  -25/-50(PG0410)
     */
    private String  temp;
}


//不可改技术参数： 设计压力"DESIGN_PRESS" 设计温度"DESIGN_TEMP" 管道介质"WORK_MEDIUM"

//7000压力管道元件=制造库才用的　=制造流水表的type。
