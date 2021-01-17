package md.system;

//[奇葩了]! 关联靠id;而这里权限的名字和id对应关系很关键，
//数据库AUTHORITY表的实际内容已做id关联的角色名必须登记在这里，否则启动报错!
//目前只能人工修改权限配置的角色名称实体表"AUTHORITY"。　角色ID属于稳定很少改动的系统性质配置；
//要 ROLE_  开头的；
//注意！ id不应该随着AuthorityName的增删改而导致ID变动。  看Table( AUTHORITY )
//Authority需要人工匹配enum AuthorityName的数据。

/**graphQL不支持中文的名字；graphql还要配套对照这个文件 graphql/common.graphql 前端对应名字。
* enum AuthorityName {  ROLE_SOMEONE,  ROLE_USER,  ROLE_ADMIN,   ROLE_Manager 不支持中文的}
* 根本不需要数字编号［数据库id不能改动］，只需要字符串EnumType.STRING。
 */
public enum AuthorityName {
    //应该按顺序添加，数据库ID递增，不能乱改表记录。
    //ROLE_cmn = 随意的登录用户。
    ROLE_Ma,    //主模型域基础用户
    ROLE_MaAdmin,   //主域的管理员
    ROLE_Th,    //第三方基础用户
    ROLE_cmnAdmin,   //公共的管理员
}

