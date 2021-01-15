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

    public final md.specialEqp.QEqp _super;

    //inherited
    public final DateTimePath<java.util.Date> accd;

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
    public final BooleanPath cping;

    //inherited
    public final DateTimePath<java.util.Date> expire;

    //inherited
    public final StringPath fno;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final md.cm.unit.QUnit insu;

    //inherited
    public final DateTimePath<java.util.Date> ispd1;

    //inherited
    public final DateTimePath<java.util.Date> ispd2;

    //inherited
    public final SetPath<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp> isps;

    //inherited
    public final StringPath level;

    // inherited
    public final md.cm.unit.QUnit makeu;

    //inherited
    public final StringPath model;

    //inherited
    public final NumberPath<Float> money;

    //inherited
    public final BooleanPath move;

    public final StringPath mtm = createString("mtm");

    // inherited
    public final md.cm.unit.QUnit mtu;

    // inherited
    public final md.cm.unit.QDivision mtud;

    //inherited
    public final StringPath name;

    //inherited
    public final DateTimePath<java.util.Date> nxtd1;

    //inherited
    public final DateTimePath<java.util.Date> nxtd2;

    //inherited
    public final StringPath occa;

    //inherited
    public final StringPath oid;

    // inherited
    public final md.cm.unit.QUnit owner;

    //inherited
    public final StringPath pa;

    public final StringPath plat = createString("plat");

    //inherited
    public final StringPath plno;

    // inherited
    public final md.cm.geography.QAddress pos;

    public final StringPath pow = createString("pow");

    //inherited
    public final StringPath rcod;

    //inherited
    public final NumberPath<Byte> reg;

    // inherited
    public final md.cm.unit.QUnit regu;

    // inherited
    public final md.cm.unit.QUnit remu;

    public final NumberPath<Float> rtl = createNumber("rtl", Float.class);

    //inherited
    public final StringPath safe;

    //inherited
    public final StringPath sno;

    //inherited
    public final StringPath sort;

    //inherited
    public final StringPath subv;

    //inherited
    public final StringPath svp;

    // inherited
    public final md.cm.unit.QUnit svu;

    //inherited
    public final SetPath<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask> task;

    //inherited
    public final StringPath type;

    //inherited
    public final BooleanPath unqf1;

    //inherited
    public final BooleanPath unqf2;

    //inherited
    public final DateTimePath<java.util.Date> used;

    // inherited
    public final md.cm.unit.QUnit useu;

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

    //inherited
    public final BooleanPath vital;

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
        this._super = new md.specialEqp.QEqp(type, metadata, inits);
        this.accd = _super.accd;
        this.cag = _super.cag;
        this.ccl1 = _super.ccl1;
        this.ccl2 = _super.ccl2;
        this.cert = _super.cert;
        this.cod = _super.cod;
        this.cping = _super.cping;
        this.expire = _super.expire;
        this.fno = _super.fno;
        this.id = _super.id;
        this.insu = _super.insu;
        this.ispd1 = _super.ispd1;
        this.ispd2 = _super.ispd2;
        this.isps = _super.isps;
        this.level = _super.level;
        this.makeu = _super.makeu;
        this.model = _super.model;
        this.money = _super.money;
        this.move = _super.move;
        this.mtu = _super.mtu;
        this.mtud = _super.mtud;
        this.name = _super.name;
        this.nxtd1 = _super.nxtd1;
        this.nxtd2 = _super.nxtd2;
        this.occa = _super.occa;
        this.oid = _super.oid;
        this.owner = _super.owner;
        this.pa = _super.pa;
        this.plno = _super.plno;
        this.pos = _super.pos;
        this.rcod = _super.rcod;
        this.reg = _super.reg;
        this.regu = _super.regu;
        this.remu = _super.remu;
        this.safe = _super.safe;
        this.sno = _super.sno;
        this.sort = _super.sort;
        this.subv = _super.subv;
        this.svp = _super.svp;
        this.svu = _super.svu;
        this.task = _super.task;
        this.type = _super.type;
        this.unqf1 = _super.unqf1;
        this.unqf2 = _super.unqf2;
        this.used = _super.used;
        this.useu = _super.useu;
        this.ust = _super.ust;
        this.usud = _super.usud;
        this.valid = _super.valid;
        this.vart = _super.vart;
        this.version = _super.version;
        this.vital = _super.vital;
    }

}

