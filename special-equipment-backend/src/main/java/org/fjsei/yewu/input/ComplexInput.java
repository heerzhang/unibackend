package org.fjsei.yewu.input;

import lombok.Getter;
import lombok.Setter;

//因为type普通对象直接用于任何接口函数的input输入参数的话,语义比较模糊,所以graphQL要求另外单独建

//与外模型配套的POJO适配器类。
//所有的输入属性字段名字都分类和合并，归集到了一个或者几个的input模型对象类当中。没有必要建立一对一的很多个的专用输入类。
//Input类模型的用法：就像一把大刷子，有预定义的就合格了可以API哪里输入通过，具体怎么用让接口函数自己去拿主意。
//属性名，取值null,"",　鉴别null还是"",                        java规则：$ 、字母、下划线开头都行，
//如何设置null的办法：数值型/日期/bool/字符Byte/字符串/ ，输入都是String=""，若是该字段允许null就算，字符串特殊若属性可空就null不可空""，可让接口自主决定。

//REST那样子直接拿Object类做输入input参数的用法有问题：如何知道数据是全改了还是只改了一个字段，全部属性必然都从前端再次带入而且覆盖后端数据库，安全性，性能和可靠性问题。
//graphQL的规则：没有在input当中出现的属性字段名，那它就省略掉，亦是就是不知道有没有变化，持久化去数据库时就不要修改这些属性。接口函数具体负责深层次合法性校验。


@Getter
@Setter
public class ComplexInput {
    //Date date; 可支持从前端输入String的日期，能自动转换成了后端Input的Date类型。graphQL底层自动处理;
    String  username;
    String  mobile;
    DeviceCommonInput   dev;
    public String getUsername() {
        return username;
    }

    public String getMobile() {
        return mobile;
    }

}


