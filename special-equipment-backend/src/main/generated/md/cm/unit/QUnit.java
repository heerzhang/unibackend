package md.cm.unit;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUnit is a Querydsl query type for Unit
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUnit extends EntityPathBase<Unit> {

    private static final long serialVersionUID = 2113441345L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUnit unit = new QUnit("unit");

    public final StringPath address = createString("address");

    public final StringPath area = createString("area");

    public final md.cm.base.QCompany company;

    public final SetPath<Division, QDivision> dvs = this.<Division, QDivision>createSet("dvs", Division.class, QDivision.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath indCod = createString("indCod");

    public final SetPath<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp> isps = this.<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp>createSet("isps", md.specialEqp.inspect.Isp.class, md.specialEqp.inspect.QIsp.class, PathInits.DIRECT2);

    public final NumberPath<Long> jcId = createNumber("jcId", Long.class);

    public final StringPath linkMen = createString("linkMen");

    public final SetPath<md.specialEqp.Eqp, md.specialEqp.QEqp> maints = this.<md.specialEqp.Eqp, md.specialEqp.QEqp>createSet("maints", md.specialEqp.Eqp.class, md.specialEqp.QEqp.class, PathInits.DIRECT2);

    public final NumberPath<Byte> mtp = createNumber("mtp", Byte.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> oldId = createNumber("oldId", Long.class);

    public final SetPath<md.specialEqp.Eqp, md.specialEqp.QEqp> owns = this.<md.specialEqp.Eqp, md.specialEqp.QEqp>createSet("owns", md.specialEqp.Eqp.class, md.specialEqp.QEqp.class, PathInits.DIRECT2);

    public final md.cm.base.QPerson person;

    public final StringPath phone = createString("phone");

    public final SetPath<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask> tasks = this.<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask>createSet("tasks", md.specialEqp.inspect.Task.class, md.specialEqp.inspect.QTask.class, PathInits.DIRECT2);

    public QUnit(String variable) {
        this(Unit.class, forVariable(variable), INITS);
    }

    public QUnit(Path<? extends Unit> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUnit(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUnit(PathMetadata metadata, PathInits inits) {
        this(Unit.class, metadata, inits);
    }

    public QUnit(Class<? extends Unit> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new md.cm.base.QCompany(forProperty("company"), inits.get("company")) : null;
        this.person = inits.isInitialized("person") ? new md.cm.base.QPerson(forProperty("person"), inits.get("person")) : null;
    }

}

