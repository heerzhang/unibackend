package org.fjsei.yewu.entity.sdn;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTEbmUser is a Querydsl query type for TEbmUser
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTEbmUser extends EntityPathBase<TEbmUser> {

    private static final long serialVersionUID = 1374640868L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTEbmUser tEbmUser = new QTEbmUser("tEbmUser");

    public final StringPath certcode = createString("certcode");

    public final StringPath loginName = createString("loginName");

    public final StringPath password = createString("password");

    public final StringPath portal = createString("portal");

    public final StringPath status = createString("status");

    public final QTSdnEnp tsdnenp;

    public final StringPath type = createString("type");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userName = createString("userName");

    public final StringPath userType = createString("userType");

    public QTEbmUser(String variable) {
        this(TEbmUser.class, forVariable(variable), INITS);
    }

    public QTEbmUser(Path<? extends TEbmUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTEbmUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTEbmUser(PathMetadata metadata, PathInits inits) {
        this(TEbmUser.class, metadata, inits);
    }

    public QTEbmUser(Class<? extends TEbmUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tsdnenp = inits.isInitialized("tsdnenp") ? new QTSdnEnp(forProperty("tsdnenp"), inits.get("tsdnenp")) : null;
    }

}

