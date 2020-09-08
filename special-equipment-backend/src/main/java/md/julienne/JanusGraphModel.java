package md.julienne;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import javax.persistence.*;

//图计算测试     存储层用Cassandra+Spark
//图数据库的API方式像SQL的JDBC的API接口做法。比如JanusGraphModel**Repository   .save()  .FindAll();就不能用JPA做了。


@Getter
@Setter
@NoArgsConstructor
@Data
public class JanusGraphModel {
   // @Id       图数据库不能用JPA了。
   // @GeneratedValue
    protected String id;

    private String title;
    private String author;

    public JanusGraphModel(String title, String author){
        this.title=title;
        this.author=author;
    }
}

