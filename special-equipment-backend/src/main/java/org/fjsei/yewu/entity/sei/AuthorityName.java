package org.fjsei.yewu.entity.sei;

//关联靠id;而这里权限的名字和id对应关系很关键，
//要 ROLE_  开头的；
//注意！ id不应该随着AuthorityName的增删改而导致ID变动。  看Table( AUTHORITY )
//AUTHORITY表，不会从AuthorityName这自动增删改的。
//Authority需要人工匹配enum AuthorityName的数据。
//"不支持中文的名字"  graphql还要配套对照这个文件 graphql/common.graphqls 前端对应名字。

//enum AuthorityName {  ROLE_SOMEONE,  ROLE_USER,  ROLE_ADMIN,   ROLE_Manager }

public enum AuthorityName {
    ROLE_SOMEONE,        //对应Authority实体表的ID=1；   非登录账户的临时验证通行的，只读很小一部分数据权限。
    ROLE_USER,           //ID 2；        登录的账户最低权限。
    ROLE_ADMIN,         //ID=3;  最高权限。
    ROLE_Manager
}


