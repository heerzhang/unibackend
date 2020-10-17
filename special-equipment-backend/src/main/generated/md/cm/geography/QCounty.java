package md.cm.geography;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCounty is a Querydsl query type for County
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCounty extends EntityPathBase<County> {

    private static final long serialVersionUID = -1382702519L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCounty county = new QCounty("county");

    public final SetPath<Adminunit, QAdminunit> ads = this.<Adminunit, QAdminunit>createSet("ads", Adminunit.class, QAdminunit.class, PathInits.DIRECT2);

    public final QCity city;

    public final BooleanPath collapse = createBoolean("collapse");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final SetPath<Town, QTown> towns = this.<Town, QTown>createSet("towns", Town.class, QTown.class, PathInits.DIRECT2);

    public QCounty(String variable) {
        this(County.class, forVariable(variable), INITS);
    }

    public QCounty(Path<? extends County> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCounty(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCounty(PathMetadata metadata, PathInits inits) {
        this(County.class, metadata, inits);
    }

    public QCounty(Class<? extends County> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.city = inits.isInitialized("city") ? new QCity(forProperty("city"), inits.get("city")) : null;
    }

}

