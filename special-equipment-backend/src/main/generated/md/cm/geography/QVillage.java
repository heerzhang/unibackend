package md.cm.geography;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVillage is a Querydsl query type for Village
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QVillage extends EntityPathBase<Village> {

    private static final long serialVersionUID = -411569267L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVillage village = new QVillage("village");

    public final QAdminunit ad;

    public final SetPath<Address, QAddress> adrs = this.<Address, QAddress>createSet("adrs", Address.class, QAddress.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public QVillage(String variable) {
        this(Village.class, forVariable(variable), INITS);
    }

    public QVillage(Path<? extends Village> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVillage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVillage(PathMetadata metadata, PathInits inits) {
        this(Village.class, metadata, inits);
    }

    public QVillage(Class<? extends Village> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ad = inits.isInitialized("ad") ? new QAdminunit(forProperty("ad"), inits.get("ad")) : null;
    }

}

