package md.specialEqp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEQP is a Querydsl query type for EQP
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEQP extends EntityPathBase<EQP> {

    private static final long serialVersionUID = 1147489208L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEQP eQP = new QEQP("eQP");

    public final StringPath cod = createString("cod");

    public final StringPath factoryNo = createString("factoryNo");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.util.Date> instDate = createDateTime("instDate", java.util.Date.class);

    public final SetPath<md.specialEqp.inspect.ISP, md.specialEqp.inspect.QISP> isps = this.<md.specialEqp.inspect.ISP, md.specialEqp.inspect.QISP>createSet("isps", md.specialEqp.inspect.ISP.class, md.specialEqp.inspect.QISP.class, PathInits.DIRECT2);

    public final md.cm.unit.QUnit maintUnt;

    public final DateTimePath<java.time.Instant> nextIspDate1 = createDateTime("nextIspDate1", java.time.Instant.class);

    public final StringPath oid = createString("oid");

    public final md.cm.unit.QUnit ownerUnt;

    public final md.cm.geography.QAddress pos;

    public final StringPath sort = createString("sort");

    public final SetPath<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask> task = this.<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask>createSet("task", md.specialEqp.inspect.Task.class, md.specialEqp.inspect.QTask.class, PathInits.DIRECT2);

    public final StringPath type = createString("type");

    public final BooleanPath valid = createBoolean("valid");

    public final StringPath vart = createString("vart");

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public QEQP(String variable) {
        this(EQP.class, forVariable(variable), INITS);
    }

    public QEQP(Path<? extends EQP> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEQP(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEQP(PathMetadata metadata, PathInits inits) {
        this(EQP.class, metadata, inits);
    }

    public QEQP(Class<? extends EQP> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.maintUnt = inits.isInitialized("maintUnt") ? new md.cm.unit.QUnit(forProperty("maintUnt"), inits.get("maintUnt")) : null;
        this.ownerUnt = inits.isInitialized("ownerUnt") ? new md.cm.unit.QUnit(forProperty("ownerUnt"), inits.get("ownerUnt")) : null;
        this.pos = inits.isInitialized("pos") ? new md.cm.geography.QAddress(forProperty("pos"), inits.get("pos")) : null;
    }

}

