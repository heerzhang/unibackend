package org.fjsei.yewu.input;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

//20200817后版本不能用WhereTree来做前端查询了！input类型不直接使用Object Type呢？Object字段可能存在循环引用，或字段引用不能作为查询输入的接口和联合类型。
//graphQL input递归无法再使用！导致ModelFilters这层类sql看来要退出前端动态解释型领域，只剩下给不想写HQL代码的后端程序静态型用 = 作废WhereTree了。

//逻辑bool表达式的根结点(或中间结点)
//谓词Predicate:"and or"；
//子查询Subquery没多大意义，通过业务层面变换消除，就不考虑。
//泛型<T>指定仅对值的类型为 T 的成员计算 In<T> 表达式。'x.name'路径表达式的类型String;
//谓词表达式Expression层次结构图：      http://www.uml.org.cn/j2ee/200912114.asp#fig1
//Criteria提供一并列版本，属性通过名称进行引用=类似Java Reflection 反射，通过牺牲编译时类型检查来支持动态查询构造。使用弱类型API编写;
//下一级属性字段一对多，多对多的要从SetJoin绕道，去取得Path做表达式。
//WhereTree代表上层逻辑( ) 亦即刮号; WhereLeaf最终节点布尔表达式。


@Getter
@Setter
public class WhereTree {
    //and ,or,  noExist, not{仅对son只能一个条件!(not son? not at)，不能not ats,not sons}
    //OR必须至少有1个过滤条件｛必须用sons[0,1,]｝。
    //AND可以是唯一1个条件{可用son}。
    //logic为空的=AND缺省();
    //noExist {仅对son只能一个条件! set必须有；不能直接上ats叶节点}  ；集合字段Not Exist特殊！
    //对单个叶子结点at存在的而且ats/sons都不存在的：AND OR失去意义。
    LJ_TYPE_Enum  lj;

    //OR AND (1&&2 ...&&3) (1||2...||3);这个sibling就是1..3;
    //sons可以为空的，若是叶子结点就是son=null的话=那就必须有东西。
    //sons和son至少有一个非空的；sons优先级上覆盖son，两者取其一。
    WhereTree w;
    //相当于()多一层 OR(), AND() ,NOT();
    //非叶子结点son必须有东西嵌套()。
    //son和sons两个都有的: a AND (b ...);
    List<WhereTree>    ws;
    //非叶子结点的可以没有；
    //at=最终的字段过滤条件：
    //noExist可以直接上at缺省逻辑, 但不能直接上ats应该有son下层逻辑。
    //ats优先级上覆盖at，两者取其一。
    WhereLeaf a;
    //降低嵌套层次; 。
    //最终叶子逻辑节点也可以多个一起拼凑；简化组织结构。　例子　and x.a= and x.b< and x.c in[1,2,3];
    //支持可省略掉WhereTree节点直接就上List<WhereLeaf>代表逻辑AND 默认 AND;
    List<WhereLeaf>      as;
    //集合字段不存在这样子的。 自反not exists隐含嵌套子语句。

    //DSELF配套的：可以省掉一个嵌套exists ( sub )
    WhereTree sup;
}





//in，not in 列举性的，　any=some，all　数值比较性的都是用于 子查询关键词之一;　子查询语句针对不同的实体Type暂不作考虑;
//sql语句中逻辑运算符优先级跟c一样，not > and > or (c里面是 ! > && > || );
//sql里多用（）来改变执行顺序
//AND NOT EXISTS 带有EXISTS谓词的子查询不返回任何数据，只产生逻辑真假。 ?=〉关联集合Many 1:N, N:N; N里头有这样的吗；
//exists(Subquery<?> subquery); 针对的是子查询？EXISTS？

//用having就一定要和group by连用，用group by不一有having;

//多值属性x.Set<>例子 SetJoin<Customer, Order> o ="[Root<Customer>]"c.join(Customer_.orders);  类似地ListJoin。
//Path<T>，表示从 Root<?> 表达式导航到的持久化属性字段。Root<T> 是一个没有父类的特殊 Path<T>。      http://www.uml.org.cn/j2ee/200912114.asp#fig1
//JPA注意多个Root搞出的，CROSS JOIN 把表A和表B的数据进行一个N*M的组合，即笛卡尔积，数据量爆炸。 LEFT  JOIN 左主表全取。INNER JOIN两表都有的才算。
//很多情况下用Join性能要比用Subquery好。子查询也称内部查询，而包含子查询的语句称外部查询。许多含嵌套子查询SQL语句都可改等效的Join表示。
//当然Subquery在某些情况下还是有优势，比如不相关Subquery使用Exist/not exist/或Subquery中做加总，另外还要考虑外围查询结果的数据量。https://blog.csdn.net/kevinsqlserver/article/details/7804855

//编译错误:方法应用到给定类型,推断类型不符合上限, 范型： 如cb.<Date>greatest(join3.get("upLoadDate") ) 添加类型修饰符号。
//JPA集合字段不能直接读取的： root.get("reps").get("upLoadDate") ) 报错，必须join或subQuery; 　非集合型的属性可以直接读取。


