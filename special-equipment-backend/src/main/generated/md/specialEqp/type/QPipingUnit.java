package md.specialEqp.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPipingUnit is a Querydsl query type for PipingUnit
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPipingUnit extends EntityPathBase<PipingUnit> {

    private static final long serialVersionUID = 402149111L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPipingUnit pipingUnit = new QPipingUnit("pipingUnit");

    public final StringPath code = createString("code");

    public final NumberPath<Float> dia = createNumber("dia", Float.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lay = createString("lay");

    public final NumberPath<Float> leng = createNumber("leng", Float.class);

    public final StringPath level = createString("level");

    public final StringPath matr = createString("matr");

    public final StringPath mdi = createString("mdi");

    public final StringPath name = createString("name");

    public final DateTimePath<java.util.Date> nxtd1 = createDateTime("nxtd1", java.util.Date.class);

    public final DateTimePath<java.util.Date> nxtd2 = createDateTime("nxtd2", java.util.Date.class);

    public final StringPath pa = createString("pa");

    public final QPipeline pipe;

    public final NumberPath<Byte> reg = createNumber("reg", Byte.class);

    public final StringPath rno = createString("rno");

    public final StringPath start = createString("start");

    public final StringPath stop = createString("stop");

    public final NumberPath<Float> thik = createNumber("thik", Float.class);

    public final NumberPath<Byte> ust = createNumber("ust", Byte.class);

    public QPipingUnit(String variable) {
        this(PipingUnit.class, forVariable(variable), INITS);
    }

    public QPipingUnit(Path<? extends PipingUnit> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPipingUnit(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPipingUnit(PathMetadata metadata, PathInits inits) {
        this(PipingUnit.class, metadata, inits);
    }

    public QPipingUnit(Class<? extends PipingUnit> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pipe = inits.isInitialized("pipe") ? new QPipeline(forProperty("pipe"), inits.get("pipe")) : null;
    }

}

