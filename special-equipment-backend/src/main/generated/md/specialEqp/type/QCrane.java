package md.specialEqp.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrane is a Querydsl query type for Crane
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCrane extends EntityPathBase<Crane> {

    private static final long serialVersionUID = -1014690271L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrane crane = new QCrane("crane");

    public final md.specialEqp.QEqp _super;

    //inherited
    public final DatePath<java.util.Date> cand;

    public final NumberPath<Float> cap = createNumber("cap", Float.class);

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

    public final BooleanPath cotr = createBoolean("cotr");

    //inherited
    public final NumberPath<Byte> cpa;

    public final StringPath cpi = createString("cpi");

    //inherited
    public final BooleanPath cping;

    public final StringPath cpm = createString("cpm");

    public final NumberPath<Float> cvl = createNumber("cvl", Float.class);

    //inherited
    public final DatePath<java.util.Date> did1;

    //inherited
    public final DatePath<java.util.Date> did2;

    //inherited
    public final DatePath<java.util.Date> expire;

    public final NumberPath<Short> flo = createNumber("flo", Short.class);

    //inherited
    public final StringPath fno;

    public final BooleanPath grab = createBoolean("grab");

    public final NumberPath<Float> high = createNumber("high", Float.class);

    public final NumberPath<Float> hlf = createNumber("hlf", Float.class);

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final NumberPath<Byte> impt;

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

    public final StringPath jobl = createString("jobl");

    //inherited
    public final StringPath level;

    public final NumberPath<Float> lmv = createNumber("lmv", Float.class);

    //inherited
    public final StringPath lpho;

    public final StringPath luf = createString("luf");

    public final NumberPath<Float> luff = createNumber("luff", Float.class);

    // inherited
    public final md.cm.unit.QUnit makeu;

    public final BooleanPath metl = createBoolean("metl");

    public final NumberPath<Float> miot = createNumber("miot", Float.class);

    //inherited
    public final DatePath<java.util.Date> mkd;

    //inherited
    public final StringPath model;

    public final NumberPath<Float> mom = createNumber("mom", Float.class);

    //inherited
    public final NumberPath<Float> money;

    //inherited
    public final BooleanPath move;

    // inherited
    public final md.cm.unit.QUnit mtu;

    // inherited
    public final md.cm.unit.QDivision mtud;

    public final NumberPath<Float> mvl = createNumber("mvl", Float.class);

    public final BooleanPath nnor = createBoolean("nnor");

    public final NumberPath<Short> ns = createNumber("ns", Short.class);

    //inherited
    public final DatePath<java.util.Date> nxtd1;

    //inherited
    public final DatePath<java.util.Date> nxtd2;

    //inherited
    public final BooleanPath ocat;

    public final StringPath occa = createString("occa");

    //inherited
    public final StringPath oid;

    public final StringPath opm = createString("opm");

    // inherited
    public final md.cm.unit.QUnit owner;

    //inherited
    public final StringPath pa;

    public final StringPath part = createString("part");

    public final StringPath pcs = createString("pcs");

    public final NumberPath<Float> pcw = createNumber("pcw", Float.class);

    //inherited
    public final StringPath plno;

    public final NumberPath<Short> pnum = createNumber("pnum", Short.class);

    // inherited
    public final md.cm.geography.QAddress pos;

    public final NumberPath<Float> rang = createNumber("rang", Float.class);

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

    public final NumberPath<Float> rtv = createNumber("rtv", Float.class);

    public final NumberPath<Float> rvl = createNumber("rvl", Float.class);

    public final NumberPath<Float> scv = createNumber("scv", Float.class);

    //inherited
    public final StringPath sno;

    //inherited
    public final StringPath sort;

    public final NumberPath<Float> span = createNumber("span", Float.class);

    //inherited
    public final StringPath subv;

    public final BooleanPath suck = createBoolean("suck");

    //inherited
    public final StringPath svp;

    // inherited
    public final md.cm.unit.QUnit svu;

    //inherited
    public final SetPath<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask> task;

    public final StringPath tm = createString("tm");

    public final StringPath tno = createString("tno");

    public final BooleanPath two = createBoolean("two");

    public final BooleanPath twoc = createBoolean("twoc");

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

    public final NumberPath<Float> vl = createNumber("vl", Float.class);

    public final BooleanPath walk = createBoolean("walk");

    public final BooleanPath whole = createBoolean("whole");

    public QCrane(String variable) {
        this(Crane.class, forVariable(variable), INITS);
    }

    public QCrane(Path<? extends Crane> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrane(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrane(PathMetadata metadata, PathInits inits) {
        this(Crane.class, metadata, inits);
    }

    public QCrane(Class<? extends Crane> type, PathMetadata metadata, PathInits inits) {
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

