package md.specialEqp.inspect;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QISP is a Querydsl query type for ISP
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QISP extends EntityPathBase<ISP> {

    private static final long serialVersionUID = -1884182496L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QISP iSP = new QISP("iSP");

    public final md.system.QUser checkMen;

    public final StringPath conclusion = createString("conclusion");

    public final md.specialEqp.QEQP dev;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<md.system.User, md.system.QUser> ispMen = this.<md.system.User, md.system.QUser>createSet("ispMen", md.system.User.class, md.system.QUser.class, PathInits.DIRECT2);

    public final DateTimePath<java.util.Date> nextIspDate = createDateTime("nextIspDate", java.util.Date.class);

    public final SetPath<md.specialEqp.Report, md.specialEqp.QReport> reps = this.<md.specialEqp.Report, md.specialEqp.QReport>createSet("reps", md.specialEqp.Report.class, md.specialEqp.QReport.class, PathInits.DIRECT2);

    public final QTask task;

    public QISP(String variable) {
        this(ISP.class, forVariable(variable), INITS);
    }

    public QISP(Path<? extends ISP> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QISP(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QISP(PathMetadata metadata, PathInits inits) {
        this(ISP.class, metadata, inits);
    }

    public QISP(Class<? extends ISP> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.checkMen = inits.isInitialized("checkMen") ? new md.system.QUser(forProperty("checkMen")) : null;
        this.dev = inits.isInitialized("dev") ? new md.specialEqp.QEQP(forProperty("dev"), inits.get("dev")) : null;
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task")) : null;
    }

}

