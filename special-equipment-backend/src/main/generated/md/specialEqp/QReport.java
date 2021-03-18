package md.specialEqp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = 1597999680L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReport report = new QReport("report");

    public final StringPath auditOperates = createString("auditOperates");

    public final StringPath authorizationUsers = createString("authorizationUsers");

    public final StringPath data = createString("data");

    public final StringPath detail = createString("detail");

    public final SetPath<md.computer.File, md.computer.QFile> files = this.<md.computer.File, md.computer.QFile>createSet("files", md.computer.File.class, md.computer.QFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final md.specialEqp.inspect.QIsp isp;

    public final StringPath modeltype = createString("modeltype");

    public final StringPath modelversion = createString("modelversion");

    public final StringPath no = createString("no");

    public final NumberPath<Double> numTest = createNumber("numTest", Double.class);

    public final StringPath path = createString("path");

    public final StringPath sign = createString("sign");

    public final StringPath snapshot = createString("snapshot");

    public final StringPath status = createString("status");

    public final StringPath type = createString("type");

    public final DateTimePath<java.util.Date> upLoadDate = createDateTime("upLoadDate", java.util.Date.class);

    public QReport(String variable) {
        this(Report.class, forVariable(variable), INITS);
    }

    public QReport(Path<? extends Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReport(PathMetadata metadata, PathInits inits) {
        this(Report.class, metadata, inits);
    }

    public QReport(Class<? extends Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.isp = inits.isInitialized("isp") ? new md.specialEqp.inspect.QIsp(forProperty("isp"), inits.get("isp")) : null;
    }

}

