package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Id;

//没必要在CompanyEs底下添加一个业务映射ID，也就是unit_ID字段，业务系统若是很多的话，xx_ID每个都独立。
//平均ES存储文件大小256字节/条。

//测试先用，看
@Document(indexName = "some")
@Data
@NoArgsConstructor
@Setting(settingPath = "elastic/esSetting.json")
public class SomeEs {
    @Id
    protected Long id;

    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=32)
            }
    )
    private String cod;

    @MultiField(mainField= @Field(type=FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer"),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=150)
            }
    )
    private String cert;

}


