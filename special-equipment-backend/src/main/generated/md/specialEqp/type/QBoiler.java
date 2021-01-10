package md.specialEqp.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoiler is a Querydsl query type for Boiler
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBoiler extends EntityPathBase<Boiler> {

    private static final long serialVersionUID = -1421790523L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoiler boiler = new QBoiler("boiler");

    public final md.specialEqp.QEqp _super;

    //inherited
    public final DateTimePath<java.util.Date> accpDt;

    public final StringPath btp = createString("btp");

    //inherited
    public final NumberPath<Byte> cag;

    //inherited
    public final StringPath ccl1;

    //inherited
    public final StringPath ccl2;

    //inherited
    public final StringPath cert;

    //inherited
    public final StringPath cod;

    //inherited
    public final StringPath contact;

    //inherited
    public final BooleanPath cping;

    //inherited
    public final NumberPath<Float> ePrice;

    //inherited
    public final DateTimePath<java.util.Date> expire;

    //inherited
    public final StringPath fNo;

    public final StringPath form = createString("form");

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final BooleanPath important;

    // inherited
    public final md.cm.unit.QUnit insU;

    //inherited
    public final DateTimePath<java.util.Date> ispD1;

    //inherited
    public final DateTimePath<java.util.Date> ispD2;

    //inherited
    public final SetPath<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp> isps;

    //inherited
    public final StringPath level;

    // inherited
    public final md.cm.unit.QUnit makeU;

    //inherited
    public final StringPath model;

    //inherited
    public final BooleanPath move;

    // inherited
    public final md.cm.unit.QUnit mtU;

    // inherited
    public final md.cm.unit.QDivision mtud;

    //inherited
    public final StringPath name;

    //inherited
    public final DateTimePath<java.util.Date> nxtD1;

    //inherited
    public final DateTimePath<java.util.Date> nxtD2;

    //inherited
    public final StringPath occasion;

    //inherited
    public final StringPath oid;

    // inherited
    public final md.cm.unit.QUnit owner;

    //inherited
    public final StringPath pa;

    //inherited
    public final StringPath plNo;

    // inherited
    public final md.cm.geography.QAddress pos;

    public final NumberPath<Float> power = createNumber("power", Float.class);

    //inherited
    public final StringPath rcod;

    //inherited
    public final NumberPath<Byte> reg;

    // inherited
    public final md.cm.unit.QUnit regU;

    // inherited
    public final md.cm.unit.QUnit remU;

    //inherited
    public final StringPath safe;

    //inherited
    public final StringPath sNo;

    //inherited
    public final StringPath sort;

    //inherited
    public final StringPath subVart;

    //inherited
    public final SetPath<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask> task;

    //inherited
    public final StringPath type;

    //inherited
    public final BooleanPath unqf1;

    //inherited
    public final BooleanPath unqf2;

    //inherited
    public final DateTimePath<java.util.Date> useDt;

    // inherited
    public final md.cm.unit.QUnit useU;

    //inherited
    public final NumberPath<Byte> ust;

    // inherited
    public final md.cm.unit.QDivision usud;

    //inherited
    public final BooleanPath valid;

    //inherited
    public final StringPath vart;

    //inherited
    public final NumberPath<Integer> version;

    public final StringPath volume = createString("volume");

    public final BooleanPath wall = createBoolean("wall");

    public QBoiler(String variable) {
        this(Boiler.class, forVariable(variable), INITS);
    }

    public QBoiler(Path<? extends Boiler> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoiler(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoiler(PathMetadata metadata, PathInits inits) {
        this(Boiler.class, metadata, inits);
    }

    public QBoiler(Class<? extends Boiler> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new md.specialEqp.QEqp(type, metadata, inits);
        this.accpDt = _super.accpDt;
        this.cag = _super.cag;
        this.ccl1 = _super.ccl1;
        this.ccl2 = _super.ccl2;
        this.cert = _super.cert;
        this.cod = _super.cod;
        this.contact = _super.contact;
        this.cping = _super.cping;
        this.ePrice = _super.ePrice;
        this.expire = _super.expire;
        this.fNo = _super.fNo;
        this.id = _super.id;
        this.important = _super.important;
        this.insU = _super.insU;
        this.ispD1 = _super.ispD1;
        this.ispD2 = _super.ispD2;
        this.isps = _super.isps;
        this.level = _super.level;
        this.makeU = _super.makeU;
        this.model = _super.model;
        this.move = _super.move;
        this.mtU = _super.mtU;
        this.mtud = _super.mtud;
        this.name = _super.name;
        this.nxtD1 = _super.nxtD1;
        this.nxtD2 = _super.nxtD2;
        this.occasion = _super.occasion;
        this.oid = _super.oid;
        this.owner = _super.owner;
        this.pa = _super.pa;
        this.plNo = _super.plNo;
        this.pos = _super.pos;
        this.rcod = _super.rcod;
        this.reg = _super.reg;
        this.regU = _super.regU;
        this.remU = _super.remU;
        this.safe = _super.safe;
        this.sNo = _super.sNo;
        this.sort = _super.sort;
        this.subVart = _super.subVart;
        this.task = _super.task;
        this.type = _super.type;
        this.unqf1 = _super.unqf1;
        this.unqf2 = _super.unqf2;
        this.useDt = _super.useDt;
        this.useU = _super.useU;
        this.ust = _super.ust;
        this.usud = _super.usud;
        this.valid = _super.valid;
        this.vart = _super.vart;
        this.version = _super.version;
    }

}

