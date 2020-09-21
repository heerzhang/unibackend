package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.suggest.completion.context.ContextMapping;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.completion.Completion;

import javax.persistence.*;

@Document(indexName = "unit", shards = 1, replicas = 0)
//@Document(indexName = "unit")
@Data
@NoArgsConstructor
//@Setting(settingPath = "es-config/elastic-analyzer.json")
//@Mapping(mappingPath = "unites.json") //通过@Mapping注解来自定义生成Mapping。resources目录/articlesearch_mapping.json。
public class UnitEs {
    @Id
    protected Long id;

    //根据单词来切分的，匹配某个词就能看见。　中文不行是单个汉字就要切分的=匹配太多了。
   //  @Field(type = FieldType.Search_As_You_Type, analyzer = "ik_smart", searchAnalyzer = "autocomplete_search")
    //@Field(type = FieldType.Keyword,analyzer = "ik_max_word")
    private String name;        //企业或机构名，人名，楼盘称谓。

    //@CompletionField(maxInputLength = 100, analyzer = "ik_smart", searchAnalyzer = "autocomplete_search")
    //用@CompletionField("ik_max_word") Completion suggest汉语多字搜索不到任何东西
    @CompletionField(maxInputLength = 100, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private Completion suggest;         //名字和函数定义一致。

    //Completion 并不是completion suggester
    private String address;     //详细住址，首要办公地点，楼盘地址。
    private String linkMen;     //负责管理人的个人名字
    private String phone;       //消息联系的主要手机号

    /*  目的不一样的模式。
     Completion Context Suggester配置(如果不配置CompletionContext则是Completion Suggester)
     @CompletionField(analyzer = "ik", contexts = {
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
还有一种使用@Mapping(mappingPath = "productIndex.json")代替@Field注解;
内置的分词器 https://blog.csdn.net/lgb190730/article/details/107882929?utm_medium=distribute.pc_relevant.none-task-blog-title-8&spm=1001.2101.3001.4242
ik_max_word分词器　https://github.com/medcl/elasticsearch-analysis-ik
*/