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

    public final NumberPath<Float> dia = createNumber("dia", Float.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lay = createString("lay");

    public final NumberPath<Float> len = createNumber("len", Float.class);

    public final StringPath matr = createString("matr");

    public final QPipeline pipe;

    public final StringPath pipecode = createString("pipecode");

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

