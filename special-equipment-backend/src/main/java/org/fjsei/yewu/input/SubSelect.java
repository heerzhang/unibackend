package org.fjsei.yewu.input;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//SubSelect选择再多也没用，只能当作一个父语句的过滤条件，只影响graphQL输出的记录数。
//前端使用的安全性： 若是对查询iSPRepository.findAll(modelFilters);设置缓存后，一段时间之内，就不会生成sql，不会到数据库去请求执行，降低被攻击风险。
//预留跟踪前端输入的查询接口过滤条件的跟踪记录能力，审计和防止潜在攻击风险。

//java 属性名称：大小写要区分。
//子语句只能输出一个字段。
//JPA From（）这里不支持子语句的??。
//子查询--无关联的，，，使用场景
//嵌套多层+关联的？属性名字叫化相对最近的子查询:(from DEV where date= & task.dep=#2.X.a)，支持父辈查询属性字段直接; #2.往前追溯前两嵌套父辈。
//#1.A.b 子语句引用前面嵌套的模型语句的属性做predicate表达式等, 　#2往前找2级subquery。

//特殊类型子语句比如 not/exists; 实际要在它的逻辑上层 EXI,NEXI,做。

@Getter
@Setter
public class SubSelect {
    //简单的属性字段， id, B.x;
    String      s;
    //复杂的
    //输出字段或表达式，函数 count, max,sum, concat, Substr();locate{像函数}；
    //这个e底下不应该嵌套子语句,没意义。
    ExpressItem     e;
    //AS ?as2  orederby as2  文literral;'   alias不能在where里头也不用到其下的子语句内。 实际只有order by和having位置能用上。
    //别名；复杂的复合输出列单字段， 只能有1个。Specify only ONE item that is to be returned as the subquery result.
   // String      alia;
    //限制可以使用的模型类type?。
    //可能无关联的,不指定关联参数的，
    String      from;      //子语句的select from模型主类
    //Subquery<T> distinct(boolean distinct);

    //引用前面嵌套的模型语句的属性做predicate表达式  #1.A.b
    //这个where底下可能会嵌套子语句。
    WhereTree   where;
    //Subquery 属于分析层面的， 执行层面才能限制条数 limit ? ; 过滤类当中无法select TOP 20;
    //String      top;        //TOP 几条
  //  String      order;      //子语句TOP的排序 [X.Y]
    //String      asc;        //？desc ;
    //group by  X,Y,Z 和顺序有关系
    List<ExpressItem>     grp;     //聚合count, 多条件分组的字段？　
    WhereTree         hav;       //过滤 聚合。

    //关联的集合字段名，用于配套noExist逻辑。
    //也可用在支持 IN,NOT IN [子查询语句]，用来表示那一个本体<T>模型.字段名去做IN,NOT IN的对照;
   // String   set;
    //有关联的子查询，需明确指定关联的那些？　？不需要明确指出的；在条件where嵌套的上级属性看出．
    //   List<String>   correlate;
}




//Subquery<T>只能输出一个字段，或是一个已经定义好的类<T>实体对象也可以的，就是不能动态组合多个字段来输出。内外逻辑比较时刻类型要一致。
//JPA无法支持限制Subquery输出的记录个数。
//属性：任意不是集合的或集合-》挑选一条null?->属性1 IN/NOT(Select top3 ID/names from 无关联新语句 order by ISP.Time desc)
//特例！ispMen.#SetSize像函数的子语句简化版本 {自动转化成count(id)子语句关联上级isp0_.id=ispmen5_.ISP_ID，无需要明确指出SubSelect}
//集合isEmpty也会底层自动转化成not (exists (select子语句并且关联上级，不需要手动写代码；
//set noteixests集合里头不存在这样的，必须用SubQuery做，;      set有关联聚合，

//SetSize集合字段不过滤直接求其大小？cb.size(集合字段)底层自动转成subquery;
//JPQL不支持limit函数
//对于in中的集合会有一个长度限制，当集合长度大于1000的时候会报错  https://blog.csdn.net/shenyanwei/article/details/75599850

//alias不能在where里头也不用到其下的子语句内。 实际只有order by和having位置能用上, JPA就剩下having可引用用上。

