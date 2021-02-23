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

    public final StringPath bmod = createString("bmod");

    //inherited
    public final DatePath<java.util.Date> cand;

    //inherited
    public final StringPath ccl1;

    //inherited
    public final StringPath ccl2;

    //inherited
    public final StringPath cert;

    //inherited
    public final StringPath cnam;

    //inherited
    public final StringPath cod;

    //inherited
    public final NumberPath<Byte> cpa;

    //inherited
    public final BooleanPath cping;

    //inherited
    public final DatePath<java.util.Date> did1;

    //inherited
    public final DatePath<java.util.Date> did2;

    //inherited
    public final DatePath<java.util.Date> expire;

    //inherited
    public final StringPath fno;

    public final StringPath form = createString("form");

    public final StringPath fuel = createString("fuel");

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final NumberPath<Byte> impt;

    //inherited
    public final DatePath<java.util.Date> insd;

    // inherited
    public final md.cm.unit.QUnit insu;

    //inherited
    public final DatePath<java.util.Date> ispd1;

    //inherited
    public final DatePath<java.util.Date> ispd2;

    //inherited
    public final SetPath<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp> isps;

    // inherited
    public final md.cm.unit.QUnit ispu;

    //inherited
    public final StringPath level;

    //inherited
    public final StringPath lpho;

    // inherited
    public final md.cm.unit.QUnit makeu;

    //inherited
    public final DatePath<java.util.Date> mkd;

    //inherited
    public final StringPath model;

    //inherited
    public final NumberPath<Float> money;

    //inherited
    public final BooleanPath move;

    // inherited
    public final md.cm.unit.QUnit mtu;

    // inherited
    public final md.cm.unit.QDivision mtud;

    //inherited
    public final DatePath<java.util.Date> nxtd1;

    //inherited
    public final DatePath<java.util.Date> nxtd2;

    //inherited
    public final BooleanPath ocat;

    //inherited
    public final StringPath oid;

    // inherited
    public final md.cm.unit.QUnit owner;

    //inherited
    public final StringPath pa;

    //inherited
    public final StringPath plno;

    // inherited
    public final md.cm.geography.QAddress pos;

    public final NumberPath<Float> power = createNumber("power", Float.class);

    public final NumberPath<Float> pres = createNumber("pres", Float.class);

    //inherited
    public final StringPath rcod;

    //inherited
    public final EnumPath<md.specialEqp.RegState_Enum> reg;

    //inherited
    public final DatePath<java.util.Date> regd;

    // inherited
    public final md.cm.unit.QUnit regu;

    // inherited
    public final md.cm.unit.QUnit remu;

    // inherited
    public final md.cm.unit.QUnit repu;

    //inherited
    public final StringPath rnam;

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
    public final StringPath titl;

    //inherited
    public final StringPath type;

    //inherited
    public final BooleanPath unqf1;

    //inherited
    public final BooleanPath unqf2;

    //inherited
    public final DatePath<java.util.Date> used;

    // inherited
    public final md.cm.unit.QUnit useu;

    //inherited
    public final EnumPath<md.specialEqp.UseState_Enum> ust;

    // inherited
    public final md.cm.unit.QDivision usud;

    //inherited
    public final StringPath vart;

    //inherited
    public final NumberPath<Integer> version;

    //inherited
    public final BooleanPath vital;

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
        this.cand = _super.cand;
        this.ccl1 = _super.ccl1;
        this.ccl2 = _super.ccl2;
        this.cert = _super.cert;
        this.cnam = _super.cnam;
        this.cod = _super.cod;
        this.cpa = _super.cpa;
        this.cping = _super.cping;
        this.did1 = _super.did1;
        this.did2 = _super.did2;
        this.expire = _super.expire;
        this.fno = _super.fno;
        this.id = _super.id;
        this.impt = _super.impt;
        this.insd = _super.insd;
        this.insu = _super.insu;
        this.ispd1 = _super.ispd1;
        this.ispd2 = _super.ispd2;
        this.isps = _super.isps;
        this.ispu = _super.ispu;
        this.level = _super.level;
        this.lpho = _super.lpho;
        this.makeu = _super.makeu;
        this.mkd = _super.mkd;
        this.model = _super.model;
        this.money = _super.money;
        this.move = _super.move;
        this.mtu = _super.mtu;
        this.mtud = _super.mtud;
        this.nxtd1 = _super.nxtd1;
        this.nxtd2 = _super.nxtd2;
        this.ocat = _super.ocat;
        this.oid = _super.oid;
        this.owner = _super.owner;
        this.pa = _super.pa;
        this.plno = _super.plno;
        this.pos = _super.pos;
        this.rcod = _super.rcod;
        this.reg = _super.reg;
        this.regd = _super.regd;
        this.regu = _super.regu;
        this.remu = _super.remu;
        this.repu = _super.repu;
        this.rnam = _super.rnam;
        this.sno = _super.sno;
        this.sort = _super.sort;
        this.subv = _super.subv;
        this.svp = _super.svp;
        this.svu = _super.svu;
        this.task = _super.task;
        this.titl = _super.titl;
        this.type = _super.type;
        this.unqf1 = _super.unqf1;
        this.unqf2 = _super.unqf2;
        this.used = _super.used;
        this.useu = _super.useu;
        this.ust = _super.ust;
        this.usud = _super.usud;
        this.vart = _super.vart;
        this.version = _super.version;
        this.vital = _super.vital;
    }

}

