package md.julienne;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFollowing is a Querydsl query type for Following
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFollowing extends EntityPathBase<Following> {

    private static final long serialVersionUID = 1127247330L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFollowing following = new QFollowing("following");

    public final BooleanPath confirmed = createBoolean("confirmed");

    public final md.system.QUser fromUser;

    public final md.system.QUser toUser;

    public QFollowing(String variable) {
        this(Following.class, forVariable(variable), INITS);
    }

    public QFollowing(Path<? extends Following> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFollowing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFollowing(PathMetadata metadata, PathInits inits) {
        this(Following.class, metadata, inits);
    }

    public QFollowing(Class<? extends Following> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fromUser = inits.isInitialized("fromUser") ? new md.system.QUser(forProperty("fromUser")) : null;
        this.toUser = inits.isInitialized("toUser") ? new md.system.QUser(forProperty("toUser")) : null;
    }

}

