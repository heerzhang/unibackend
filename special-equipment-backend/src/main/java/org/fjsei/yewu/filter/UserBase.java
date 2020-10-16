package org.fjsei.yewu.filter;


//interface类比union：偏向数据类型组织用在便利graphQL上层应用的表达，不是给后端用的。
//实体类不可搞 interface： ？可能死循环。
// 要从数据库初始化相应的实体, 那就有问题了, 因为容器不知道要用该接口的哪一个具体实现来初始化它.

public interface UserBase {
}


