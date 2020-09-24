package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;
import javax.persistence.*;

@Document(indexName = "unit")
@Data
@NoArgsConstructor
@Mapping(mappingPath = "unites.json")       //没看到效果
public class UnitEs {
    @Id
    protected Long id;
    //若@Field(type = FieldType.Auto,analyzer = "ik_smart")完全不行都单个汉字来分词的，ik分词器没能设置上。
    //若用FieldType.Auto,analyzer = "ik_max_word", searchAnalyzer = "ik_smart"完全不行都单个汉字来分词的，ik分词器没能设置上。
    //Keyword做搜索很正常。可是加上analyzer = "ik_max_word", searchAnalyzer = "ik_smart"就全变Text嵌Keyword，而且其它字段也跟着一起变。
    //@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    //@Field(analyzer ="ik_smart", searchAnalyzer ="ik_smart")        //ik分词器没能设置上,其它字段也跟着一起变。
    @Field(type = FieldType.Auto, ignoreAbove = 260 ,analyzer ="ik_smart")
    private String name;        //企业或机构名，人名，楼盘称谓。

    //不做注解的话String默认生成mapping是Text底下再嵌套Keyword的类型;字符串将默认被同时映射成text和keyword类型.
    private String address;     //详细住址，首要办公地点，楼盘地址。

    //@Field(type = FieldType.Auto)
    private String linkMen;     //负责管理人的个人名字
    //@Field(type = FieldType.Keyword)
    private String phone;       //消息联系的主要手机号
}



/*
要提高性能，就得结合FieldType.Keyword　FieldType.Text俩个一起做，Keyword照顾查全率，Text就能用倒排索引提高性能但无法保障100%都能查出来。
分词器最佳实践是：索引时用ik_max_word，在搜索时用ik_smart；　https://blog.csdn.net/qq_15267341/article/details/106954445?utm_medium=distribute.pc_relevant.none-task-blog-title-5&spm=1001.2101.3001.4242
中文用的：ik分词器自定义词库；　https://www.cnblogs.com/guanxiaohe/p/12365882.html
倒排索引与搜索引擎　https://zhuanlan.zhihu.com/p/101586644
没有设置analyzer = "ik_" 汉语默认都单个汉字来分词的。
配置：　@Setting(settingPath = "es-config/elastic-analyzer.json")
还有一种使用@Mapping(mappingPath = "productIndex.json")代替@Field注解;
内置的分词器 https://blog.csdn.net/lgb190730/article/details/107882929?utm_medium=distribute.pc_relevant.none-task-blog-title-8&spm=1001.2101.3001.4242
ik_max_word分词器　https://github.com/medcl/elasticsearch-analysis-ik
从ES查看index的映射URL=  http://localhost:9200/unit/_mapping
Completion Suggester输入框的自动补齐功能只能用于前缀采用Client使用REST API进行请求; 部分功能ES开发中；多个汉字多英文词就搜不到!
FuzzyQueryBuilder使用Fuzzy搜索有纠错功能，但是Fuzzy查询字段必须text类型的。
text类型保存时分词，全文检索,支持模糊、精确查询,适合超大文本存储,但不支持聚合,排序操作。
keyword类型整个文本串索引,不分词，可做过滤筛选、排序和聚合(统计),支持模糊、精确匹配,节省内存。
Keyword 根据整个字符串建立反向索引=精确定位，Text 根据分词后的许多独立单词再来建反向索引＝模糊大量。
Multi Field 多重字段=对一个字段进行多种不同方式的索引。多重字段情况，你可以查询 title，也可以查询title.keyword查询类型为keyword的子字段;
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
针对FieldType.Keyword的查询可用findAllByNameContains(),相当模糊查询%A%;

*/
