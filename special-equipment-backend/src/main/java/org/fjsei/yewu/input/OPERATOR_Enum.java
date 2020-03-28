package org.fjsei.yewu.input;

//逻辑操作符符号;

//操作符 =, != ,>=, <=, > ,<, in([])  BETWEEN([a,b]),  Like相似'自带通配符' ;不同数据类型操作符分开；
//集合字段去做NEXI不存在判定：可能EXI/NEXI都是成立的，因为集合里头记录分开的２条记录相反的条件。ａｌｌ逻辑？
//集合属性判定不存在这样的对象，用SETNEXI：自动往中间添加not exists(select * from T where exists(子语句;  by hez 2019/03/23


public enum OPERATOR_Enum {
    EQ,
    NE,
    GE,
    LE,
    GT,
    LT,
    BTW,
    IN,
    LK,
    EMPTY,
    NULL,
    EXI,
    NEXI,
    TRUE,
    FALSE,
    NNULL,
    NEMPTY
}



//操作符 =, != ,>=, <=, > ,<, in([])  BETWEEN([a,b]),
//Like相似'自带通配符' ;不同数据类型操作符分开；
//noteixests; isEmpty, isNull;  集合字段Not Exist特殊！上层节点treeNode.=SetNoExists底层自动做subquery；　
//SETNEXI 移到上以层次 变身 DSELF；
//特殊的枚举{INT, String}可做in;
//graphQL基本类型就没日期,但是中间框架能够自动转换。
