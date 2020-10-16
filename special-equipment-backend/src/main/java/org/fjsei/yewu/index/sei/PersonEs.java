package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;
import javax.persistence.Id;

//保持模块独立性，减少关联依赖，就不在PersonEs底下添加一个业务映射ID，也就是unit_ID字段。

@Document(indexName = "person")
@Data
@NoArgsConstructor
public class PersonEs {
    @Id
    protected Long id;

    @MultiField(mainField= @Field(type=FieldType.Text),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=60)
            }
    )
    private String name;        //人名。
    @Field(type = FieldType.Keyword)
    private String no;       //身份证号码
    //不做注解的话String默认生成mapping是Text底下再嵌套Keyword的类型;字符串将默认被同时映射成text和keyword类型.
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String address;     //详细住址，首要办公地点，楼盘地址。
    @Field(type = FieldType.Keyword)
    private String phone;       //手机号
    @Field(type = FieldType.Keyword)
    private String gender;     //性别
    //Keyword字段rangeQuery逻辑若是字段null的，gte(99) gte("99")竟然为真，被解释成"from":"to"。
    @MultiField(mainField= @Field(type=FieldType.Text),
            otherFields={ @InnerField(suffix="keyword",type=FieldType.Keyword, ignoreAbove=120)
            }
    )
    private String occupation;     //职业

}

