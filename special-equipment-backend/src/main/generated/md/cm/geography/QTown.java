package md.cm.geography;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTown is a Querydsl query type for Town
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTown extends EntityPathBase<Town> {

    private static final long serialVersionUID = -1699254447L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTown town = new QTown("town");

    public final QAdminunit ads;

    public final QCounty county;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> oldId = createNumber("oldId", Long.class);

    public QTown(String variable) {
        this(Town.class, forVariable(variable), INITS);
    }

    public QTown(Path<? extends Town> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTown(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTown(PathMetadata metadata, PathInits inits) {
        this(Town.class, metadata, inits);
    }

    public QTown(Class<? extends Town> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ads = inits.isInitialized("ads") ? new QAdminunit(forProperty("ads"), inits.get("ads")) : null;
        this.county = inits.isInitialized("county") ? new QCounty(forProperty("county"), inits.get("county")) : null;
    }

}

