package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.suggest.completion.context.ContextMapping;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.completion.Completion;

import javax.persistence.*;

@Document(indexName = "unit")
@Data
@NoArgsConstructor
public class UnitEs {
    @Id
    protected Long id;
    //根据单词来切分的，匹配某个词就能看见。　中文不行是单个汉字就要切分的=匹配太多了。
   //  @Field(type = FieldType.Search_As_You_Type, analyzer = "ik_smart", searchAnalyzer = "autocomplete_search")
    @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
    private String name;        //企业或机构名，人名，楼盘称谓。

    //@CompletionField(maxInputLength = 100, analyzer = "ik_smart", searchAnalyzer = "autocomplete_search")
    //用@CompletionField("ik_max_word") Completion suggest汉语多字搜索不到任何东西

   // @CompletionField(maxInputLength = 100, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    //@Field(type = FieldType.Search_As_You_Type)
    private Completion suggest;         //特殊定义。名字和函数定义一致。

    //不做注解的话String默认生成mapping是Text底下再嵌套Keyword的类型;字符串将默认被同时映射成text和keyword类型.
    @Field(type = FieldType.Text)
    private String address;     //详细住址，首要办公地点，楼盘地址。
    @CompletionField(maxInputLength = 100)
    private String linkMen;     //负责管理人的个人名字
    @Field(type = FieldType.Keyword)
    private String phone;       //消息联系的主要手机号

}



/*
配置：　@Setting(settingPath = "es-config/elastic-analyzer.json")
还有一种使用@Mapping(mappingPath = "productIndex.json")代替@Field注解;
内置的分词器 https://blog.csdn.net/lgb190730/article/details/107882929?utm_medium=distribute.pc_relevant.none-task-blog-title-8&spm=1001.2101.3001.4242
ik_max_word分词器　https://github.com/medcl/elasticsearch-analysis-ik
从ES查看index的映射URL=  http://localhost:9200/unit/_mapping
Completion Suggester输入框的自动补齐功能只能用于前缀采用Client使用REST API进行请求; 部分功能ES开发中。
FuzzyQueryBuilder使用Fuzzy搜索有纠错功能，但是Fuzzy查询字段必须text类型的。
text类型保存时分词，全文检索,支持模糊、精确查询,适合超大文本存储,但不支持聚合,排序操作。
keyword类型整个文本串索引,不分词，可做过滤筛选、排序和聚合(统计),支持模糊、精确匹配,节省内存。
Multi Field 多重字段=对一个字段进行多种不同方式的索引。
completion suggester问题：小写，跨越英文空格单词多词也找不到，汉语单个汉字都孤立成单词了，多汉字的都无法找到。
edge_ngram适合前缀匹配；ngram适合前缀中缀检索；
即时搜索（Instant Search）=输入即搜索（search-as-you-type）；
prefix 查询存在严重的资源消耗集群压力，参数 max_expansions 控制着前缀匹配的数量；
部分匹配=粗笨低效的全文搜索，很不推荐%A%。
wildcard和regexp查询方式与prefix查询相同，扫描倒排索引,性能很差;避免左通配*foo正则;必须对Keyword类型来做。
search-as-you-type的模式：分词，关键是它不够快，延迟问题凸显, 能匹配的列表太多。
completion suggester比这Edge N-Grams{等于search-as-you-type}更高效快;但搜索输入超过max_gram的search-as-you-type反而匹配不到了。
查询时(query time)实现的模式性能很低。对比，索引时(index time)实现的模式能改善性能。
目的不一样的模式。
     Completion Context Suggester配置(如果不配置CompletionContext则是Completion Suggester)
     @CompletionField(analyzer = "ik", contexts = {
                    @CompletionContext(name = "name", type = ContextMapping.Type.CATEGORY),
                    @CompletionContext(name = "address", type = ContextMapping.Type.CATEGORY)
           })
    private String name;
@Setting(settingPath = "es-config/elastic-analyzer.json")
@Mapping(mappingPath = "unites.json")  通过@Mapping注解来自定义生成Mapping。resources目录/articlesearch_mapping.json。
*/
