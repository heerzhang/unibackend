package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.suggest.completion.context.ContextMapping;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.completion.Completion;

import javax.persistence.*;
//type = "annotated-completion-type",

@Document(indexName = "unit", shards = 1, replicas = 0)
//@Document(indexName = "unit")
@Data
@NoArgsConstructor
//@Setting(settingPath = "es-config/elastic-analyzer.json")
public class UnitEs {
    @Id
    protected Long id;
     //? ?  @Field(type = FieldType.Text,analyzer = "ik_max_word")
    //根据单词来切分的，匹配某个词就能看见。　中文不行是单个汉字就要切分的=匹配太多了。
  //  @Field(type = FieldType.Search_As_You_Type, analyzer = "ik_smart", searchAnalyzer = "autocomplete_search")
  //  @CompletionField(maxInputLength = 100)
    private String name;        //企业或机构名，人名，楼盘称谓。

    @CompletionField(maxInputLength = 100)
    private Completion suggest;

    //Completion 并不是completion suggester
    private String address;     //详细住址，首要办公地点，楼盘地址。
    private String linkMen;     //负责管理人的个人名字
    private String phone;       //消息联系的主要手机号

    /*Completion Context Suggester配置(如果不配置CompletionContext则是Completion Suggester)*/
   /* @CompletionField(analyzer = "ik", contexts = {
                   @CompletionContext(name = "name", type = ContextMapping.Type.CATEGORY),
                  @CompletionContext(name = "address", type = ContextMapping.Type.CATEGORY)
           })
    private String name;
    */
}



/*
[name] is not a completion suggest field ：报错时快照
  @Setting(settingPath = "es-config/elastic-analyzer.json")
  @CompletionField(analyzer ="ik_smart" )


*/