package md.specialEqp;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.*;


/**
 * QEqp is a Querydsl query type for Eqp
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEqp extends EntityPathBase<Eqp> {

    private static final long serialVersionUID = 1147490232L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEqp eqp = new QEqp("eqp");

    public final DatePath<java.util.Date> cand = createDate("cand", java.util.Date.class);

    public final StringPath ccl1 = createString("ccl1");

    public final StringPath ccl2 = createString("ccl2");

    public final StringPath cert = createString("cert");

    public final StringPath cnam = createString("cnam");

    public final StringPath cod = createString("cod");

    public final NumberPath<Byte> cpa = createNumber("cpa", Byte.class);

    public final BooleanPath cping = createBoolean("cping");

    public final DatePath<java.util.Date> did1 = createDate("did1", java.util.Date.class);

    public final DatePath<java.util.Date> did2 = createDate("did2", java.util.Date.class);

    public final DatePath<java.util.Date> expire = createDate("expire", java.util.Date.class);

    public final StringPath fno = createString("fno");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Byte> impt = createNumber("impt", Byte.class);

    public final DatePath<java.util.Date> insd = createDate("insd", java.util.Date.class);

    public final md.cm.unit.QUnit insu;

    public final DatePath<java.util.Date> ispd1 = createDate("ispd1", java.util.Date.class);

    public final DatePath<java.util.Date> ispd2 = createDate("ispd2", java.util.Date.class);

    public final SetPath<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp> isps = this.<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp>createSet("isps", md.specialEqp.inspect.Isp.class, md.specialEqp.inspect.QIsp.class, PathInits.DIRECT2);

    public final md.cm.unit.QUnit ispu;

    public final StringPath level = createString("level");

    public final StringPath lpho = createString("lpho");

    public final md.cm.unit.QUnit makeu;

    public final DatePath<java.util.Date> mkd = createDate("mkd", java.util.Date.class);

    public final StringPath model = createString("model");

    public final NumberPath<Float> money = createNumber("money", Float.class);

    public final BooleanPath move = createBoolean("move");

    public final md.cm.unit.QUnit mtu;

    public final md.cm.unit.QDivision mtud;

    public final DatePath<java.util.Date> nxtd1 = createDate("nxtd1", java.util.Date.class);

    public final DatePath<java.util.Date> nxtd2 = createDate("nxtd2", java.util.Date.class);

    public final BooleanPath ocat = createBoolean("ocat");

    public final StringPath oid = createString("oid");

    public final md.cm.unit.QUnit owner;

    public final StringPath pa = createString("pa");

    public final StringPath plno = createString("plno");

    public final md.cm.geography.QAddress pos;

    public final StringPath rcod = createString("rcod");

    public final EnumPath<RegState_Enum> reg = createEnum("reg", RegState_Enum.class);

    public final DatePath<java.util.Date> regd = createDate("regd", java.util.Date.class);

    public final md.cm.unit.QUnit regu;

    public final md.cm.unit.QUnit remu;

    public final md.cm.unit.QUnit repu;

    public final StringPath rnam = createString("rnam");

    public final StringPath sno = createString("sno");

    public final StringPath sort = createString("sort");

    public final StringPath subv = createString("subv");

    public final StringPath svp = createString("svp");

    public final md.cm.unit.QUnit svu;

    public final StringPath titl = createString("titl");

    public final StringPath type = createString("type");

    public final BooleanPath unqf1 = createBoolean("unqf1");

    public final BooleanPath unqf2 = createBoolean("unqf2");

    public final DatePath<java.util.Date> used = createDate("used", java.util.Date.class);

    public final md.cm.unit.QUnit useu;

    public final EnumPath<UseState_Enum> ust = createEnum("ust", UseState_Enum.class);

    public final md.cm.unit.QDivision usud;

    public final StringPath vart = createString("vart");

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public final BooleanPath vital = createBoolean("vital");

    public QEqp(String variable) {
        this(Eqp.class, forVariable(variable), INITS);
    }

    public QEqp(Path<? extends Eqp> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEqp(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEqp(PathMetadata metadata, PathInits inits) {
        this(Eqp.class, metadata, inits);
    }

    public QEqp(Class<? extends Eqp> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.insu = inits.isInitialized("insu") ? new md.cm.unit.QUnit(forProperty("insu"), inits.get("insu")) : null;
        this.ispu = inits.isInitialized("ispu") ? new md.cm.unit.QUnit(forProperty("ispu"), inits.get("ispu")) : null;
        this.makeu = inits.isInitialized("makeu") ? new md.cm.unit.QUnit(forProperty("makeu"), inits.get("makeu")) : null;
        this.mtu = inits.isInitialized("mtu") ? new md.cm.unit.QUnit(forProperty("mtu"), inits.get("mtu")) : null;
        this.mtud = inits.isInitialized("mtud") ? new md.cm.unit.QDivision(forProperty("mtud"), inits.get("mtud")) : null;
        this.owner = inits.isInitialized("owner") ? new md.cm.unit.QUnit(forProperty("owner"), inits.get("owner")) : null;
        this.pos = inits.isInitialized("pos") ? new md.cm.geography.QAddress(forProperty("pos"), inits.get("pos")) : null;
        this.regu = inits.isInitialized("regu") ? new md.cm.unit.QUnit(forProperty("regu"), inits.get("regu")) : null;
        this.remu = inits.isInitialized("remu") ? new md.cm.unit.QUnit(forProperty("remu"), inits.get("remu")) : null;
        this.repu = inits.isInitialized("repu") ? new md.cm.unit.QUnit(forProperty("repu"), inits.get("repu")) : null;
        this.svu = inits.isInitialized("svu") ? new md.cm.unit.QUnit(forProperty("svu"), inits.get("svu")) : null;
        this.useu = inits.isInitialized("useu") ? new md.cm.unit.QUnit(forProperty("useu"), inits.get("useu")) : null;
        this.usud = inits.isInitialized("usud") ? new md.cm.unit.QDivision(forProperty("usud"), inits.get("usud")) : null;
    }

}

