package md.specialEqp.type;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
//一条管道底下有很多的管道单元来组成的。

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

    //每个单元编码不同，管道单元的代码：报告书和单线图里面的key。
    private String pipecode;    //EQP_CODE管道编号

    //每个单元管道特性表 TB_PIPELINE_UNIT_PARA  JC_TEMP_PIPELINE_UNIT_PARA
    //单项管道检验总长度;  LAY_MODE=!埋地||LAY_MODE=!架空     //PIPELINE_MEDIUM==钢制&&PIPELINE_LEVEL=!GA
    //TB_PIPELINE_UNIT_PARA.LAY_MODE   is '敷设方式'         ??JSON非结构化
    //TB_PIPELINE_UNIT_PARA.V_LAY_MODE  is '计费用-敷设方式（架空、埋地、其他）' ？？标准化Enum?
    //NOMINAL_DIA>=50.0&&NOMINAL_DIA<=150.0  + length;

    private String  matr;  //PIPELINE_MEDIUM管道材质，@[{id:'钢制',text:'钢制'},{id:'PE管',text:'PE管'}]
    //合成属性123级别+ABCD类别；PIPELINE_LEVEL = "";// 管道级别@[{id:'GA1',text:'GA1'},{id:'GA2',text:'GA2'},{id:'GB1',text:'GB1'},{id:'GB2',text:'GB2'},{id:'GC1',text:'GC1'},{id:'GC2',text:'GC2'},{id:'GC3',text:'GC3'},{id:'GD1',text:'GD1'},{id:'GD2',text:'GD2'}]
    private String  lay; //LAY_MODE管道敷设方式[{id:'埋地',text:'埋地'},{id:'架空',text:'架空'},{id:'其它',text:'其它'}]
    private Float dia;   //NOMINAL_DIA公称直径（mm）
    private Float len;  //LENGTH；
}

//不可改技术参数：