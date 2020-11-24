package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Id;
import java.util.Date;

//非独立索引；仅作为ES内部对象nested类型。

@Data
@NoArgsConstructor
public class UnitEs {
    @Id
    protected Long id;    //单位Unit ID
    //ES嵌套很深的？  扁平化，反规范化的。
    @MultiField(mainField= @Field(type=FieldType.Text),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=180)
            }
    )
    private String name;
    //被管辖地区　码
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String address;

}


