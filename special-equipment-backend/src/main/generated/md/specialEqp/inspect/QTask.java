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

    public final NumberPath<Float> cost = createNumber("cost", Float.class);

    public final DateTimePath<java.util.Date> date = createDateTime("date", java.util.Date.class);

    public final StringPath dep = createString("dep");

    public final ListPath<md.specialEqp.Eqp, md.specialEqp.QEqp> devs = this.<md.specialEqp.Eqp, md.specialEqp.QEqp>createList("devs", md.specialEqp.Eqp.class, md.specialEqp.QEqp.class, PathInits.DIRECT2);

    public final StringPath fee = createString("fee");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<Isp, QIsp> isps = this.<Isp, QIsp>createSet("isps", Isp.class, QIsp.class, PathInits.DIRECT2);

    public final StringPath status = createString("status");

    public final BooleanPath test = createBoolean("test");

    public final BooleanPath verif = createBoolean("verif");

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

