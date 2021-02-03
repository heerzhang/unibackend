package md.specialEqp.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVessel is a Querydsl query type for Vessel
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QVessel extends EntityPathBase<Vessel> {

    private static final long serialVersionUID = -858138082L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVessel vessel = new QVessel("vessel");

    public final md.specialEqp.QEqp _super;

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

    public final NumberPath<Float> fulw = createNumber("fulw", Float.class);

    public final NumberPath<Float> high = createNumber("high", Float.class);

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final NumberPath<Byte> impt;

    // inherited
    public final md.cm.unit.QUnit insu;

    public final StringPath insul = createString("insul");

    //inherited
    public final DatePath<java.util.Date> ispd1;

    //inherited
    public final DatePath<java.util.Date> ispd2;

    //inherited
    public final SetPath<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp> isps;

    // inherited
    public final md.cm.unit.QUnit ispu;

    public final StringPath jakm = createString("jakm");

    //inherited
    public final StringPath level;

    //inherited
    public final StringPath lpho;

    // inherited
    public final md.cm.unit.QUnit makeu;

    public final StringPath mdi = createString("mdi");

    //inherited
    public final DatePath<java.util.Date> mkd;

    //inherited
    public final StringPath model;

    //inherited
    public final NumberPath<Float> money;

    public final StringPath mont = createString("mont");

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

    public final StringPath plat = createString("plat");

    //inherited
    public final StringPath plno;

    public final NumberPath<Short> pnum = createNumber("pnum", Short.class);

    // inherited
    public final md.cm.geography.QAddress pos;

    public final StringPath prs = createString("prs");

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

    public final NumberPath<Float> rtl = createNumber("rtl", Float.class);

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

    public final StringPath vol = createString("vol");

    public final NumberPath<Float> weig = createNumber("weig", Float.class);

    public QVessel(String variable) {
        this(Vessel.class, forVariable(variable), INITS);
    }

    public QVessel(Path<? extends Vessel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVessel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVessel(PathMetadata metadata, PathInits inits) {
        this(Vessel.class, metadata, inits);
    }

    public QVessel(Class<? extends Vessel> type, PathMetadata metadata, PathInits inits) {
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

