package org.fjsei.yewu.entity.fjtj;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//旧平台用户登录。
//实际上JPA最关键就是定义这两个接口文件，每个表实体都要搞；其实也挺简单的，就是列举字段属性类型与名字，而对象关联的定义较麻烦。
//实体表旧表引用只需定义想要的字段，id字段必须有唯一性的。

//实体和Repository类可随意地在多个数据库中间搬迁；每个数据库有独立的目录包。但是最好简单名不要相同。
//每个数据源库都要明确一看就知道是哪一个数据库的实体（Repository目录相互不同），简名最好区分咯；不然名字相同就混乱。
//若H2测试数据库；Schema默认Public；还要添加Schema一个: HRUSER，俩个分离的账户或用户库。若Oracle实际Dblink连接的其它库。云数据库代理网关的机制。Java类全名唯一性。
//Table先属于某个schema,定义schema又属于某database三级结构！建database时默认建名public的schema;但不能加参数catalog！ catalog是系统级schema,用于存储系统函数系统元数据。
//像是@Table(name = "",  schema="　",)不推荐使用，数据库对跨schema关联查询的支持不好。


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_USER_INFO",  schema="HRUSER",
        uniqueConstraints={@UniqueConstraint(columnNames={"USER_ID"})} )
public class HrUserinfo {
    @Id
    @Column(name = "USER_ID", insertable=false, updatable=false)
    protected String  userId;        //账号ID

    @Column(name = "PWD")
    private String  password;      //明文密码

    //JPA自动建立表，但是已存在表若已经有的表字段不会重新改的，还比较安全。
    @Column(name = "USER_STATUS")
    private Integer  status;       //=2有效
}

//对于Orcacle多用户schema分开的表。 @Table(name = "TB_USER_INFO",  schema="HRUSER",；不能直接写"HRUSER.TB_USER_INFO"
//@Column( columnDefinition表示创建表时，SQL语句，一般用于通过Entity生成表（DB中表已经建好，该属性没有必要）
