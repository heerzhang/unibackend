package md.cm.unit;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDivision is a Querydsl query type for Division
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDivision extends EntityPathBase<Division> {

    private static final long serialVersionUID = -1108590870L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDivision division = new QDivision("division");

    public final StringPath address = createString("address");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkMen = createString("linkMen");

    public final SetPath<md.specialEqp.Eqp, md.specialEqp.QEqp> maints = this.<md.specialEqp.Eqp, md.specialEqp.QEqp>createSet("maints", md.specialEqp.Eqp.class, md.specialEqp.QEqp.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final md.cm.geography.QAddress pos;

    public final QUnit unit;

    public final SetPath<md.specialEqp.Eqp, md.specialEqp.QEqp> uses = this.<md.specialEqp.Eqp, md.specialEqp.QEqp>createSet("uses", md.specialEqp.Eqp.class, md.specialEqp.QEqp.class, PathInits.DIRECT2);

    public QDivision(String variable) {
        this(Division.class, forVariable(variable), INITS);
    }

    public QDivision(Path<? extends Division> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDivision(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDivision(PathMetadata metadata, PathInits inits) {
        this(Division.class, metadata, inits);
    }

    public QDivision(Class<? extends Division> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pos = inits.isInitialized("pos") ? new md.cm.geography.QAddress(forProperty("pos"), inits.get("pos")) : null;
        this.unit = inits.isInitialized("unit") ? new QUnit(forProperty("unit"), inits.get("unit")) : null;
    }

}

