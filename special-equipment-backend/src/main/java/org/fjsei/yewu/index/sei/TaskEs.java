package org.fjsei.yewu.index.sei;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.Date;


/**
 * 测试先用, 准备删除！，
 * 应该引进 IspEs，加快Isp检索可能有点用处？？
 */

@Data
@NoArgsConstructor
public class TaskEs {
    @Id
    protected Long id;
    //ES嵌套很深的？
    private String dep;  //类型弱化? ,应该是部门表的ID!
    private Date date;
    private String status;
    private String  fee;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date endDate;
}


/*
如果是EAGER，那么表示取出这条数据时，它关联的数据也同时取出放入内存中;用EAGER时，因为在内存里，所以在session外也可以取。
如果是LAZY，那么取出这条数据时，它关联的数据并不取出来，在同一个session中，什么时候要用，就什么时候取(再次访问数据库)。
但在session外就不能再取了,failed to lazily initialize a collection报错；因为操作实际被多场次的数据库session分开。
查询一个graphQL按策略执行器（因为性能要求）＝＝〉并发分解的和异步的策略＝＝〉导致分配到了多个数据库session了。
*/


