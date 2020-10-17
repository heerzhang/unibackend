package md.specialEqp.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAmusement is a Querydsl query type for Amusement
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAmusement extends EntityPathBase<Amusement> {

    private static final long serialVersionUID = 988301521L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAmusement amusement = new QAmusement("amusement");

    public final md.specialEqp.QEQP _super;

    //inherited
    public final StringPath cod;

    //inherited
    public final StringPath factoryNo;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final DateTimePath<java.util.Date> instDate;

    //inherited
    public final SetPath<md.specialEqp.inspect.ISP, md.specialEqp.inspect.QISP> isps;

    // inherited
    public final md.cm.unit.QUnit maintUnt;

    //inherited
    public final DateTimePath<java.time.Instant> nextIspDate1;

    //inherited
    public final StringPath oid;

    // inherited
    public final md.cm.unit.QUnit ownerUnt;

    // inherited
    public final md.cm.geography.QAddress pos;

    //inherited
    public final StringPath sort;

    //inherited
    public final SetPath<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask> task;

    //inherited
    public final StringPath type;

    //inherited
    public final BooleanPath valid;

    //inherited
    public final StringPath vart;

    //inherited
    public final NumberPath<Integer> version;

    public final StringPath volume = createString("volume");

    public QAmusement(String variable) {
        this(Amusement.class, forVariable(variable), INITS);
    }

    public QAmusement(Path<? extends Amusement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAmusement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAmusement(PathMetadata metadata, PathInits inits) {
        this(Amusement.class, metadata, inits);
    }

    public QAmusement(Class<? extends Amusement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new md.specialEqp.QEQP(type, metadata, inits);
        this.cod = _super.cod;
        this.factoryNo = _super.factoryNo;
        this.id = _super.id;
        this.instDate = _super.instDate;
        this.isps = _super.isps;
        this.maintUnt = _super.maintUnt;
        this.nextIspDate1 = _super.nextIspDate1;
        this.oid = _super.oid;
        this.ownerUnt = _super.ownerUnt;
        this.pos = _super.pos;
        this.sort = _super.sort;
        this.task = _super.task;
        this.type = _super.type;
        this.valid = _super.valid;
        this.vart = _super.vart;
        this.version = _super.version;
    }

}

