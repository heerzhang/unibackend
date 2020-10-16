package org.fjsei.yewu.input;

import lombok.Getter;
import lombok.Setter;
//graphQL内省和接口参数输入的场合，实际只用Getter就可以。
//[语法核心限制] 查询和变更Query, Mutation的接口输入的参数类型不可以用外模型object对象类型=type定义的啊！！。
//Input Object：输入对象 定义的数据类型，因为 Object 的字段可能存在循环引用a->b->a，或者字段引用了不能作为查询输入对象的接口和联合类型。
//因为type普通对象直接用于输入参数的话,语义比较模糊,所以graphQL要求另外单独建
//都是String类型的，input弱类型化。 前端往后端这边的输入数据接口直接json/无类型，无需要区分/数值/日期/bool/字符Byte/字符串。
//弱类型String顺带可以鉴别null还是""的区分，能强化语义场景。
//规则：没有在input当中出现的属性字段名，那它就省略掉，就是不知道有没有变化;

@Getter
@Setter
public class DeviceCommonInput {
    //这里字段名字必须和外模型相同的。
    String  cod;
    String  address;
    String  oid;
    String   lat;
    Long    ownerId;
    //String  ownerId;
}


//基本类型Built-in GraphQL::ScalarType有5种 String Int  ID  Boolean Float {精度小数点后5位}。
