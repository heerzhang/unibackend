package org.fjsei.yewu.input;

import lombok.Getter;
import lombok.Setter;

//输入参数，Mutation的输入input字段，或者可用作搜索Query的过滤字段。

@Getter
@Setter
public class UnitCommonInput {
    //这里字段名字必须和外模型相同的。
    boolean company;    //是企业的还是个人的
    String  name;
    String  no;         //外部数据源的关键字, 统一社会信用代码/身份证号码
    String  address;
    String  linkMen;
    String  phone;
}


