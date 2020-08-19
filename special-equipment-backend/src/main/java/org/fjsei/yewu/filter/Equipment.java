package org.fjsei.yewu.filter;


//interface类比union：偏向数据类型组织用在便利graphQL上层应用的表达。
// 要从数据库初始化相应的实体, 那就有问题了, 因为容器不知道要用该接口的哪一个具体实现来初始化它.
//graphQL语法union SearchResult = Photo | Person #union不可嵌套接口=只能是普通对象类型不能是标量类型 ... on Person。

public abstract class Equipment {
    //这里无需定义字段；请在*.graphqls外模型的配置文件定义允许的输出字段名。
    protected Long id;
    private String cod;
    private String type;
    private String sort;
    private String vart;
}


