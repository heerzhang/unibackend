package md.specialEqp.inspect;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTask is a Querydsl query type for Task
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTask extends EntityPathBase<Task> {

    private static final long serialVersionUID = 1720227115L;

    public static final QTask task = new QTask("task");

    public final DateTimePath<java.util.Date> date = createDateTime("date", java.util.Date.class);

    public final StringPath dep = createString("dep");

    public final ListPath<md.specialEqp.EQP, md.specialEqp.QEQP> devs = this.<md.specialEqp.EQP, md.specialEqp.QEQP>createList("devs", md.specialEqp.EQP.class, md.specialEqp.QEQP.class, PathInits.DIRECT2);

    public final StringPath fee = createString("fee");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<ISP, QISP> isps = this.<ISP, QISP>createSet("isps", ISP.class, QISP.class, PathInits.DIRECT2);

    public final StringPath status = createString("status");

    public QTask(String variable) {
        super(Task.class, forVariable(variable));
    }

    public QTask(Path<? extends Task> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTask(PathMetadata metadata) {
        super(Task.class, metadata);
    }

}

