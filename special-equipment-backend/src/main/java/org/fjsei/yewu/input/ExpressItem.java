package org.fjsei.yewu.input;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

//JPA运算操作 表达式：　代表属性字段或者取值以及比较的配对的字段属性，　比如 Isp.cod='hao', 等号的左右都可称作JPA表达式。
//其他的表达式：数学表达式，函数表达式(输出属性的类型)，除了布尔表达式(它在上一层次的结构里面)
//和JPA的 Expression<?> 对应；
//对可相互替换的域，可考虑默认优先级排序，。
//中间纯粹的运算符结点，很可能根本不需要其他字段{需要嵌套的}， 但是有些运算符和参数形式可以简化处理：直接定义在本该是上一级结点内就可以了。
//alias别名　无法引用，放弃支持。subOut()函数用于替换一小部分alias情形。

@Getter
@Setter
public class ExpressItem {
    //数值型的表达式，和其他类型表达式。
    //运算符号。算数表达式--二叉树
    //数值型+ - * / % 五种运算符号;  abs(绝对值) ,neg(取负反数);
    //concat(As,Bs), substr(A,idx,len),locate(A,s,i),strLen(); lower,upper,trimH/B/T("#",s),literal,
    //Max, Min，Sum, Avg;  some, all, greatest, least
    //size, count();    addDate(date,3天); currentDate,
    //Coalesce ;　sCase（）; subOut()表示子语句输出列的引用。
    //y运算操作符=null 表示=运算叶子结点？ subquery;或者直接选出单个属性。
    String    y;
    //函数 #StrLen, #lower（）, #upper ；函数型修饰标签。
    //trim();   substr(),  locate(,);
    //自动生成的特殊count(id/*) 子语句。 对某个cb.size(集合字段的大小)实际底层将自动转成关联subquery;
    //.#SetMax,.#SetMin  .#SetSum, .#SetAvg     数值(/double/Long),　日期Max,Min， 有实际用途意义。
    //some=any/ all子查询用于和x进行比较的， 直接输出subquery表达式
    //函数当作运算符号？
   // String      fun;
    //属性--常量'标题||'函数？　ｓｅｌｅｃｔ  concat(A || 'ccv');
   // String    sel;
    //简单属性名称　
    String      s;
    //subquery子语句，subquery(select B.x from B) as _B_x_ 可看作特别的属性。
    //这个SubSelect实际能和ExpressItem同一个层代替其使用的，下沉到其对象内部定义，实际用法多层{sub:{}},其它字段空的。
    SubSelect sub;        //代替属性sx的。
    //递归嵌套
    //  List<ExpressItem>  expl;
    //简单的表达式运算符号 SetSize就不需要什么左右的因子，直接上简单属性名；，
    ExpressItem l;
    //需要２个的运算 (X + Y);
    ExpressItem r;

    //表达式;      类型就几个：日期，枚举型,　boolean,　long数值, double数值，　字符串；
    //属性和操作符的序列；    关联对象级联也得去join;  数值和属性名字区分。
    //diff=-;
  // List<String>  exp;   //简易的数值型表达式+ - * / %, 后缀表达式模式；对集合某个记录的字段放这里没有意义除非是集合聚合函数。
    //表达式;      类型就几个：日期，枚举型,　boolean,　long数值, double数值，　字符串；
    //graphQL 基本类型 =标量 ID，Float Boolean Int String BigDecimal  BigInteger Byte Char Short Long。就没日期，枚举型单独定义。

    Long      lv;
    Double    dv;
    String    sv;
    //输入input可以自动直接转换。
    Date      dt;
    //支持五种基本的数据类型，可以直接当作输入的5种，免去类型转换的异常抛出毛病，接口那层就做了这５种的类型检查。
    Boolean   bv;
    int     len =-1;

    //函数的参考值，不能是属性名的;
    // String   ref;       //日期DIFF(sx-ref)具体日,　子字符串2定位 locate(X,'abc')，集合聚合函数最简单比对。

    //找个可替代的级联同义词字段？ Coalesce: 取第一个非null的值 (1，2，。。。)
    //函数的 参数列表...　　Coalesce（）；
    List<ExpressItem>   a;
    //别名；复杂的复合输出列单字段， 只能有1个。Specify only ONE item that is to be returned as the subquery result.
    //引用别名的地方having  where；
    //aliaUse[] 实际没法使用的，改成 函数表达式: subOut();
 //   String      alia;
    //支持从直接底下的数据判定类型模式: １，直接比较TYPE类型sCase  还有２，布尔表达式case
    //按照顺序排列的Case when then else END；
    //cwt本身就是表达式可做字段；也是可能會在where内出現的。　select (case); 两个地方都能出现Case;
    List<CaseExpression>    cwt;
}




// <T> Expression<T>  cb.literal(T value);
//cb.substring(root.get("nextIspDate"),1,4)
//cb.sum( ( Expression ) root.get("nextIspDate") , 22.89 )

