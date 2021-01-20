package md.specialEqp.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPipeline is a Querydsl query type for Pipeline
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPipeline extends EntityPathBase<Pipeline> {

    private static final long serialVersionUID = 1547503754L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPipeline pipeline = new QPipeline("pipeline");

    public final md.specialEqp.QEqp _super;

    //inherited
    public final DateTimePath<java.util.Date> accd;

    //inherited
    public final StringPath ccl1;

    //inherited
    public final StringPath ccl2;

    public final SetPath<PipingUnit, QPipingUnit> cells = this.<PipingUnit, QPipingUnit>createSet("cells", PipingUnit.class, QPipingUnit.class, PathInits.DIRECT2);

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

    public final StringPath matr = createString("matr");

    public final StringPath mdi = createString("mdi");

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
    public final StringPath name;

    //inherited
    public final DateTimePath<java.util.Date> nxtd1;

    //inherited
    public final DateTimePath<java.util.Date> nxtd2;

    //inherited
    public final BooleanPath ocat;

    //inherited
    public final StringPath occa;

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

    public final StringPath prs = createString("prs");

    public final StringPath pttl = createString("pttl");

    //inherited
    public final StringPath rcod;

    //inherited
    public final EnumPath<md.specialEqp.RegState_Enum> reg;

    // inherited
    public final md.cm.unit.QUnit regu;

    // inherited
    public final md.cm.unit.QUnit remu;

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

    public final StringPath temp = createString("temp");

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
    public final EnumPath<md.specialEqp.UseState_Enum> ust;

    // inherited
    public final md.cm.unit.QDivision usud;

    //inherited
    public final StringPath vart;

    //inherited
    public final NumberPath<Integer> version;

    //inherited
    public final BooleanPath vital;

    public QPipeline(String variable) {
        this(Pipeline.class, forVariable(variable), INITS);
    }

    public QPipeline(Path<? extends Pipeline> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPipeline(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPipeline(PathMetadata metadata, PathInits inits) {
        this(Pipeline.class, metadata, inits);
    }

    public QPipeline(Class<? extends Pipeline> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new md.specialEqp.QEqp(type, metadata, inits);
        this.accd = _super.accd;
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
        this.ocat = _super.ocat;
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
        this.vart = _super.vart;
        this.version = _super.version;
        this.vital = _super.vital;
    }

}

