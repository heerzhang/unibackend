package md.specialEqp.inspect;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.*;


/**
 * QIsp is a Querydsl query type for Isp
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QIsp extends EntityPathBase<Isp> {

    private static final long serialVersionUID = -1884181472L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIsp isp = new QIsp("isp");

    public final StringPath bsType = createString("bsType");

    public final md.system.QUser checkMen;

    public final StringPath conclusion = createString("conclusion");

    public final md.specialEqp.QEqp dev;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<md.system.User, md.system.QUser> ispMen = this.<md.system.User, md.system.QUser>createSet("ispMen", md.system.User.class, md.system.QUser.class, PathInits.DIRECT2);

    public final DateTimePath<java.util.Date> nextIspDate = createDateTime("nextIspDate", java.util.Date.class);

    public final StringPath no = createString("no");

    public final md.specialEqp.QReport report;

    public final SetPath<md.specialEqp.Report, md.specialEqp.QReport> reps = this.<md.specialEqp.Report, md.specialEqp.QReport>createSet("reps", md.specialEqp.Report.class, md.specialEqp.QReport.class, PathInits.DIRECT2);

    public final md.cm.unit.QUnit servu;

    public final QTask task;

    public QIsp(String variable) {
        this(Isp.class, forVariable(variable), INITS);
    }

    public QIsp(Path<? extends Isp> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIsp(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIsp(PathMetadata metadata, PathInits inits) {
        this(Isp.class, metadata, inits);
    }

    public QIsp(Class<? extends Isp> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.checkMen = inits.isInitialized("checkMen") ? new md.system.QUser(forProperty("checkMen")) : null;
        this.dev = inits.isInitialized("dev") ? new md.specialEqp.QEqp(forProperty("dev"), inits.get("dev")) : null;
        this.report = inits.isInitialized("report") ? new md.specialEqp.QReport(forProperty("report"), inits.get("report")) : null;
        this.servu = inits.isInitialized("servu") ? new md.cm.unit.QUnit(forProperty("servu"), inits.get("servu")) : null;
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task"), inits.get("task")) : null;
    }

}

