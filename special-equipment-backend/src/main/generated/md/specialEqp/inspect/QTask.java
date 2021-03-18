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

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTask task = new QTask("task");

    public final md.cm.unit.QUnit constu;

    public final NumberPath<Float> cost = createNumber("cost", Float.class);

    public final DateTimePath<java.util.Date> date = createDateTime("date", java.util.Date.class);

    public final StringPath dep = createString("dep");

    public final StringPath director = createString("director");

    public final StringPath fee = createString("fee");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<Isp, QIsp> isps = this.<Isp, QIsp>createSet("isps", Isp.class, QIsp.class, PathInits.DIRECT2);

    public final StringPath opAddress = createString("opAddress");

    public final md.cm.unit.QUnit servu;

    public final StringPath status = createString("status");

    public final BooleanPath test = createBoolean("test");

    public final BooleanPath verif = createBoolean("verif");

    public QTask(String variable) {
        this(Task.class, forVariable(variable), INITS);
    }

    public QTask(Path<? extends Task> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTask(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTask(PathMetadata metadata, PathInits inits) {
        this(Task.class, metadata, inits);
    }

    public QTask(Class<? extends Task> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.constu = inits.isInitialized("constu") ? new md.cm.unit.QUnit(forProperty("constu"), inits.get("constu")) : null;
        this.servu = inits.isInitialized("servu") ? new md.cm.unit.QUnit(forProperty("servu"), inits.get("servu")) : null;
    }

}

