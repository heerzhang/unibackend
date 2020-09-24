package org.fjsei.yewu.index.sei;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.util.Streamable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//验证举例　"name"字段取值: "目前经营情况及内外部经营环境未发生重大变化"

public interface UnitEsRepository extends ElasticsearchRepository<UnitEs, Long> {
    //wildcard查询方式性能差;
    //若FieldType.Keyword字段就完全正常，可是若字段FieldType.Auto或FieldType.Text就不行了！。
    //用FieldType.Auto,analyzer = "ik_"完全不行；
    //@Field(analyzer ="ik_smart", searchAnalyzer ="ik_smart") 完全不行!单个汉字匹配；
    //用FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"只能单个词语匹配，多词语反而配不到=很奇怪!。
    //用FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word" 多词语反而配不到哪=很奇怪!。
    Streamable<UnitEs> findAllByNameContains(String name);
    //等价findAllByNameContains
    //FieldType.Keyword情况下和findAllByNameContains一样的，很正常的,像SQL like %A%。
    Streamable<UnitEs> findAllByNameContaining(String name);
    //findByNameLike等价findByNameStartingWith; Contains/Containing等价"*?*"通配符查询;
    //@Field(analyzer ="ik_smart", searchAnalyzer ="ik_smart")表现正常了；
    @Query("{\"wildcard\": {\"name.keyword\": {\"value\": \"*?0*\"}}}")
    Streamable<UnitEs> findAllByName_KeywordContains(String name);

    //FieldType.Text字段+analyzer缺省　随意都能匹配到
    //主要问题时能匹配到的列表太多了！　"内外"匹配到。也不等价于%A%，而是name分词后截取词语匹配||OR合集，列表会暴多。
    //+analyzer,多个词语会变成合并||OR关系，只要输入串截一个词匹配上的就都算入。
    //ik_max_word  "内外"匹配到。　"生环"匹配到
    //FieldType.Keyword 前缀方式匹配到，后缀不行，能全名字过滤。
    //FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word"　匹配到很多
    //等价于前缀查询　"?*"
    Streamable<UnitEs> findAllByNameLike(String name);

    //FieldType.Text能匹配很多，输入多个词语会变成合并||OR关系。
    //FieldType.Text字段+analyzer缺省，若用ik_smart存索引"内外部"会当作词而"内外"不能当成一个词。
    //+analyzer,多个词语可行；和可能多个字反而匹配不到，少一个字因为分词关系变了所以反而能够匹配原来输入的分词习惯而生成的索引就能匹配上了。
    //比起findAllByNameLike会更严格，匹配列表就更少。多个词语会变成合并||OR关系，只要输入串截一个词匹配上的就都算入。
    //ik_max_word  "内外"匹配到。　"生环"匹配不到
    //FieldType.Keyword 前缀也不能匹配到，后缀不行，只能全名字过滤。
    //FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word"　匹配到很多
    //等价于match查询相关度查询，但不是matchPhraseQuery查询
    Streamable<UnitEs> findAllByNameMatches(String name);

    //对FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word"可做到&&关系能缩小匹配队列。name可多词组成的；要分词的。
    Streamable<UnitEs> findAllByNameMatchesAndNameMatches(String name,String name2);

    //FieldType.Text字段+analyzer缺省，只能对单个汉字匹配，多汉字不行。
    //+analyzer,只能对词语匹配，但是多个词语就不行，必须分开成[]数组，names[]是||OR其中一个能匹配,反而匹配更多了。
    //类似findAllByNameMatches　"内外"匹配不到。names[]低下中单个name必须是单词的，单个name若是多词组成的就不能匹配上。
    //ik_max_word "内外"匹配到。　"生环"匹配不到
    //FieldType.Keyword 前缀也不能匹配到，后缀不行，只能全名字过滤。
    //若Keyword就等价terms
    Streamable<UnitEs> findAllByNameIn(String[] names);
    //没现成的，只能自己做：FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word"时　效果挺不错的。
    //FieldType.Keyword字段就完全不能用！
    //@Field(analyzer ="ik_smart", searchAnalyzer ="ik_smart")很好；
    @Query("{\"match_phrase\": {\"name\": {\"query\": \"?0\"}}}")
    Streamable<UnitEs> findAllByNameMatchePhrase(String name);
}


/*
[相对来论]wildcard和regexp查询方式与prefix查询相同，扫描倒排索引,性能很差;避免左通配*foo正则;必须对Keyword类型来做。
Elasticsearch 模糊查询 wildcard、regexp、prefix选型　https://blog.csdn.net/alex_xfboy/article/details/88298165
ES5.x里，注意数值类型是否需范围查询，其实只用Term或Terms精确匹配的，应定义为keyword类型。在ES6.x里会自动转换。https://blog.csdn.net/alex_xfboy/article/details/85648389
ElasticSearch　性能　调优　nested 慢 好几倍　parent-child关系 慢几百倍　https://cloud.tencent.com/developer/article/1495929
termQuery查询　是精准查询是非评分查询{输入不分词}；term查询不要用多个单词来查＝查不到数据。若完全匹配则可查到。
terms query - in[,]检索= 类似于SQL中in关键字的用法[]||或OR。"query": {"terms": {"name.keyword": ["A", "B"]}} ;
match查询　是评分查询输入会分词，结果列表||OR的。 搜索引擎类似的评分查询评分相关度查询使用match，精确字段查询使用matchPhrase。 MatchPhraseQueryBuilder
matchPhraseQuery查询：输入做分词，要含所有的分词，顺序无差，且连接在一起;"match_phrase"; matchPhraseQueryBuilder.boost(10); matchPhraseQueryBuilder.slop(2);
query_string查询：和match类似，但match要指定字段，query_string是所有字段搜索。
keyword长度受限：中文utf8字符串长度 32766/4=8190 保险。
wildcard query 扫描所有倒排索引, 性能较差。
用 NGram 切分（按照字符切分，最大最小字符数都设置为1），然后查询的时候使用 match_phrase_query 即可，应该比通配符wildcard高效;ngram分词器min_gram max_gram切的粒度太细占存储。
前端用户自主选择matchPhraseQuery"match_phrase"还是termQuery"term"　ES模糊与精准查询： https://blog.csdn.net/frgzs/article/details/88427444
match query比phrase match的性能要高10倍; 精确字段查询使用matchPhrase ; rescoring，计分。
*/
