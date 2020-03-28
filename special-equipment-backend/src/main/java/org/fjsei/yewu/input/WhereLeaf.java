package org.fjsei.yewu.input;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

//逻辑bool表达式的叶子结点（在它底下才是表达式｛数值函数或字段属性的生成公式或复合体｝）。

//Input Object：输入对象是为了query而定义的数据类型，
//禁止字段引用：不能作为查询输入对象的接口和联合类型。
//关联属性过滤查询集合字段，join/joinSet的模式默认Inner join;若是Left Outer Join需求可以通过其他途径解决/或化解转换。
//多对多关系的属性下级字段做where过滤就需要join两次，其他的只需要join一次；
//多对多的,下级字段比较遇到上级属性是空集合的： left outer join才能判定集合是否为空的，代替inner join。
//任意一个集合字段的判定null空，必须上left outer join；  左边表是操作主题/实体模型类， 右边表都是它的下级关联模型实体表，多级关联=下级的下级关联。
//一对一、多对一的非集合属性，也要上left outer join 否则左边表这边null？无法判定；cross join只有两边表都有的才显示，左边表有但是null就不显示。

//存在性Exist:非关联的实体就不给考虑了；
//不考虑isMember() isNotMember,关联ID代替对象。
//注意：实体模型类中要使用Set而不是List来表示集合属性字段。
//简单的和复合表达方式可以并存使用的模式，语义上支持扩展。可以设置优先用那一个；
//表达式ExpressItem优先。


@Getter
@Setter
public class WhereLeaf {

    //必填字段sx，不能为空，必须能够匹配java的entity实体； .#Set表示最后属性是集合操作，其它.#特殊操作最后属性是基础数据类型。
    //X.id可以指定id来代表某个实体，ID比较 equal=; !=;  。
    //Parent.child.X;  字段分隔符.符号, 不限定级联层级;
    //保留字段: 如 ISP.ispMen.#SetSize：代表集合属性的记录个数,可以支持特殊操作=人数/报告数/设备数。 _SetSize不能做成一般的模型实体的字段来用。
    //保留字段 #StrLen: 字符串长度判定。　　
    //最后一个附加字段可指出集合聚合操作，且必须#字符开头的。.#SetMax,.#SetMin, 代表其中的最大最小，.#SetSum, .#SetAvg。  ?还要数值型; 要做Set子查询。　count()。
    //String类型字段无法支持这些操作Max,Min,Sum,。    集合日期字符串聚合意义 cb.greatest，cb.least；
    //集合属性下搞聚合Max/Sum就必须用subQuery{不是exists,而是select单个数值型的并加(select..)>=N比较用的};  greaterThan代替ge; //聚合query结果Tuple?
    //函数length()：字符串长度； #StrLen, #lower,#upper小写，
    //ExpressItem  l 若是非空的，那么 s就废弃；
    //支持subEntity.get("checkMen")= get(ispMen.#1)　？关联的子查询语句 User=Set<User>都能接受。
    String   s;      //直接了当的属性字段名,  A.B.#1 往前回退一层subquery对应的上层查询内的可识别属性。

    //操作符左边的内容；　
    //复杂的表达式, 若　l有的　s就忽略了。　
    //s属性字段可下沉入到ExpressItem的s；
    ExpressItem     l;

    //缺省就是 = eq；
    //IN , NOT IN[子查询语句];
    //测试集合非空isNotEmpty(Expression<c> collection);
    //isNull 或是 集合空,
    //枚举形式In;[NOT] IN， 针对普通非关联下级属性字段/基本类型；    X.zd in ('a','c');
    //集合字段不存在这样的对象ID; NOT(set.has()); 不存在这样的？简单逻辑无法搞定 !! 只能搞subQuery呢?。注意：not即相反逻辑:存在这样的=就可以使用简单逻辑做。
    //关联到最新一条检验？，求取最新那一条？ IN/NOT(Select top3 ID/names from 带Join 原集合 order by ISP.Time desc)
    //isNotMember和isEmpty类似用法,后面一个参数是集合型的字段。
    //直接bool的操作： noNull,null,true,false; setEmpty=null, setNotEmpty=noNull;
    //可直接指示表达式操作具体数值类型，Long,Double数值表达式就2种类型。
    //日期求取相差天数 DIFF(sx-ref);
    //逻辑操作符符号  =, != ,>=, <=, > ,<, in([])  BETWEEN([a,b]),
    //Like相似'自带通配符' ;不同数据类型操作符分开；
    //noteixests; isEmpty, isNull;  集合字段Not Exist特殊！上层节点treeNode.=SetNoExists底层自动做subquery；　
    OPERATOR_Enum  o;

    //操作符右手边的内容；有些逻辑操作符不需要r　等右手边的内容;
    ExpressItem     r;

    //这个层面直接安排数据，　就没必要下沉到ｌ　ExpressItem　ｒ这些对象下面去了；　简化点{ {} }。
    //val和arr字段二选择其一{必须至少有一个非空的值}，首选val;
    String     sv;  //5种基本类型，取值， %abc?d  ;通配符; 日期，数字型的; bool操作省略。
    Date       dt;
    Double      dv;
    //Boolean    bv；　布尔 就省了
    Long        lv;
    //这个层次也配套5种基本数据类型的位置，也可帮助简化对象表达嵌套层次。
    //如IN(1,2,3,..4)， in中的长度不能大于1000;
    //BETWEEN{A,B}　In[,,]
    List<String>    sa;       //[数组型的]取值集合； 可能是枚举的类型。
    List<Date>      dta;       //between[date1, date2]
    List<Double>    da;
    List<Long>      la;
    //配套SETNEXI的描述條件限定某集合字段底下的那些个对象的过滤条件，Not( Set<A>.exists(A.where) )。
    //SubSelect      sub;       　//下沉到ExpressItem底下
}





//JPA内存对象可直接比较，判定是否是集合属性的成员中一条：[NOT] MEMBER OF， 针对关联Set/List类型字段；isMember()对象ID。
//Root和Path都是Expression，在其<JavaType>一致时就可直接比较;
//Query构造在hibernate当中是Criteria，而JPA当中叫做TypeQuery；通过适配器对接的　CriteriaQueryTypeQueryAdapter。
//JPA实体关联已经都是靠ID维护着关联关系1:n/1:1/n:m的，ID没有算啥玩意？肯定不正常的，? 没关联实体意味着数据缺失，基本是非法的。
//所以，正常情况INNER JOIN就可以了。LEFT  JOIN对于进入下一级字段过滤条件null了啥意义。
//有子查询的情况才会有多个Set<Root<?>> roots=query.getRoots();否则正常只能一个Root，它是和From配对的，
//普通join都不关Root的事。Where里头from子句也不要Root；where ( exists(select * from B) )就不需要Root再多做一个。

//cross join即=inner join,
//JPA有需要访问下级属性特定某个字段才需要join;否则不需要要join. 比如仅仅测试下级属性的null/Empty()/size()这些就不需要join。
//非集合的字段且是关联对象型的；若是只需要inner join的模式，也可无需在代码明确join,可直接root.get("task").get("status")就可/自动会cross join;


