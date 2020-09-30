package org.fjsei.yewu.index.sei;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.util.Streamable;

//验证举例　"name"字段取值: "目前经营情况及内外部经营环境未发生重大变化"

public interface PersonEsRepository extends ElasticsearchRepository<PersonEs, Long> {
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
    Streamable<UnitEs> findAllByNameMatchesAndNameMatches(String name, String name2);

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
    @Query("{\"match_phrase\": {\"name\": {\"query\": \"?0\"}}}")
    Streamable<UnitEs> findAllByNameMatchePhrase2(String name, String name2);

    //@Query("{\"query_string\": {\"query\": \"?0 ?1\",\"fields\":[\"name\"],\"default_operator\":\"and\"}}") 短语内部单词顺序可以打乱也能匹配：对"name": "纬亿惠州","name2": "锂能" 可匹配到；
    //@Query("{\"query_string\": {\"query\": \"\\\"?0\\\" \\\"?1\\\"\",\"fields\":[\"name\"],\"default_operator\":\"and\"}}") 对"name": "纬亿惠州","name2": "锂能" 匹配不到；
    //"股份有限公司"也必须一起输入，它被当成一个token单词了，'股份有限公司'若再做拆分做输入参数就无法匹配{ik_smart分词器条件下的}。输入当中标点符号等会被filter给舍弃掉。"有限公司"可以匹配'责任有限公司';
    //针对人名字段来做Text分词搜索的，有可能查不到人名。找不到情况下，应该转而使用keyword字段wildcard模糊查找。
    @Query("{\"query_string\": {\"query\": \"\\\"?0\\\" \\\"?1\\\"\",\"fields\":[\"name\"],\"default_operator\":\"and\"}}")
    Streamable<UnitEs> findAllByNameQueryPhrase2(String name, String name2);
    //实际等价于query_string上面这个DSL。这里俩个输入参数 ?0 ?1 都是短语phrase可多次单词组成，两个参数逻辑&&AND关系能缩小匹配结果列表。相当于match_phrase但是多个输入。
    @Query("{\"simple_query_string\": {\"query\": \"\\\"?0\\\" \\\"?1\\\"\",\"fields\":[\"name\"],\"default_operator\":\"and\"}}")
    Streamable<UnitEs> findAllByNameSqueryPhrase2(String name, String name2);
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
match_phrase查询API手册：　https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html
wildcard query　term query　terms query　prefix query　range query都属于不分词的精确匹配，而full-text queries是才分词的。
Text类型不要用term查询而应该用match query;
全文搜索中：phrase就是分词后的固定多词语顺序和单词间隔的{ match_phrase query : 默认max_expansions=50个单词最多了}。boost=增强分数;
multi_match query 是对多个字段{标题+简介2个字段}都去搜同一个query string的。
"query_string":容易报错{二次封装：描述字符串和语法拆解，first_name转义\ 短语引号"John Smith" \"}。 "simple_query_string"更易于使用。
term查询是精确查询的{Text字段不要用term查询}。 terms是OR逻辑满足一个term就匹配上。terms_set查询可以指定最少满足的terms的个项数{minimum_should_match_field可定制，针对数组型字段}。
wildcard查询很耗时，wildcard不能用在Text类型字段。
"query_string" : { "query" :... 这样的DSL片段=属于全文搜索分词！：要分词的查询。供专业用户使用,语法严格容易报错{转义字符\\保留字符多+ - = && || > < ! ( ) { } [ ] ^ " ~ * ? : \ /}。
query_string无法对nested字段生效；必须单独用nested query来做。
object字段类型　"fields" : ["city.*"],可代表内嵌对象city底下的多个字段搜索。举例如下：
"query_string" : { "query" : "city.\\*:(this AND that OR thus)"   }
"query_string":　{ "query":　"NOT(\"?\" \"?\")",  "fields":　["name"]　}　这里的?是Spring Repository自动带入的变量,而"?" "?"是ES语法要求，JSON语法{\"query\": "\?0\"}。
"query_string": {"query":"\"?\" \"?\"",  "fields":["name"，]} 对Text字段的,只能单个单词搜索,两个??代表两个输入参数，然后逻辑OR||合并匹配的；单个参数输入多单词反而无法匹配到{分词！}。
simple_query_string查询是傻瓜接口语法但是不会报错只会忽略掉；有个特别功能可以设置"flags": "OR|AND|PREFIX"来过滤实际要用到的语法保留字符(操作符)。" 包裹phrases短语多单词匹配；
分词之后单词叫token；这时phrase就表示多个token组成的，也就是正常输入和用户要搜索的那一串文本=叫做phrase{包含一个有顺序和间隔的几个token分词/单词}。
simple_query_string相比query_string接口语法功能稍微受到裁剪/制约，少了普通用户都用不到的功能。　举例如下：
"simple_query_string" : {
    "query": "\"fried eggs\" +(eggplant | potato) -frittata", 说明只有"fried eggs\"是短语(全字符串精确匹配)。其它比如frittata都是单词。
    "fields": ["title^5", "body"],
    "default_operator": "and"
}
分词搜索输入当中标点符号等会被filter给舍弃掉。输入"有限公司"可以匹配'责任有限公司'，但是不能匹配'股份有限公司'的公司名字。
输入"责任有限"　无法匹配到，　但是输入"责任"　反而却能匹配到。｛解释：分词后和原来存储不一样的，原来存储的是＇责任有限公司＇｝；姓名汉语的也会拆分姓+名;
禁用 wildcard模糊匹配 ,让Elasticsearch飞起来: https://blog.csdn.net/laoyang360/article/details/85109769?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-2.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-2.nonecase
写入使用ik_max_word，查询时用ik_smart并对查询词的长度进行限制（避免被外部用户叠词攻击）  https://yuerblog.cc/2019/12/06/
根据业务场景，进行恰当内存的分配; 关闭Swap 性能：  https://zhuanlan.zhihu.com/p/67600167
默认分词器standard作中文分词查询不好。 若设置ik分词时查询性能则是standard分词的2-3倍。 https://wenku.baidu.com/view/82b082b5998fcc22bcd10df4.html
elasticsearch 性能测试; jmeter 压力测试工具    https://www.cnblogs.com/sesexxoo/p/6190583.html
rangeQuery("name").gte("99")    范围查询 >a&&<b之内的。
prefixQuery("linkMen.keyword",as.getLinkMen()) 这里prefixQuery对Text字段没意义，需要针对复合内嵌字段keyword来做。
boolQuery().包括四种must，filter, should,must_Not。用filter代替must来提高性能：filter不会像Must一样计算分值{Text分词的分值才有意义}，逻辑语义上filter等价must。
例子　"bool" : { "must" : [ 　{ "match_phrase" : {　}  ]  };     boolQuery().filter();   非全文搜索的直接用filter代替must；
should至少有一个语句要匹配，与 OR 等价。
*match*开头的和*query_string结束的或明显打分有关的语法标签：这些都是Text全文检索；剩下的就是正常的结构化匹配/没有分词的通常意义查询或过滤。
ES多条件查询must和should不能直接跟随地并列在一起的：应当改成嵌套的　(x && y)　&&　( A || B)　逻辑表达形式，如下：
"bool": {
	"must": [{
			"bool": {
				"must": [{　　　x && y      ]
			     }, {
			"bool": {
				"should": [{　　　A || B     ]
				}
    	]
    }
全文检索matchPhraseQuery，可调整slop参数。  matchPhraseQuery("name",as.getName()).slop(2) 容许相差slop个数的单词(顺序调换或多出单词)但是单词缺少绝对不行的。
参数slop() 容许短语匹配相差slop数：有些看着像差异很大，实际也就slop个单词的差别程度，只能多，输入该有的单词都必须有。
．minimumShouldMatch(2) 提示逻辑OR的最少满足项目数。
.filter() 或者bool底下.must_Not() 这两种查询的算是过滤器（不计算_score的）。
termQuery()是精确字符匹配的所以不能用在Text字段上，
termsQuery(,[a,b])任意一个都算=逻辑||OR。　"term"是单个短语串精确匹配，"terms"是多个短语串当中任意一个精确匹配。
terms_set　 TermsSetQueryBuilder 针对集合数组型字段的匹配:类似1:N数据库的１对多关系字段。termsSetQueryBuilder.setMinimumShouldMatchField竟然是用一个字段来做配置；minimum_should_match_script也真的慢。
"minimum_should_match_script": { "source": "Math.min(params.num_terms, doc['required_matches'].value)" } 其中params.num_terms是在terms搜索输入元素个数;doc['required_matches'].value是文档记录字段值。
termsSetQueryBuilder必须配置匹配数/啰嗦?。　termsSetQueryBuilder.setMinimumShouldMatchScript(new Script("2"));　 这里"2"表示最少匹配到2个。
这个terms_set 就是比termsQuery多出能够控制集合字段 底下满足匹配的个数／最少有多少个。
termsLookupQuery（）竟然关联到其他的index索引数据库去了/index_type/id:。跨越索引关联关系。
multi_match  针对多个字段一起来搜索匹配。
existsQuery { "exists": {  "field": "user" }} 如果字段是空字符串""或者包含null的数组[null,"foo"]，那么会被当作逻辑true。
*/

