package md.specialEqp.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFactoryVehicle is a Querydsl query type for FactoryVehicle
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFactoryVehicle extends EntityPathBase<FactoryVehicle> {

    private static final long serialVersionUID = 1640621738L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFactoryVehicle factoryVehicle = new QFactoryVehicle("factoryVehicle");

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

    public QFactoryVehicle(String variable) {
        this(FactoryVehicle.class, forVariable(variable), INITS);
    }

    public QFactoryVehicle(Path<? extends FactoryVehicle> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFactoryVehicle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFactoryVehicle(PathMetadata metadata, PathInits inits) {
        this(FactoryVehicle.class, metadata, inits);
    }

    public QFactoryVehicle(Class<? extends FactoryVehicle> type, PathMetadata metadata, PathInits inits) {
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

