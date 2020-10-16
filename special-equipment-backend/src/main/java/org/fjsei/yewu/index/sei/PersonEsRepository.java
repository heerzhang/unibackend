package org.fjsei.yewu.index.sei;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.util.Streamable;


public interface PersonEsRepository extends ElasticsearchRepository<PersonEs, Long> {

    Streamable<PersonEs> findAllByNameContains(String name);
    //等价findAllByNameContains
    //FieldType.Keyword情况下和findAllByNameContains一样的，很正常的,像SQL like %A%。
    Streamable<PersonEs> findAllByNameContaining(String name);
    //findByNameLike等价findByNameStartingWith; Contains/Containing等价"*?*"通配符查询;
    //@Field(analyzer ="ik_smart", searchAnalyzer ="ik_smart")表现正常了；
    @Query("{\"wildcard\": {\"name.keyword\": {\"value\": \"*?0*\"}}}")
    Streamable<PersonEs> findAllByName_KeywordContains(String name);

    Streamable<PersonEs> findAllByNameLike(String name);

    Streamable<PersonEs> findAllByNameMatches(String name);

    //对FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word"可做到&&关系能缩小匹配队列。name可多词组成的；要分词的。
    Streamable<PersonEs> findAllByNameMatchesAndNameMatches(String name, String name2);

    Streamable<PersonEs> findAllByNameIn(String[] names);
    //没现成的，只能自己做：FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word"时　效果挺不错的。
    //FieldType.Keyword字段就完全不能用！
    //@Field(analyzer ="ik_smart", searchAnalyzer ="ik_smart")很好；
    @Query("{\"match_phrase\": {\"name\": {\"query\": \"?0\"}}}")
    Streamable<PersonEs> findAllByNameMatchePhrase(String name);
    @Query("{\"match_phrase\": {\"name\": {\"query\": \"?0\"}}}")
    Streamable<PersonEs> findAllByNameMatchePhrase2(String name, String name2);

    @Query("{\"query_string\": {\"query\": \"\\\"?0\\\" \\\"?1\\\"\",\"fields\":[\"name\"],\"default_operator\":\"and\"}}")
    Streamable<PersonEs> findAllByNameQueryPhrase2(String name, String name2);
    //实际等价于query_string上面这个DSL。这里俩个输入参数 ?0 ?1 都是短语phrase可多次单词组成，两个参数逻辑&&AND关系能缩小匹配结果列表。相当于match_phrase但是多个输入。
    @Query("{\"simple_query_string\": {\"query\": \"\\\"?0\\\" \\\"?1\\\"\",\"fields\":[\"name\"],\"default_operator\":\"and\"}}")
    Streamable<PersonEs> findAllByNameSqueryPhrase2(String name, String name2);
}


