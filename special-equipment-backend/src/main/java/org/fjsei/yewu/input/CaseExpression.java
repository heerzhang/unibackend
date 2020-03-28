package org.fjsei.yewu.input;

import lombok.Getter;
import lombok.Setter;

//case特殊表达式。
//case函数只返回第一个符合条件的值，剩下的case部分将会被自动忽略。
//支持两种模式：　不能混合用，按照顺序以List<CaseExpression>的第一个结点数据对来　为参考．
// Case  ,When ? Then .., ELSE ... End!


@Getter
@Setter
public class CaseExpression{
    //支持模式１：　when 　简单的 针对某单属性的 = 比较 EQ 相等判定;
    //这种简化情形的属性要在包裹List<CaseExpression>的那个ExpressItem当中设置SX属性, case SX when 。
    //sCase（）:　上层节点设置模式１操作符 sCase；　　而模式２就是空操作符就可以。
    ExpressItem     w;
    //支持模式２：　bool 　更灵活适用性更大的，只要是 逻辑表达式 就允许；
    //模式区分：以List<CaseExpression>的第一个结点为准。
    WhereTree       b;

    //按照顺序bool when都没有的 就是代表 ELSE最后哪一个了，意味着End! 后面的数据对无效。
    //按照顺序排列的； ELSE后直接结束，
    //上层来看，List<CaseExpression>的最后那个节点　ｔ就是 else内容。
    //THEN ...  或　ELSE ...
    ExpressItem     t;
    //表达式;      类型就几个：日期，枚举型,　boolean,　long数值, double数值，　字符串；
}




//模式２： <R> Case<R> selectCase(); 布尔表达式case
//模式１： <C, R> SimpleCase<C,R> selectCase(Expression<? extends C> expression);  直接比较TYPE类型case


/*
Long     l;
Double   d;
String   s;
//graphQL 基本类型 =标量 ID，Float Boolean Int String BigDecimal  BigInteger Byte Char Short Long。就没日期，枚举型单独定义。
//输入input可以自动直接转换。
Date     dt;
Boolean   bo;
*/
