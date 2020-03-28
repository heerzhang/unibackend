package org.fjsei.yewu.input;

//和*.graphqls里头对应输入input的定义LJ_TYPE。
//为了简化丰富逻辑， 新增逻辑介入符号，DSELF自反符: 针对过滤Set<>集合字段（集合底下的不存在这样的个体）;         @㊟：by  herzhang:2019-03-23


public enum LJ_TYPE_Enum {
    AND,
    OR,
    NOT,
    DSELF
}



//conjunction 和 disjunction ，分别是使用 AND 和 OR 操作符进行来联结
//DSELF自反符    noExist 　子语句，  集合函数运算？	deny self；
/*
逻辑符号DSELF，  放在这层次位置适合;
针对性支持：某Set<?>属性字段它的集合当中不存在这样子的对象。 自反not exists隐含嵌套子语句。
样本；
目标ISP：
select isp0_ from ISP isp0_ where ...
AND {
not  (exists ( select *
    from ISP isp2_
        where
            isp0_.id=isp2_.id
            and { ... }
  }
*/

