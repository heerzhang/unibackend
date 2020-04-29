package md.julienne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.system.User;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String title;

    private String  plain;  //菜谱描述的文本形式

    private Date updatedAt;
    private String author;  //原始的版权人
    //这个字段改成图片文件ID； 不是实际的图片URL;
    private String image;

    @ManyToOne
    @JoinColumn
    private User createdBy;   //添加人
            //大的文本和数据！
    @Lob
    @Basic(fetch= FetchType.LAZY)
    private String description;  //菜谱描述的富文本json字符串。

    //简单处理的非结构 信息。
    private String ingredients;   //配料明细的json对象字符串。

    public  Recipe(String title, String author, String plain, String image, String ingredients, String description){
        this.title=title;
        this.author=author;
        this.plain=plain;
        this.image=image;
        this.ingredients=ingredients;
        this.description=description;
    }
}

//菜谱
