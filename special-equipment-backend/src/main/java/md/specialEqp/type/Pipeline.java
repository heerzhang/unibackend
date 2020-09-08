package md.specialEqp.type;

import lombok.Getter;
import lombok.Setter;
import md.specialEqp.EQP;
import md.specialEqp.inspect.ISP;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;
//7000压力管道元件=制造库才用的　=制造流水表的type。


@Getter
@Setter
@Entity
public class Pipeline extends EQP {
    //8000压力管道/ TB_PIPELINE_PARA  JC_TEMP_PIPELINE_PARA
    private String volume;
    //管道底下的具体的许多个单元组成集合： TB_PIPELINE_UNIT_PARA  JC_TEMP_PIPELINE_UNIT_PARA
    //单元也可以合并。
    @OneToMany(mappedBy="pipe" ,fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region ="Slow")
    private Set<PipingUnit> cells;

    //管道总体的相关参数，不是具体单元的，是合计数据或者都统一的参数。

    public Pipeline(String cod, String type, String oid){
        super(cod,type,oid);
        volume="820升";
    }

}


