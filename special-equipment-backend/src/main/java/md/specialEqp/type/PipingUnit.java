package md.specialEqp.type;

import lombok.Getter;
import lombok.Setter;
import md.specialEqp.inspect.Isp;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**一条管道底下有很多的管道单元来组成的。TB_PIPELINE_UNIT_PARA
 * 每个单元管道特性表 TB_PIPELINE_UNIT_PARA  JC_TEMP_PIPELINE_UNIT_PARA
*/
@Getter
@Setter
@Entity
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"pipe_id", "pipecode"})} )
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Medium")
public class PipingUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipe_id")
    private Pipeline pipe;

    /**EQP_CODE管道编号:每个单元编码不同，管道单元的代码：报告书和单线图里面的key。*/
    private String  code;
    /**管道单元登记编号EQP_UNT_REGCOD;*/
    private String  rno;
    /**管道起点START_PLACE; 看单线图和工程图*/
    private String  start;
    /**管道止点END_PLACE; 看单线图和工程图*/
    private String  stop;
    //todo: 级别enum ?
    //.V_PIPELINE_LEVEL  '计费用-管道级别（[G][A-D][1-3]）'
    //合成属性123级别+ABCD类别；PIPELINE_LEVEL = "";// 管道级别@[{id:'GA1',text:'GA1'},{id:'GA2',text:'GA2'},{id:'GB1',text:'GB1'},{id:'GB2',text:'GB2'},{id:'GC1',text:'GC1'},{id:'GC2',text:'GC2'},{id:'GC3',text:'GC3'},{id:'GD1',text:'GD1'},{id:'GD2',text:'GD2'}]

    /**PIPELINE_MEDIUM管道材质，@[{id:'钢制',text:'钢制'},{id:'PE管',text:'PE管'}]
     * 实际上　是标号 PE100;   20（GB/T8163-2008）
     */
    private String  matr;

    /**TB_PIPELINE_UNIT_PARA.LAY_MODE   is '敷设方式'         ??JSON非结构化    不规范
    *TB_PIPELINE_UNIT_PARA.V_LAY_MODE  is '计费用-敷设方式（架空、埋地、其他）' ？？标准化Enum?比较少
     * LAY_MODE管道敷设方式[{id:'埋地',text:'埋地'},{id:'架空',text:'架空'},{id:'其它',text:'其它'}]
    */
    private String  lay;

    /**NOMINAL_DIA公称直径（mm）不规范　φ168，有很多数据。
     * .V_PIPELINE_DIA  '计费用-管道直径（公称直径mm）规范，数据较少;
     */
    private Float dia;

    /**LENGTH；管道长度m*/
    private Float leng;
    /**使用状态USE_STA；*/
    private Byte   ust;
    //.EQP_REG_STA  '注册状态'

    //关联 备份字段：
    /**下次年检日期YEAR_NEXT_ISP_DATE；*/
    private Date nxtD1;      //NEXT_ISP_DATE1下次检验日期1（在线、年度）粗的检
    /**定检下检日期NEXT_ISP_DATE；*/
    private Date nxtD2;      //NEXT_ISP_DATE2下次检验日期2(机电定检，内检，全面）
    //省略掉：监检报告下检日期INCP_NEXT_ISP_DATE；

    //todo:关联 Isp字段：
    /**关联Isp 以过滤排序形式返回给前端展示层的。*/
  //  private Isp isp;   //定检检验报告
    //private Isp year;  //年检报告
    //监检报告
}


//不可改参数：EQP_CODE; LENGTH;管道起点START_PLACE;管道止点END_PLACE;管道单元登记编号EQP_UNT_REGCOD;
//附带不可改属性： 使用状态USE_STA； 下次年检日期YEAR_NEXT_ISP_DATE；定检下检日期NEXT_ISP_DATE；监检报告下检日期INCP_NEXT_ISP_DATE；
// 年检报告编号YEAR_ISP_REPORT_COD； 年检结论YEAR_ISP_CONCLU；年检日期YEAR_ISP_DATE；
//   定检检验报告编号ISP_REPORT_COD；定检结论ISP_CONCLU；定检日期ISP_DATE；
//   监检报告编号INCP_ISP_REPORT_COD；监检结论INCP_ISP_CONCLU；监检日期INCP_ISP_DATE；

//NOMINAL_DIA>=50.0&&NOMINAL_DIA<=150.0  + length;
//单项管道检验总长度;  LAY_MODE=!埋地||LAY_MODE=!架空     //PIPELINE_MEDIUM==钢制&&PIPELINE_LEVEL=!GA
