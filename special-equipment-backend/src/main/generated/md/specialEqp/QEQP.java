package md.specialEqp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEqp is a Querydsl query type for Eqp
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEqp extends EntityPathBase<Eqp> {

    private static final long serialVersionUID = 1147490232L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEqp eqp = new QEqp("eqp");

    public final DateTimePath<java.util.Date> accpDt = createDateTime("accpDt", java.util.Date.class);

    public final NumberPath<Byte> cag = createNumber("cag", Byte.class);

    public final StringPath ccl1 = createString("ccl1");

    public final StringPath ccl2 = createString("ccl2");

    public final StringPath cert = createString("cert");

    public final StringPath cod = createString("cod");

    public final StringPath contact = createString("contact");

    public final BooleanPath cping = createBoolean("cping");

    public final NumberPath<Float> ePrice = createNumber("ePrice", Float.class);

    public final DateTimePath<java.util.Date> expire = createDateTime("expire", java.util.Date.class);

    public final StringPath fNo = createString("fNo");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath important = createBoolean("important");

    public final md.cm.unit.QUnit insU;

    public final DateTimePath<java.util.Date> ispD1 = createDateTime("ispD1", java.util.Date.class);

    public final DateTimePath<java.util.Date> ispD2 = createDateTime("ispD2", java.util.Date.class);

    public final SetPath<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp> isps = this.<md.specialEqp.inspect.Isp, md.specialEqp.inspect.QIsp>createSet("isps", md.specialEqp.inspect.Isp.class, md.specialEqp.inspect.QIsp.class, PathInits.DIRECT2);

    public final StringPath level = createString("level");

    public final md.cm.unit.QUnit makeU;

    public final StringPath model = createString("model");

    public final BooleanPath move = createBoolean("move");

    public final md.cm.unit.QUnit mtU;

    public final md.cm.unit.QDivision mtud;

    public final StringPath name = createString("name");

    public final DateTimePath<java.util.Date> nxtD1 = createDateTime("nxtD1", java.util.Date.class);

    public final DateTimePath<java.util.Date> nxtD2 = createDateTime("nxtD2", java.util.Date.class);

    public final StringPath occasion = createString("occasion");

    public final StringPath oid = createString("oid");

    public final md.cm.unit.QUnit owner;

    public final StringPath pa = createString("pa");

    public final StringPath plNo = createString("plNo");

    public final md.cm.geography.QAddress pos;

    public final StringPath rcod = createString("rcod");

    public final NumberPath<Byte> reg = createNumber("reg", Byte.class);

    public final md.cm.unit.QUnit regU;

    public final md.cm.unit.QUnit remU;

    public final StringPath safe = createString("safe");

    public final StringPath sNo = createString("sNo");

    public final StringPath sort = createString("sort");

    public final StringPath subVart = createString("subVart");

    public final SetPath<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask> task = this.<md.specialEqp.inspect.Task, md.specialEqp.inspect.QTask>createSet("task", md.specialEqp.inspect.Task.class, md.specialEqp.inspect.QTask.class, PathInits.DIRECT2);

    public final StringPath type = createString("type");

    public final BooleanPath unqf1 = createBoolean("unqf1");

    public final BooleanPath unqf2 = createBoolean("unqf2");

    public final DateTimePath<java.util.Date> useDt = createDateTime("useDt", java.util.Date.class);

    public final md.cm.unit.QUnit useU;

    public final NumberPath<Byte> ust = createNumber("ust", Byte.class);

    public final md.cm.unit.QDivision usud;

    public final BooleanPath valid = createBoolean("valid");

    public final StringPath vart = createString("vart");

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

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
        this.insU = inits.isInitialized("insU") ? new md.cm.unit.QUnit(forProperty("insU"), inits.get("insU")) : null;
        this.makeU = inits.isInitialized("makeU") ? new md.cm.unit.QUnit(forProperty("makeU"), inits.get("makeU")) : null;
        this.mtU = inits.isInitialized("mtU") ? new md.cm.unit.QUnit(forProperty("mtU"), inits.get("mtU")) : null;
        this.mtud = inits.isInitialized("mtud") ? new md.cm.unit.QDivision(forProperty("mtud"), inits.get("mtud")) : null;
        this.owner = inits.isInitialized("owner") ? new md.cm.unit.QUnit(forProperty("owner"), inits.get("owner")) : null;
        this.pos = inits.isInitialized("pos") ? new md.cm.geography.QAddress(forProperty("pos"), inits.get("pos")) : null;
        this.regU = inits.isInitialized("regU") ? new md.cm.unit.QUnit(forProperty("regU"), inits.get("regU")) : null;
        this.remU = inits.isInitialized("remU") ? new md.cm.unit.QUnit(forProperty("remU"), inits.get("remU")) : null;
        this.useU = inits.isInitialized("useU") ? new md.cm.unit.QUnit(forProperty("useU"), inits.get("useU")) : null;
        this.usud = inits.isInitialized("usud") ? new md.cm.unit.QDivision(forProperty("usud"), inits.get("usud")) : null;
    }

}

