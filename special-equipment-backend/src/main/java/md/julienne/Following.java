package md.julienne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.system.User;

import javax.persistence.*;
import java.io.Serializable;

//表：关系可以进一步拆解，增加中间的独立关系表，传递关系。
//Ｎ:Ｎ关系的中间表，特别形式，增加新的字段，明确声明的实体类，但是没有Repository接口类。
//因为没有独立的单个字段形式ＩＤ，无法引入Repository，所以该实体的查询记录无法分页显示。

//用户following you.关注？接受关注请求;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Following_Follower")
public class Following implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "toUser_id")
    private User toUser;


    @Id
    @ManyToOne
    @JoinColumn(name = "fromUser_id")
    private User fromUser;

    //上面两个字段toUser_id+ fromUser_id，组成合并ＩＤ唯一性,没有独立单一ＩＤ字段。

    //Ｎ:Ｎ关系的中间表　增加新的字段；
    //就是为了 confirmed  字段才需要特意增加本实体类的。
    private Boolean confirmed;

}



//假如多对多中间表业需要分页查询的话，那么这个做法不妥，必须更复杂点：引入Repository和完全独立的普通实体表+独立ID。
//多对多分解成了一对多＋中间表＋多对一的模式，就是手动添加中间实体表，而不是自动生成的才两个索引字段ManyToMany中间表模式。

//这个是多对多中间表的特别形式，它没有Repository类来操作，只能从两端的实体类间接搞。
//双向ManyToMany多对多；
//每一个单向的多对多ManyToMany A->B ,都能分解成一对多 A->M ＋中间实体表M ＋多对一 M->B。
//JPA关系映射系列五：many-to-many 关联表存在额外字段关系映射 https://blog.csdn.net/dandandeshangni/article/details/79456742



